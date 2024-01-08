package com.accounting.rest.multitenant.tenant.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Md. Amran Hossain
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.accounting.rest.multitenant.tenant.repository",
		"com.accounting.rest.multitenant.tenant.entity" })
@EnableJpaRepositories(basePackages = { "com.accounting.rest.multitenant.tenant.repository",
		"com.accounting.rest.multitenant.tenant.service" }, entityManagerFactoryRef = "tenantEntityManagerFactory", transactionManagerRef = "tenantTransactionManager")
public class TenantDatabaseConfig {

	@Bean(name = "tenantJpaVendorAdapter")
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Bean(name = "tenantTransactionManager")
	public JpaTransactionManager transactionManager(
			@Qualifier("tenantEntityManagerFactory") EntityManagerFactory tenantEntityManager) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(tenantEntityManager);
		return transactionManager;
	}

	/**
	 * The multi tenant connection provider
	 *
	 * @return
	 */
	@Bean(name = "datasourceBasedMultitenantConnectionProvider")
	@ConditionalOnBean(name = "masterEntityManagerFactory")
	public MultiTenantConnectionProvider multiTenantConnectionProvider() {
		// Autowires the multi connection provider
		return new DataSourceBasedMultiTenantConnectionProviderImpl();
	}

	/**
	 * The current tenant identifier resolver
	 *
	 * @return
	 */
	@Bean(name = "currentTenantIdentifierResolver")
	public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
		return new CurrentTenantIdentifierResolverImpl();
	}

	/**
	 * Creates the entity manager factory bean which is required to access the JPA
	 * functionalities provided by the JPA persistence provider, i.e. Hibernate in
	 * this case.
	 *
	 * @param connectionProvider
	 * @param tenantResolver
	 * @return
	 */
	@Bean(name = "tenantEntityManagerFactory")
	@ConditionalOnBean(name = "datasourceBasedMultitenantConnectionProvider")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("datasourceBasedMultitenantConnectionProvider") MultiTenantConnectionProvider connectionProvider,
			@Qualifier("currentTenantIdentifierResolver") CurrentTenantIdentifierResolver tenantResolver) {
		LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
		// All tenant related entities, repositories and service classes must be scanned
		emfBean.setPackagesToScan("com.accounting.rest.multitenant.tenant");
		emfBean.setJpaVendorAdapter(jpaVendorAdapter());
		emfBean.setPersistenceUnitName("tenantdb-persistence-unit");
		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.multiTenancy", MultiTenancyStrategy.DATABASE); // Corrected constant
		properties.put("hibernate.multi_tenant_connection_provider", connectionProvider);
		properties.put("hibernate.tenant_identifier_resolver", tenantResolver);
		properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		properties.put("hibernate.show_sql", true);
		properties.put("hibernate.format_sql", true);
		properties.put("hibernate.hbm2ddl.auto", "update");
		emfBean.setJpaPropertyMap(properties);
		return emfBean;
	}
}
