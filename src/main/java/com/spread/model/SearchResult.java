package com.spread.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the item to be fetched from a search engine for further processing
 * 
 * @author Haytham Salhi
 *
 */
public class SearchResult {
	
	private List<SearchItem> searchItems = new ArrayList<SearchItem>();
	private String totalResults;
	
	public SearchResult() {
		
	}

	/**
	 * @return the searchItems
	 */
	public List<SearchItem> getSearchItems() {
		return searchItems;
	}

	/**
	 * @param searchItems the searchItems to set
	 */
	public void setSearchItems(List<SearchItem> searchItems) {
		this.searchItems = searchItems;
	}

	/**
	 * @return the totalResults
	 */
	public String getTotalResults() {
		return totalResults;
	}

	/**
	 * @param totalResults the totalResults to set
	 */
	public void setTotalResults(String totalResults) {
		this.totalResults = totalResults;
	}
	
	public void addSearchItem(SearchItem searchItem) {
		searchItems.add(searchItem);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchResult [searchResults=" + searchItems
				+ ", totalResults=" + totalResults + "]";
	}
}
