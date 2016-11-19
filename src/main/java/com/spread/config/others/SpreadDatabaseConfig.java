package com.spread.config.others;

import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Configuration
@EnableJpaRepositories(
		basePackages = { "com.spread.persistence.rds.repository" },
		entityManagerFactoryRef = "spreadEntityManager",
		transactionManagerRef = "spreadTransactionManager")
public class SpreadDatabaseConfig {
	
	private static final Logger logger = LogManager.getLogger(SpreadDatabaseConfig.class);

	@Bean(name = "spreadDataSource")
	public DataSource dataSource(
			@Value("${db.driver.class.name}") String driverClassName,
			@Value("${db.username}") String username,
			@Value("${db.password}") String password,
			@Value("${db.url}") String url) {
		
		logger.info("Building the data source ...");
		logger.info("url=" + url);
		logger.info("username=" + username);
		logger.info("password=" + password);
		
		// This class is not an actual connection pool; it does not actually pool Connections. 
		// It just serves as simple replacement for a full-blown connection pool, 
		// implementing the same standard interface, but creating new Connections on every call.  
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName(driverClassName);
//		dataSource.setUsername(username);
//		dataSource.setPassword(password);
//		dataSource.setUrl(url);
		
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setUrl(url);
		
		dataSource.setInitialSize(5);
		
		try {
			// We can override it of course for this/these connection/s
			logger.info("Transaction Isolation Level of this db: " + dataSource.getConnection().getTransactionIsolation());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		logger.info("Initial size of the pool: " + dataSource.getInitialSize());
		logger.info("Maximum number of active connections that can be allocated at the same time: " + dataSource.getMaxTotal());
		logger.info("Maximum number of connections that can remain idle in the pool: " + dataSource.getMaxIdle());

		return dataSource;
	}
	
	@Bean(name="spreadEntityManager")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("spreadDataSource") DataSource dataSource, 
			Environment env) {
		
		logger.info("Building the entity manager factory bean ...");
		
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource);
		entityManagerFactoryBean.setPackagesToScan("com.spread.persistence.rds.model");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
		entityManagerFactoryBean.setPersistenceUnitName("spreadPU");
		
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
		jpaProperties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
		jpaProperties.put("hibernate.ejb.naming_strategy", env.getRequiredProperty("hibernate.ejb.naming_strategy"));
		jpaProperties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
		jpaProperties.put("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
		// Added this when upgrading to 5.2.3.Final
		//jpaProperties.put("hibernate.id.new_generator_mappings", env.getRequiredProperty("hibernate.id.new_generator_mappings"));

		//jpaProperties.put("hibernate.connection.CharSet", "utf8");
		//jpaProperties.put("hibernate.connection.characterEncoding", "utf8");
		//jpaProperties.put("hibernate.connection.useUnicode", "true");
		entityManagerFactoryBean.setJpaProperties(jpaProperties);

		return entityManagerFactoryBean;
	}
	
	@Bean(name = "spreadTransactionManager")
	public JpaTransactionManager transactionManager(
			@Qualifier("spreadEntityManager") EntityManagerFactory entityManagerFactory) {
		
		logger.info("Building the trasnaction manager ...");
		
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		
		return transactionManager;
	}
}
