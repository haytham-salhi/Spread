package com.spread.experiment.data.stemmers;

import com.spread.util.nlp.arabic.stemmers.LightStemmer10;

import weka.core.stemmers.Stemmer;

/**
 * A wrapper light stemmer for LightStemmer10
 * 
 * @author Haytham Salhi
 *
 */
public class LightStemmer implements Stemmer {

	private static final long serialVersionUID = 3920567409456927216L;
	
	private LightStemmer10 lightStemmer10;
	
	public LightStemmer() {
		lightStemmer10 = new LightStemmer10();
	}

	@Override
	public String getRevision() {
		return serialVersionUID + "";
	}

	@Override
	public String stem(String word) {
		return lightStemmer10.findStem(word);
	}
}
