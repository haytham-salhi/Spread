package com.spread.srg;

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

import com.google.common.io.Files;
import com.spread.config.RootConfig;
import com.spread.experiment.RawSearchResult;
import com.spread.experiment.data.Data;
import com.spread.experiment.data.stemmers.ArabicStemmerKhoja;
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

/**
 * For experiment general testing
 * @author Haytham Salhi
 *
 */
@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ExperimentTest {
	
	private static final Logger logger = LogManager.getLogger(ExperimentTest.class);
	
	@Autowired
	private SearchResultRepository searchResultRepository;
	
	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private QueryRepository queryRepository;
	
	@Autowired
	private SearchEngineRepository searchEngineRepository;
	
	@Autowired
	private MeaningRepository meaningRepository;
	
	@Autowired
	@Qualifier("approach3Labeling")
	private Data data;
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void experimentsTest() throws Exception {
		
		// UI Input:
		// Query
		// A.Q, MODE = A_Q_ONLY, A_Q
		// Search Engine
		// Max data size to retrieve
		
		// 1. distanceMeasure: Euclidean, Manhatten, and so on and
		// 2. Stemmer: with or without
		// 3. Words to keep
		// 4. TF
		// 5. IDF
		// 6. Min term frequence to keep
		
		// 7. Clustering Method, K if any
		
		// Get all ambiguos queries
		List<Query> ambiguousQueries = queryRepository.findByIsAmbiguous(true);
		ambiguousQueries.forEach(System.out::println);
		
		// From the above list
		int ambiguousQuerySelectedByUser = 1;
		List<Meaning> clearMeaningsWithClearQueriesForAq = meaningRepository.findMeaningsWithClearQueries(ambiguousQuerySelectedByUser); 
		
		for (Meaning meaning : clearMeaningsWithClearQueriesForAq) {
			System.out.println(meaning.getClazz() + " " + meaning.getName() + " " + meaning.getClearQuery());
		}

		// ------------- Data 
		// This will be the input for preparation
		List<RawSearchResult> rawSearchResults = data.getSearchResults(Arrays.asList(new Integer[] {9,10,11,12,13,14,15,16}), SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, false, 2);
		//rawSearchResults.forEach(n -> System.out.println(n.getInnerPage()));
		System.out.println(rawSearchResults.size());
		
		// This will be the input for preparation
		List<String> meaningsList = data.getMeaningsForClearQueries(Arrays.asList(new Integer[] {9,10,11,12,13,14,15,16}));
		
		System.out.println(meaningsList);
		
		// Note that 2, 3, 4 ids the same for getSearchResults and getMeaningsForClearQuerirs of course :)
		
		
		// -------- Preparation
		WClusteringPreprocessor preprocessor = new WClusteringPreprocessor(rawSearchResults, meaningsList);
		
		// 1. 
		preprocessor.prepare(FeatureSelectionModes.TITLE_WITH_SNIPPET);
		
		System.out.println(preprocessor.getTrainingDataSet());

		
		// 2.
		preprocessor.preprocessTrainingDataset(new ArabicStemmerKhoja(), true, 1000, false, true);
		
		
		System.out.println(preprocessor.getTrainingDataSet());;
		
		
		// --------- Clustering
		// User input:
		// 1- Algorithm/s
		// 2- Algorithm parameters 
			// (like K)
			// - K = # of meanings
			// - K = custome value
			// - k = multi value of k
		
			// Distance measure (the applicable ones depend on the algorithm itself)
			// For example k means support only euclidean and manhatten
			// - Euclidean
			// - Manhatten
			// - others 
		SimpleKMeans kmeansModel = new SimpleKMeans();
		
		int k = clearMeaningsWithClearQueriesForAq.size();
		kmeansModel.setNumClusters(k);
		kmeansModel.setDistanceFunction(new EuclideanDistance());
		
		kmeansModel.buildClusterer(preprocessor.getTrainingDataSet());
		
		System.out.println(kmeansModel);
		
		
		
		// --------- Evaluation
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(kmeansModel);
		
		Instances labeledTrainingDataset = preprocessor.getTrainingDataSetWithClassAttr();
		labeledTrainingDataset.setClassIndex(0);
		System.out.println(labeledTrainingDataset);
		
		//eval.evaluateClusterer(new Instances(preprocessor.getTrainingDataSet()));
		eval.evaluateClusterer(labeledTrainingDataset);
		System.out.println(eval.clusterResultsToString());
		
		String evalString = eval.clusterResultsToString();
		
		double[] values = WekaHelper.getIncorrectlyClassifiedInstances(evalString);
		
		System.out.println("Number: " + values[0]);
		System.out.println("Percentage: " + values[1]);

		
		
		System.out.println(Arrays.toString(eval.getClusterAssignments()));
		
		double[] assignments = eval.getClusterAssignments();
		
		for (int i = 0; i < assignments.length; i++) {
			System.out.println(rawSearchResults.get(i) + " --------->" + " cluster " + assignments[i]);
		}
		
		
//		//searchResultRepository.findByQuerySearchEngine_Query_NameAndQuerySearchEngine_SearchEngine_Code("عمان", SearchEngineCode.GOOGLE);
//		List<SearchResult> ambigQueryResults = searchResultRepository.findByQueryAndSearchEngine("عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
//		List<SearchResult> clearQueryResults1 = searchResultRepository.findByQueryAndSearchEngine("مدينة عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
//		List<SearchResult> clearQueryResults2 = searchResultRepository.findByQueryAndSearchEngine("سلطنة عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
//
//		//results.forEach(System.out::println);
//		System.out.println(ambigQueryResults.size());
//		System.out.println(clearQueryResults1.size());
//		System.out.println(clearQueryResults2.size());

	}
}
