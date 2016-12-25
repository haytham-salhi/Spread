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
import com.spread.experiment.experiments.AQDifferentKsExperiment;
import com.spread.experiment.experiments.BaseExperiment;
import com.spread.experiment.experiments.CQExperiment;
import com.spread.experiment.experiments.RunnableExperiment;
import com.spread.experiment.preparation.FeatureSelectionModes;
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
		
		((CQExperiment)cQExperiment).setVariables(sizes, featureSelectionModes, searchEngineCode, withInnerPage, stemmer, countWords, wordsToKeep, TF, IDF);
		
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
		
		((AQDifferentKsExperiment)aQDifferentKsExperiment).setVariables(sizes, featureSelectionModes, ks, searchEngineCode, withInnerPage, stemmer, countWords, wordsToKeep, TF, IDF);
		
		// 3.
		aQDifferentKsExperiment.run();

	}
}
