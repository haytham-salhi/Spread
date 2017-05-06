package com.spread.frontcontrollers.labeling.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

public class SurveyItemsAmbiguousWrapper {
	
	@Valid
	private List<SurveyItemAmbiguous> surveyItems = new ArrayList<SurveyItemAmbiguous>();
	private String queryName;
	
	public SurveyItemsAmbiguousWrapper() {
		// TODO Auto-generated constructor stub
	}

	public SurveyItemsAmbiguousWrapper(List<SurveyItemAmbiguous> surveyItems) {
		super();
		this.surveyItems = surveyItems;
	}

	/**
	 * @return the items
	 */
	public List<SurveyItemAmbiguous> getSurveyItems() {
		return surveyItems;
	}

	/**
	 * @param items the items to set
	 */
	public void setSurveyItems(List<SurveyItemAmbiguous> surveyItems) {
		this.surveyItems = surveyItems;
	}

	/**
	 * @return the queryName
	 */
	public String getQueryName() {
		return queryName;
	}

	/**
	 * @param queryName the queryName to set
	 */
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
}
