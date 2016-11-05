package com.spread.crawler;

import java.util.List;
import java.util.Map;

import com.spread.persistence.rds.model.enums.Language;

public abstract class Crawler {
	
	// Read queries from the files
	// for each query
		// Fetch
		// Store
			// Store Store everything in MongoDB
			// Store title, url, snippet, ref to MySql
	
	
	
	public void handleQueries(boolean innerPage, int size, Map<String, List<String>> queries, Language lang) {
		
		
	}

}
