package com.spread.experiment;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.stemmers.Stemmer;

import com.google.common.io.Files;
import com.spread.config.RootConfig;
import com.spread.experiment.RawSearchResult;
import com.spread.experiment.data.Data;
import com.spread.experiment.data.stemmers.ArabicStemmerKhoja;
import com.spread.experiment.data.stemmers.LightStemmer;
import com.spread.experiment.experiments.AQDifferentKsExperiment;
import com.spread.experiment.experiments.BaseExperiment;
import com.spread.experiment.experiments.CQExperiment;
import com.spread.experiment.experiments.EnhancedCQExperiment;
import com.spread.experiment.experiments.RunnableExperiment;
import com.spread.experiment.preparation.FeatureSelectionModes;
import com.spread.experiment.preparation.FeatureSpaceModes;
import com.spread.experiment.preparation.WClusteringPreprocessor;
import com.spread.persistence.rds.model.Meaning;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.SearchEngine;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchEngineRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.persistence.rds.repository.TestRepository;
import com.spread.util.WekaHelper;

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ExperimentsTest {
	
	@Autowired
	@Qualifier("CQExperiment")
	private BaseExperiment cQExperiment;
	
	@Autowired
	@Qualifier("AQDifferentKsExperiment")
	private BaseExperiment aQDifferentKsExperiment;
	
	@Autowired
	@Qualifier("enhancedCQExperiment")
	private BaseExperiment enhancedCQExperiment;
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void firstCQExperimentTest() throws Exception {
		// 1.
		cQExperiment.setExperimentName("experiment-approach3-labeling-with-charts"); // The folder name of the experiment
		cQExperiment.setAlgorithmName("k-means"); // the sub folder name of the experiment
		
		// 2.
		// Variables
		int[] sizes = {10, 100}; // Mainly we change this in this experiment
		FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY,
				FeatureSelectionModes.SNIPPET_ONLY,
				FeatureSelectionModes.TITLE_WITH_SNIPPET}; // Mainly we change this in this experiment
		// These are usually neutralized
		SearchEngineCode searchEngineCode = SearchEngineCode.GOOGLE;
		boolean withInnerPage = false;
		Stemmer stemmer = new ArabicStemmerKhoja(); 
		boolean countWords = true;
		int wordsToKeep = 1000;
		boolean TF = false;
		boolean IDF = true;
		
		((CQExperiment)cQExperiment).setVariables(sizes, featureSelectionModes, searchEngineCode, withInnerPage, stemmer, false, false, true, false, false, false, true, false, 
				true, 1000, 1000, false, true, 1, 1, 1);
		
		// 3.
		cQExperiment.run();
	}
	
	@Test
	public void firstAQDifferentKsExperimentTest() throws Exception {
		
		// 1.
		aQDifferentKsExperiment.setExperimentName("experiment-aq-different-ks-with-charts"); // The folder name of the experiment
		aQDifferentKsExperiment.setAlgorithmName("k-means"); // the sub folder name of the experiment
		
		// 2.
		// Variables
		int[] sizes = {10, 100}; // Mainly we change this in this experiment
		FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY,
				FeatureSelectionModes.SNIPPET_ONLY,
				FeatureSelectionModes.TITLE_WITH_SNIPPET}; // Mainly we change this in this experiment
		int[] ks = {2, 3, 4, 5, 6, 7, 8};
		// These are usually neutralized
		SearchEngineCode searchEngineCode = SearchEngineCode.GOOGLE;
		boolean withInnerPage = false;
		Stemmer stemmer = new ArabicStemmerKhoja(); 
		boolean countWords = true;
		int wordsToKeep = 1000;
		boolean TF = false;
		boolean IDF = true;
		
		((AQDifferentKsExperiment)aQDifferentKsExperiment).setVariables(sizes, featureSelectionModes, ks, searchEngineCode, withInnerPage, stemmer, false, false, true, false, false, false, true, false,
				true, 1000, 1000, TF, IDF, 1, 1, 1);
		
		// 3.
		aQDifferentKsExperiment.run();

	}
	
	@Test
	public void secondCQExperimentTest() throws Exception {
		// 1.
		cQExperiment.setExperimentName("experiment-approach3-labeling-google"); // The folder name of the experiment
		cQExperiment.setAlgorithmName("k-means"); // the sub folder name of the experiment
		
		// 2.
		// Variables
		int[] sizes = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100}; // Mainly we change this in this experiment
		FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY,
				FeatureSelectionModes.SNIPPET_ONLY,
				FeatureSelectionModes.TITLE_WITH_SNIPPET,
				FeatureSelectionModes.INNER_PAGE}; // Mainly we change this in this experiment
		
		// These are usually neutralized
		SearchEngineCode searchEngineCode = SearchEngineCode.GOOGLE;
		boolean withInnerPage = true; // Get the inner page
		
		// Text preprocessing related
		Stemmer stemmer = new ArabicStemmerKhoja(); 
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
		int wordsToKeep = 40;
		int wordsToKeepInCaseOfInnerPage = 300; // Only applied when detecting innerPage attribute added to training set
		boolean TF = false; // damping
		boolean IDF = true;
		int nGramMinSize = 2; // 1 and 1 mean tokenize 1 gram (1 word), 2 and 2 mean toenize 2-gram words 
		int nGramMaxSize = 2; // If you specify a range 1, 2. That means 1-gram and 2-gram will be included in the dictionary (lexicon)
		int minTermFreqToKeep = 2; // TODO think about it?
		
		
		((CQExperiment)cQExperiment).setVariables(sizes, featureSelectionModes, searchEngineCode, withInnerPage, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval,
				arabicNumbersRemoval, nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval,
				countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, nGramMinSize, nGramMaxSize, minTermFreqToKeep);
		
		// 3.
		cQExperiment.run();
	}
	
	@Test
	public void secondAQDifferentKsExperimentTest() throws Exception {
		
		// 1.
		aQDifferentKsExperiment.setExperimentName("experiment-aq-different-ks-with-charts"); // The folder name of the experiment
		aQDifferentKsExperiment.setAlgorithmName("k-means"); // the sub folder name of the experiment
		
		// 2.
		// Variables
		int[] sizes = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100}; // Mainly we change this in this experiment
		FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY,
				FeatureSelectionModes.SNIPPET_ONLY,
				FeatureSelectionModes.TITLE_WITH_SNIPPET,
				FeatureSelectionModes.INNER_PAGE}; // Mainly we change this in this experiment
		
		int[] ks = {2, 3, 4, 5, 6, 7, 8, 9, 10};
		// These are usually neutralized
		// These are usually neutralized
		SearchEngineCode searchEngineCode = SearchEngineCode.GOOGLE;
		boolean withInnerPage = true; // Get the inner page
		
		// Text preprocessing related
		Stemmer stemmer = new ArabicStemmerKhoja(); 
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
		int wordsToKeep = 40;
		int wordsToKeepInCaseOfInnerPage = 300; // Only applied when detecting innerPage attribute added to training set
		boolean TF = false;
		boolean IDF = true;
		int nGramMinSize = 2; // 1 and 1 mean tokenize 1 gram (1 word), 2 and 2 mean toenize 2-gram words 
		int nGramMaxSize = 2; // If you specify a range 1, 2. That means 1-gram and 2-gram will be included in the dictionary (lexicon)
		int minTermFreqToKeep = 2; // TODO think about it?
		
		((AQDifferentKsExperiment)aQDifferentKsExperiment).setVariables(sizes, featureSelectionModes, ks, searchEngineCode, withInnerPage, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval,
				arabicNumbersRemoval, nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval,
				countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, nGramMinSize, nGramMaxSize, minTermFreqToKeep);
		
		// 3.
		aQDifferentKsExperiment.run();

	}
	
	// This code should be synced with run in ExperimentAPIController
	@Test
	public void thirdCQExperimentTest() throws Exception {
		// 1.
		enhancedCQExperiment.setExperimentName("experiment-approach3-labeling-google"); // The folder name of the experiment
		enhancedCQExperiment.setAlgorithmName("k-means"); // the sub folder name of the experiment
		enhancedCQExperiment.setJustOneQueryTest(true); // Here to specify if we want to run for the first query only for test
		
		// 2.
		// Variables
		int[] sizes = {30}; // 30 per clear query
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
		int wordsToKeep = 40; // the top-N most common words;
		int wordsToKeepInCaseOfInnerPage = 300; // Only applied when detecting innerPage attribute added to training set
		boolean TF = false; // damping
		boolean IDF = true;
		int nGramMinSize = 1; // 1 and 1 mean tokenize 1 gram (1 word), 2 and 2 mean toenize 2-gram words 
		int nGramMaxSize = 1; // If you specify a range 1, 2. That means 1-gram and 2-gram will be included in the dictionary (lexicon)
		int minTermFreqToKeep = 1; // TODO think about it?
		
		
		((EnhancedCQExperiment)enhancedCQExperiment).setVariables(sizes, featureSelectionModes, featureSpaceModes, searchEngineCode, withInnerPage, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval,
				arabicNumbersRemoval, nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval,
				countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, minTermFreqToKeep);
		
		// 3.
		enhancedCQExperiment.run();
	}
}
