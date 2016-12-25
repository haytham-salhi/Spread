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
}
