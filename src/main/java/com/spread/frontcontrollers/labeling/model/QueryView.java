package com.spread.frontcontrollers.labeling.model;


import com.spread.persistence.rds.model.Query;


public class QueryView {
	
	private Query query;
	private int respondents;
	
	public QueryView() {
	}

	public QueryView(Query query, int respondents) {
		super();
		this.query = query;
		this.respondents = respondents;
	}

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	/**
	 * @return the respondents
	 */
	public int getRespondents() {
		return respondents;
	}

	/**
	 * @param respondents the respondents to set
	 */
	public void setRespondents(int respondents) {
		this.respondents = respondents;
	}
}
