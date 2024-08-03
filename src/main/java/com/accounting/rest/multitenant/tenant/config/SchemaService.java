package com.accounting.rest.multitenant.tenant.config;

import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Service;

import com.accounting.rest.multitenant.mastertenant.entity.MasterTenant;
import com.accounting.rest.multitenant.mastertenant.repository.MasterTenantRepository;
import com.zaxxer.hikari.HikariDataSource;

@Service
public class SchemaService {
//	@Autowired
//	private MasterDatabaseConfigProperties masterDbProperties;
	@Autowired
	private MasterTenantRepository masterTenantRepository;
	@Autowired
	private Environment environment;
	public static final String PROPERTY_PREFIX = "spring.datasource.";

	@Bean
	public void createOrUpdateSchema() {
		List<MasterTenant> masterTenants = masterTenantRepository.findAll();
		for (MasterTenant data : masterTenants) {
			LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
			HikariDataSource hikariDataSource = new HikariDataSource();
//			hikariDataSource.setUsername(data.getUserName());
//			hikariDataSource.setPassword(data.getPassword());
			hikariDataSource.setUsername(environment.getProperty("spring.datasource.username"));
			hikariDataSource.setPassword(environment.getProperty("spring.datasource.password"));

//			hikariDataSource.setJdbcUrl(data.getUrl());
			hikariDataSource.setJdbcUrl(
					replaceDatabaseName(environment.getProperty("spring.datasource.url"), data.getDbName()));
			hikariDataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class"));
			hikariDataSource.setPoolName(environment.getProperty("spring.datasource.poolName" + "-connection-pool"));
			// HikariCP settings
			hikariDataSource
					.setMaximumPoolSize(Integer.parseInt(environment.getProperty("spring.datasource.maxPoolSize")));
			hikariDataSource.setMinimumIdle(Integer.parseInt(environment.getProperty("spring.datasource.minIdle")));
			hikariDataSource.setConnectionTimeout(
					Integer.parseInt(environment.getProperty("spring.datasource.connectionTimeout")));
			hikariDataSource.setIdleTimeout(Integer.parseInt(environment.getProperty("spring.datasource.maxPoolSize")));
			DataSource ds = hikariDataSource;

			Properties properties = new Properties();
			properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
			properties.put(AvailableSettings.SHOW_SQL, true);
			properties.put(AvailableSettings.FORMAT_SQL, true);
			properties.put(AvailableSettings.HBM2DDL_AUTO, "update");

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

	public String replaceDatabaseName(String jdbcUrl, String newDatabaseName) {
		// Use replaceFirst with a regex to replace the part between / and ?
		return jdbcUrl.replaceFirst("/[^?]+", "/" + newDatabaseName);
	}

}
