package com.spread.frontcontrollers.model;

import org.hibernate.validator.constraints.NotEmpty;

import com.spread.persistence.rds.model.enums.QueryFormulationStartegy;
import com.spread.persistence.rds.model.enums.SearchEngineCode;

public class IntersectDemoData {
	
	@NotEmpty(message = "Must not be empty")
	private String query;
	
	// Should be \n separated
	@NotEmpty(message = "Must not be empty")
	private String meanings;
	
	private QueryFormulationStartegy queryFormulationStartegy;
	
	private SearchEngineCode searchEngine;
	
	public IntersectDemoData() {
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMeanings() {
		return meanings;
	}
	
	/**
	 * 
	 * @param meanings
	 */
	public void setMeanings(String meanings) {
		this.meanings = meanings;
	}
	
	/**
	 * 
	 * @return
	 */
	public QueryFormulationStartegy getQueryFormulationStartegy() {
		return queryFormulationStartegy;
	}
	
	/**
	 * 
	 * @param queryFormulationStartegy
	 */
	public void setQueryFormulationStartegy(
			QueryFormulationStartegy queryFormulationStartegy) {
		this.queryFormulationStartegy = queryFormulationStartegy;
	}

	/**
	 * @return the searchEngine
	 */
	public SearchEngineCode getSearchEngine() {
		return searchEngine;
	}

	/**
	 * @param searchEngine the searchEngine to set
	 */
	public void setSearchEngine(SearchEngineCode searchEngine) {
		this.searchEngine = searchEngine;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IntersectDemoData [query=" + query + ", meanings=" + meanings
				+ ", searchEngine=" + searchEngine + "]";
	}
}
