package com.spread.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Configuration
@ComponentScan({"com.spread.config.others", "com.spread.fetcher", "com.spread.jobs"}) 
@PropertySource("classpath:/application.properties")
@EnableScheduling
public class RootConfig {
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
