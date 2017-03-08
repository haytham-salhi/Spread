package com.spread.frontcontrollers.labeling.model;

public enum YesNoAnswer {
	YES("Yes"), NO("No");
	
	private String name;
	
	private YesNoAnswer(String name) {
		this.name = name;
	}
	
	// This is especially for itemLabel="name" because spring searches for getName to get the value
	public String getName() {
		return name;
	}
}
