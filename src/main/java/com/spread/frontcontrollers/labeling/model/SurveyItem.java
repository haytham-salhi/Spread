package com.spread.frontcontrollers.labeling.model;

import javax.validation.constraints.NotNull;


public class SurveyItem {
	
	private Integer id;
	
	private String title; // TODO This should not be here since it is not form input, this data should be got from elsewhere
	private String url; // TODO This should not be here since it is not form input, this data should be got from elsewhere
	private String snippet; // TODO This should not be here since it is not form input, this data should be got from elsewhere
	
	@NotNull(message = "Empty!")
	private YesNoAnswer answer;
	
	public SurveyItem() {
		// TODO Auto-generated constructor stub
	}
	
	
	public SurveyItem(Integer id, String title, String url, String snippet,
			YesNoAnswer answer) {
		super();
		this.id = id;
		this.title = title;
		this.url = url;
		this.snippet = snippet;
		this.answer = answer;
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the snippet
	 */
	public String getSnippet() {
		return snippet;
	}
	/**
	 * @param snippet the snippet to set
	 */
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	/**
	 * @return the answer
	 */
	public YesNoAnswer getAnswer() {
		return answer;
	}
	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(YesNoAnswer answer) {
		this.answer = answer;
	}
	
	
}
