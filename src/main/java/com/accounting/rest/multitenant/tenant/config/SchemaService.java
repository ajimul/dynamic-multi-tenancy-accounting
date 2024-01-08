package com.accounting.rest.multitenant.tenant.config;

import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Service;

import com.accounting.rest.multitenant.mastertenant.config.MasterDatabaseConfigProperties;
import com.accounting.rest.multitenant.mastertenant.entity.MasterTenant;
import com.accounting.rest.multitenant.mastertenant.repository.MasterTenantRepository;
import com.zaxxer.hikari.HikariDataSource;

@Service
public class SchemaService {
	@Autowired
	private MasterDatabaseConfigProperties masterDbProperties;
	@Autowired
	private MasterTenantRepository masterTenantRepository;

	@Bean
	public void createOrUpdateSchema() {
		List<MasterTenant> masterTenants = masterTenantRepository.findAll();
		for (MasterTenant data : masterTenants) {
			LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
			HikariDataSource hikariDataSource = new HikariDataSource();
			hikariDataSource.setUsername(data.getUserName());
			hikariDataSource.setPassword(data.getPassword());
			hikariDataSource.setJdbcUrl(data.getUrl());
			hikariDataSource.setDriverClassName(data.getDriverClass());
			hikariDataSource.setPoolName(data.getDbName() + "-connection-pool");
			// HikariCP settings
			hikariDataSource.setMaximumPoolSize(masterDbProperties.getMaxPoolSize());
			hikariDataSource.setMinimumIdle(masterDbProperties.getMinIdle());
			hikariDataSource.setConnectionTimeout(masterDbProperties.getConnectionTimeout());
			hikariDataSource.setIdleTimeout(masterDbProperties.getIdleTimeout());
			DataSource ds = hikariDataSource;

			Properties properties = new Properties();
			properties.put(org.hibernate.cfg.Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
			properties.put(org.hibernate.cfg.Environment.SHOW_SQL, true);
			properties.put(org.hibernate.cfg.Environment.FORMAT_SQL, true);
			properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "update");

			em.setDataSource(ds);
			em.setPackagesToScan("com.accounting.rest.multitenant.tenant");
			em.setPersistenceUnitName(data.getDbName() + "-persistence-unit");
			JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
			em.setJpaVendorAdapter(vendorAdapter);
			em.setJpaProperties(properties);
			// Initialize the EntityManagerFactory
			em.afterPropertiesSet();
			em.destroy();
		}

	}

}
