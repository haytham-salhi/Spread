package com.spread.persistence.rds.model.enums;

/**
 * 
 * @author Haytham Salhi
 *
 */
public enum SearchEngineCode {
	
	GOOGLE("Google"), BING("Bing"), YAHOO("Yahoo");
	
	private String name;
	
	private SearchEngineCode(String name) {
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	// This is especially for itemLabel="value" because spring searches for getName to get the value
	public String getName() {
		return name;
	}
}
