package com.spread.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class HelloJob {
	
	private static final Logger logger = LogManager.getLogger(HelloJob.class);
	
	public HelloJob() {
		logger.info("Initialized");
	}
	
	@Scheduled(fixedDelay = 5000)
	public void printCurrentTime() {
		logger.info("Hello Body. My name is Haytham!");
	}
}
