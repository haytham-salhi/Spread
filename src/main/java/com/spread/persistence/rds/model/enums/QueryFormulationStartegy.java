package com.spread.persistence.rds.model.enums;

/**
 * 
 * @author Haytham Salhi
 *
 */
public enum QueryFormulationStartegy {
	
	APPEND("Append the meaning to the query"), // == APPEND_RIGHT
	NO_APPEND("Don't append the meaning");
	//APPEND_RIGHT("Append the meaning to the right of the query"), Just in case uin future
	//APPEND_LEFT("Append the meaning to the left of the query");
	
	private String name;
	
	private QueryFormulationStartegy(String name) {
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	// This is especially for itemLabel="name" because spring searches for getName to get the value
	public String getName() {
		return name;
	}
}
