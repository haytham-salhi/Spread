package com.spread.fetcher;

import java.io.Serializable;

import com.spread.model.SearchResult;

public interface SearchEngineFetcher extends Serializable {
	
	public static final String NOT_FOUND = "not found";
	
	/**
	 * Fetches results with default size
	 * <br />
	 * For Google Custom, the default result size that can be returned is 100, which is the default
	 * <br />
	 * For Bing and Google, a one can return more, but the default for this API is 200 
	 * 
	 * @param query
	 * @return
	 */
	public SearchResult fetch(String query, boolean fetchInnerPage);
	
	/**
	 * 
	 * 
	 * @param query
	 * @param maxNumOfResultsToFetch
	 * @return
	 */
	public SearchResult fetch(String query, int maxNumOfResultsToFetch, boolean fetchInnerPage);
}
