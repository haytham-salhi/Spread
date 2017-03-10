package com.spread.frontcontrollers.labeling.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

public class SurveyItemsWrapper {
	
	@Valid
	private List<SurveyItem> surveyItems = new ArrayList<SurveyItem>();
	private String queryName;
	
	public SurveyItemsWrapper() {
		// TODO Auto-generated constructor stub
	}

	public SurveyItemsWrapper(List<SurveyItem> surveyItems) {
		super();
		this.surveyItems = surveyItems;
	}

	/**
	 * @return the items
	 */
	public List<SurveyItem> getSurveyItems() {
		return surveyItems;
	}

	/**
	 * @param items the items to set
	 */
	public void setSurveyItems(List<SurveyItem> surveyItems) {
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
