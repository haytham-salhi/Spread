package com.spread.config.others;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Configuration
@EnableMongoRepositories(basePackages = {"com.spread.persistence.nosql.repository"})
public class MongoConfig extends AbstractMongoConfiguration {
	
	private static final Logger logger = LogManager.getLogger(MongoConfig.class);
	
	@Value("${mongo.database.name}")
	private String databaseName;
	
	@Value("${mongo.database.host.address}")
	private String hostAddress;
	
	@Value("${mongo.database.port}")
	private int portNumber;

	@Override
	protected String getDatabaseName() {
		logger.info("Mongo database: " + databaseName);
		return databaseName;
	}

	@Override
	public Mongo mongo() throws Exception {
		logger.info("Creating Mongo client instance ...");
		logger.info("address=" + hostAddress + ", port=" + portNumber);

		return new MongoClient(hostAddress, portNumber);
	}
	
	@Override
	protected String getMappingBasePackage() {
		return "com.spread.persistence.nosql.model";
	}
}
