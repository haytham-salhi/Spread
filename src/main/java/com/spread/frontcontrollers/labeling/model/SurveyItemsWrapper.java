package com.spread.frontcontrollers.labeling.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

public class SurveyItemsWrapper {
	
	@Valid
	private List<SurveyItem> surveyItems = new ArrayList<SurveyItem>();
	
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
}
