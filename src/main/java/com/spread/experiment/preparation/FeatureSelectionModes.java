package com.spread.experiment.preparation;

/**
 * 
 * @author Haytham Salhi
 *
 */
public enum FeatureSelectionModes {
	
	TITLE_ONLY("Title only"), 
	TITLE_WITH_SNIPPET("Title with snippet"),
	SNIPPET_ONLY("Snippet only");
	
	private String name;
	
	private FeatureSelectionModes(String name) {
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	// This is especially for itemLabel="name" because spring searches for getName to get the value
	public String getName() {
		return name;
	}
	
	public String getFileLabel() {
		return name().toLowerCase();
	}
}
