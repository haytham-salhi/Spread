package com.spread.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Configuration
@ComponentScan({"com.spread.data"}) 
@PropertySource("classpath:/application.properties")
public class DataTestConfig {
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		System.out.println("This should not be appeared when running the app in tomcat!");
		return new PropertySourcesPlaceholderConfigurer();
	}
}
