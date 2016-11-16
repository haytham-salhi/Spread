package com.spread.persistence.rds.model.enums;

/**
 * 
 * @author Haytham Salhi
 *
 */
public enum QueryFormulationStartegy {
	
	APPEND("Append the meaning to the query"), NO_APPEND("Don't append the meaning");
	
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
