package com.spread.fetcher;

import com.spread.model.SearchResult;

public interface SearchEngineFetcher {
	
	/**
	 * Fetches all possible results
	 * 
	 * @param query
	 * @return
	 */
	public SearchResult fetch(String query);
}
