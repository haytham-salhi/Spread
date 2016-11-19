package com.spread.crawler;

import java.util.List;
import java.util.Map;

import com.spread.model.Meaning;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;

public abstract class Crawler {
	
	// Read queries from the files
	// for each query
		// Fetch
		// Store
			// Store Store everything in MongoDB
			// Store title, url, snippet, ref to MySql
	
	public void handleQueries(boolean innerPage, int size,
			Map<String, List<Meaning>> queries, Language lang,
			SearchEngineLanguage searchEngineLanguage,
			Location locationOfFetching) {
		// TODO Auto-generated method stub
		
	}

}
