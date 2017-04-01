package com.spread.experiment.preparation;

/**
 * 
 * @author Haytham Salhi
 *
 */
public enum FeatureSpaceModes {
	
	SINGLE_WORDS("SW", 1, 1), 
	PHRASES_OF_TWO_GRAMS("2-GRAMS", 2, 2), 
	SINGLE_WORDS_WITH_TWO_GRAMS("SW_2-GRAMS", 1, 2),
	SINGLE_WORDS_WITH_TWO_GRAMS_WITH_THREE_GRAMS("SW_2-GRAMS_3-GRAMS", 1, 3);
	
	private String name; 
	private int min;
	private int max;
	
	private FeatureSpaceModes(String name, int min, int max) {
		this.name = name;
		this.min = min;
		this.max = max;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	// This is especially for itemLabel="name" because spring searches for getName to get the value
	public String getName() {
		return name;
	}
	
	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public String getFileLabel() {
		return getName().toLowerCase();
	}
}
