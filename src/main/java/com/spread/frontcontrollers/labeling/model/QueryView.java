package com.spread.frontcontrollers.labeling.model;


import java.util.List;

import com.spread.persistence.rds.model.Query;


public class QueryView {
	
	private Query query;
	private int respondents;
	private List<String> respondentNames;
	
	public QueryView() {
	}

	public QueryView(Query query, int respondents, List<String> respondentNames) {
		super();
		this.query = query;
		this.respondents = respondents;
		this.respondentNames = respondentNames;
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

	/**
	 * @return the respondentNames
	 */
	public List<String> getRespondentNames() {
		return respondentNames;
	}

	/**
	 * @param respondentNames the respondentNames to set
	 */
	public void setRespondentNames(List<String> respondentNames) {
		this.respondentNames = respondentNames;
	}
}
