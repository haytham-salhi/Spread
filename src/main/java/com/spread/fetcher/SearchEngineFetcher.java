package com.spread.fetcher;

import com.spread.model.SearchResult;

public interface SearchEngineFetcher {
	
	public static final String NOT_FOUND = "not found";
	
	/**
	 * Fetches results with default size
	 * <br />
	 * For Google, the default result size that can be returned is 100, which is the default
	 * <br />
	 * For Bing, a one can return more, but the default for this API is 200 
	 * 
	 * @param query
	 * @return
	 */
	public SearchResult fetch(String query);
	
	/**
	 * 
	 * 
	 * @param query
	 * @param maxNumOfResultsToFetch
	 * @return
	 */
	public SearchResult fetch(String query, int maxNumOfResultsToFetch);
}
