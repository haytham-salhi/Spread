package com.spread.frontcontrollers;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import weka.core.stemmers.Stemmer;

import com.spread.experiment.data.stemmers.LightStemmer;
import com.spread.experiment.experiments.BaseExperiment;
import com.spread.experiment.experiments.EnhancedCQExperiment;
import com.spread.experiment.experiments.EnhancedCQExperiment2;
import com.spread.experiment.preparation.FeatureSelectionModes;
import com.spread.experiment.preparation.FeatureSpaceModes;
import com.spread.persistence.rds.model.enums.SearchEngineCode;

@RestController
@Scope("session")
@RequestMapping("/experiments")
public class ExperimentAPIController implements Serializable {
	
	private static final long serialVersionUID = -4107975505040579437L;

	private static final Logger LOGGER = LogManager.getLogger(ExperimentAPIController.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	@Qualifier("enhancedCQExperiment2")
	private BaseExperiment enhancedCQExperiment;
	
	private boolean experimentRunning = false;
	
	// This code should be synced with thirdCQExperimentTest in ExperimentsTest
	/**
	 * 
	 * @param aqSizeTest if 0 or less, that means all a.qs
	 * @param fullEval
	 * @param wordsToKeep
	 * @param wordsToKeepInCaseOfInnerPage
	 * @return
	 */
	@RequestMapping(value = "/run", method = RequestMethod.GET)
	public ResponseEntity<String> run(
			@RequestParam(defaultValue= "1", required = false, name = "aqst") int aqSizeTest,
			@RequestParam(defaultValue= "true", required = false, name = "fe") boolean fullEval,
			@RequestParam(defaultValue = "40", required = false, name = "wtk1") int wordsToKeep,
			@RequestParam(defaultValue = "300", required = false, name = "wtk2") int wordsToKeepInCaseOfInnerPage,
			// Size per clear query
			@RequestParam(defaultValue = "30", required = false, name = "size") int size,
			// Possible values: G (for Google), B (for Bing), and GB (for Google and Bing)
			@RequestParam(defaultValue = "G", required = false, name = "engine") String engine,
			@RequestParam(defaultValue = "", required = false, name = "customName") String customName) {
		if(!experimentRunning) {
			experimentRunning = true;
		} else {
			LOGGER.info("Exp is already running!");
			return new ResponseEntity<String>("Experiment is running!", HttpStatus.IM_USED);
		}
		
		BaseExperiment enhancedCQExperiment;

		if(fullEval) {
			enhancedCQExperiment = (BaseExperiment) applicationContext.getBean("enhancedCQExperiment2");
		} else {
			enhancedCQExperiment = (BaseExperiment) applicationContext.getBean("enhancedCQExperiment");
		}
		
		// 1.
		enhancedCQExperiment.setExperimentName("experiment-approach3-labeling-google-full_" + fullEval + "-" + new Date().getTime() + "-" + customName); // The folder name of the experiment
		enhancedCQExperiment.setAlgorithmName("k-means"); // the sub folder name of the experiment
		enhancedCQExperiment.setBasePath("/var/www/html/experiments/"); // To be set just here in APIs
		enhancedCQExperiment.setSizeOfAmbiguousQueriesToLoaded(aqSizeTest);
		
		// 2.
		// Variables
		int[] sizes = {size}; // size per clear query
		FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY,
				FeatureSelectionModes.SNIPPET_ONLY,
				FeatureSelectionModes.TITLE_WITH_SNIPPET,
				FeatureSelectionModes.INNER_PAGE}; // Mainly we change this in this experiment
		FeatureSpaceModes[] featureSpaceModes = {FeatureSpaceModes.SINGLE_WORDS, 
				FeatureSpaceModes.PHRASES_OF_TWO_GRAMS,
				FeatureSpaceModes.SINGLE_WORDS_WITH_TWO_GRAMS,
				FeatureSpaceModes.SINGLE_WORDS_WITH_TWO_GRAMS_WITH_THREE_GRAMS};
		
		// These are usually neutralized
		SearchEngineCode searchEngineCode = SearchEngineCode.GOOGLE;
		switch (engine) {
			case "G":
				searchEngineCode = SearchEngineCode.GOOGLE;
				break;
			case "B":
				searchEngineCode = SearchEngineCode.BING;
				break;
			case "GB":
				searchEngineCode = null;
				break;
			default:
				return new ResponseEntity<String>("Weein raye7 ya kbeer", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		boolean withInnerPage = true; // Get the inner page [CR:] in EnhancedCQExperiment, the data.getSearchResults, the innerPage flag has no effect. Always gets the inner page 
		
		// Text preprocessing related
		Stemmer stemmer = new LightStemmer(); 
		boolean letterNormalization = true;
		boolean diacriticsRemoval = true;
		boolean puncutationRemoval = true;
		boolean nonArabicWordsRemoval = true;
		boolean arabicNumbersRemoval = true;
		boolean nonAlphabeticWordsRemoval = true;
		boolean stopWordsRemoval = true;
		boolean ambiguousQueryRemoval = true;
		
		// Vector-space related (dictionary related)
		boolean countWords = true;
		//int wordsToKeep = 40; // the top-N most common words;
		//int wordsToKeepInCaseOfInnerPage = 300; // Only applied when detecting innerPage attribute added to training set
		boolean TF = false; // damping
		boolean IDF = true;
		int nGramMinSize = 1; // 1 and 1 mean tokenize 1 gram (1 word), 2 and 2 mean toenize 2-gram words 
		int nGramMaxSize = 1; // If you specify a range 1, 2. That means 1-gram and 2-gram will be included in the dictionary (lexicon)
		int minTermFreqToKeep = 1; // TODO think about it?
		
		// This is not correct! You should push the setVariables method to a new parent class!
		if(fullEval) {
			((EnhancedCQExperiment2)enhancedCQExperiment).setVariables(sizes, featureSelectionModes, featureSpaceModes, searchEngineCode, withInnerPage, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval,
					arabicNumbersRemoval, nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval,
					countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, minTermFreqToKeep);
		} else {
			((EnhancedCQExperiment)enhancedCQExperiment).setVariables(sizes, featureSelectionModes, featureSpaceModes, searchEngineCode, withInnerPage, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval,
					arabicNumbersRemoval, nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval,
					countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, minTermFreqToKeep);
		}
		
		
		// 3.
		try {
			enhancedCQExperiment.run();
		} catch (Exception e1) {
			LOGGER.error(ExceptionUtils.getStackTrace(e1));
			
			experimentRunning = false;
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		experimentRunning = false;
		return new ResponseEntity<String>(new File(".").getAbsolutePath(), HttpStatus.OK);
	}
}
