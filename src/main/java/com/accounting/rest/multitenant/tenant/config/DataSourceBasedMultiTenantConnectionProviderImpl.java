package com.accounting.rest.multitenant.tenant.config;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.accounting.rest.multitenant.mastertenant.config.DBContextHolder;
import com.accounting.rest.multitenant.mastertenant.entity.MasterTenant;
import com.accounting.rest.multitenant.mastertenant.repository.MasterTenantRepository;
import com.accounting.rest.multitenant.mastertenant.services.DefaultMultiTanentDataSourceSet;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceBasedMultiTenantConnectionProviderImpl
		extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

	private static final Logger LOG = LoggerFactory.getLogger(DataSourceBasedMultiTenantConnectionProviderImpl.class);

	private static final long serialVersionUID = 1L;

	private Map<String, DataSource> dataSourcesMtApp = new TreeMap<>();

	@Autowired
	private MasterTenantRepository masterTenantRepository;

	@Autowired
	ApplicationContext applicationContext;
//	@Autowired
//	DataSourceUtil dataSourceUtil;
	@Autowired
	private Environment environment;
	@Autowired
	private DefaultMultiTanentDataSourceSet defaultMultiTanentDataSourceSet;

	@Override
	protected DataSource selectAnyDataSource() {

		// This method is called more than once. So check if the data source map
		// is empty. If it is then rescan master_tenant table for all tenant
		if (dataSourcesMtApp.isEmpty()) {
			defaultMultiTanentDataSourceSet.insertDefaultMasterTenant();
			List<MasterTenant> masterTenants = masterTenantRepository.findAll();
			LOG.info("selectAnyDataSource() method call...Total tenants:" + masterTenants.size());
			for (MasterTenant masterTenant : masterTenants) {
				dataSourcesMtApp.put(masterTenant.getDbName(), createAndConfigureDataSource(masterTenant));
			}
		}
		return this.dataSourcesMtApp.values().iterator().next();
	}

	@Override
	protected DataSource selectDataSource(String tenantIdentifier) {

		// If the requested tenant id is not present check for it in the master
		// database 'master_tenant' table
		tenantIdentifier = initializeTenantIfLost(tenantIdentifier);
		if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
			List<MasterTenant> masterTenants = masterTenantRepository.findAll();
			LOG.info("selectDataSource() method call...Tenant:" + tenantIdentifier + " Total tenants:"
					+ masterTenants.size());
			for (MasterTenant masterTenant : masterTenants) {
				dataSourcesMtApp.put(masterTenant.getDbName(), createAndConfigureDataSource(masterTenant));
			}
		}
		// check again if tenant exist in map after rescan master_db, if not, throw
		// UsernameNotFoundException
		if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
			LOG.warn("Trying to get tenant:" + tenantIdentifier + " which was not found in master db after rescan");
			throw new UsernameNotFoundException(
					String.format("Tenant not found after rescan, " + " tenant=%s", tenantIdentifier));
		}
		return this.dataSourcesMtApp.get(tenantIdentifier);
	}

	private String initializeTenantIfLost(String tenantIdentifier) {
		if (tenantIdentifier != DBContextHolder.getCurrentDb()) {
			tenantIdentifier = DBContextHolder.getCurrentDb();
		}
		return tenantIdentifier;
	}

	public DataSource createAndConfigureDataSource(MasterTenant masterTenant) {
		HikariDataSource ds = new HikariDataSource();
		ds.setUsername(environment.getProperty("spring.datasource.username"));
		ds.setPassword(environment.getProperty("spring.datasource.password"));
		ds.setJdbcUrl(replaceDatabaseName(environment.getProperty("spring.datasource.url"), masterTenant.getDbName()));
		ds.setDriverClassName(environment.getProperty("spring.datasource.driver-class"));
		// HikariCP settings - could come from the master_tenant table but
		// hardcoded here for brevity
		// Maximum waiting time for a connection from the pool
		ds.setConnectionTimeout(Integer.parseInt(environment.getProperty("spring.datasource.maxPoolSize")));
		// Minimum number of idle connections in the pool
		ds.setMinimumIdle(Integer.parseInt(environment.getProperty("spring.datasource.minIdle")));
		// Maximum number of actual connection in the pool
		ds.setMaximumPoolSize(Integer.parseInt(environment.getProperty("spring.datasource.maxPoolSize")));
		// Maximum time that a connection is allowed to sit idle in the pool
		ds.setIdleTimeout(Integer.parseInt(environment.getProperty("spring.datasource.connectionTimeout")));
		ds.setConnectionTimeout(Integer.parseInt(environment.getProperty("spring.datasource.connectionTimeout")));
		// Setting up a pool name for each tenant datasource
		String tenantConnectionPoolName = masterTenant.getDbName() + "-connection-pool";
		ds.setPoolName(tenantConnectionPoolName);
		LOG.info("Configured datasource:" + masterTenant.getDbName() + ". Connection pool name:"
				+ tenantConnectionPoolName);
		return ds;
	}

	public static String replaceDatabaseName(String jdbcUrl, String newDatabaseName) {
		// Use replaceFirst with a regex to replace the part between / and ?
		return jdbcUrl.replaceFirst("/[^?]+", "/" + newDatabaseName);
	}
}
