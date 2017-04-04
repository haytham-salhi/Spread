package com.spread.experiment.experiments;

/**
 * 
 * @author Haytham Salhi
 *
 */
public abstract class BaseExperiment implements RunnableExperiment {
	// Some props
	private String experimentName;
	private String algorithmName;
	private String basePath = ""; // Optional, where the experiment results folder will be
	private int sizeOfAmbiguousQueriesToLoaded = -1; // < 1 means all queries 
	
	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}
	
	public String getAlgorithmName() {
		return algorithmName;
	}
	
	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}
	
	public String getBasePath() {
		return basePath;
	}
	
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public void setSizeOfAmbiguousQueriesToLoaded(
			int sizeOfAmbiguousQueriesToLoaded) {
		this.sizeOfAmbiguousQueriesToLoaded = sizeOfAmbiguousQueriesToLoaded;
	}
	
	public int getSizeOfAmbiguousQueriesToLoaded() {
		return sizeOfAmbiguousQueriesToLoaded;
	}
}
