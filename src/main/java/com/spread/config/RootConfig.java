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
@ComponentScan({"com.spread.config.others", "com.spread.fetcher", "com.spread.jobs", "com.spread.data", "com.spread.crawler", "com.spread.experiment"}) 
@PropertySource("classpath:/application.properties")
//@EnableScheduling
public class RootConfig {
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
