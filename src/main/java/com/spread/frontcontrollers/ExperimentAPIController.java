package com.spread.frontcontrollers;

import java.io.File;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope("session")
@RequestMapping("/experiments")
public class ExperimentAPIController implements Serializable {
	
	private static final long serialVersionUID = -4107975505040579437L;

	private static final Logger LOGGER = LogManager.getLogger(ExperimentAPIController.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	//@Autowired
	//@Qualifier("enhancedCQExperiment2")
	//private BaseExperiment enhancedCQExperiment;
	
	
	@RequestMapping(value = "/run", method = RequestMethod.GET)
	public ResponseEntity<String> run() {
		
//		// 1.
//		enhancedCQExperiment.setExperimentName("experiment-approach3-labeling-google"); // The folder name of the experiment
//		enhancedCQExperiment.setAlgorithmName("k-means"); // the sub folder name of the experiment
//		
//		// 2.
//		// Variables
//		int[] sizes = {30}; // 30 per clear query
//		FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY,
//				FeatureSelectionModes.SNIPPET_ONLY,
//				FeatureSelectionModes.TITLE_WITH_SNIPPET,
//				FeatureSelectionModes.INNER_PAGE}; // Mainly we change this in this experiment
//		FeatureSpaceModes[] featureSpaceModes = {FeatureSpaceModes.SINGLE_WORDS, 
//				FeatureSpaceModes.PHRASES_OF_TWO_GRAMS,
//				FeatureSpaceModes.SINGLE_WORDS_WITH_TWO_GRAMS,
//				FeatureSpaceModes.SINGLE_WORDS_WITH_TWO_GRAMS_WITH_THREE_GRAMS};
//		
//		// These are usually neutralized
//		SearchEngineCode searchEngineCode = SearchEngineCode.GOOGLE;
//		boolean withInnerPage = true; // Get the inner page [CR:] in EnhancedCQExperiment, the data.getSearchResults, the innerPage flag has no effect. Always gets the inner page 
//		
//		// Text preprocessing related
//		Stemmer stemmer = new LightStemmer(); 
//		boolean letterNormalization = true;
//		boolean diacriticsRemoval = true;
//		boolean puncutationRemoval = true;
//		boolean nonArabicWordsRemoval = true;
//		boolean arabicNumbersRemoval = true;
//		boolean nonAlphabeticWordsRemoval = true;
//		boolean stopWordsRemoval = true;
//		boolean ambiguousQueryRemoval = true;
//		
//		// Vector-space related (dictionary related)
//		boolean countWords = true;
//		int wordsToKeep = 40; // the top-N most common words;
//		int wordsToKeepInCaseOfInnerPage = 300; // Only applied when detecting innerPage attribute added to training set
//		boolean TF = false; // damping
//		boolean IDF = true;
//		int nGramMinSize = 1; // 1 and 1 mean tokenize 1 gram (1 word), 2 and 2 mean toenize 2-gram words 
//		int nGramMaxSize = 1; // If you specify a range 1, 2. That means 1-gram and 2-gram will be included in the dictionary (lexicon)
//		int minTermFreqToKeep = 1; // TODO think about it?
//		
//		
//		((EnhancedCQExperiment2)enhancedCQExperiment).setVariables(sizes, featureSelectionModes, featureSpaceModes, searchEngineCode, withInnerPage, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval,
//				arabicNumbersRemoval, nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval,
//				countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, minTermFreqToKeep);
//		
//		// 3.
//		try {
//			enhancedCQExperiment.run();
//		} catch (Exception e1) {
//			LOGGER.error(e1);
//			
//			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
		
		String dirPath = "haistest/memo";
		new File(dirPath).mkdirs();
		
		LOGGER.info(new File(".").getAbsolutePath());
		
		return new ResponseEntity<String>(new File(".").getAbsolutePath(), HttpStatus.OK);
	}
}
