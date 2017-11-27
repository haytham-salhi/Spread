package com.spread.srg;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
import com.spread.fetcher.impl.BingFetcher;
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

/**
 * The first experiment (ExperimentCQ) before refactoring
 * 
 * @author Haytham Salhi
 *
 */
@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ExperimentCQTest {
	
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
	
	private static final Logger LOGGER = LogManager.getLogger("experimentApproach3");
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void experimentsTest() throws Exception {
		
		long startTime = System.currentTimeMillis();
		
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
		
		for (Query query : ambiguousQueries) {
			LOGGER.info("Processing for A.Q: " + query.getName());
			
			List<Meaning> clearMeaningsWithClearQueriesForAq = meaningRepository.findMeaningsWithClearQueries(query.getId());
			
			LOGGER.info("Its meanings are: " + clearMeaningsWithClearQueriesForAq.stream().map(n -> n.getName()).collect(Collectors.toList()));

			int[] sizes = {10, 100};
			
			for (int size : sizes) {
				FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY, FeatureSelectionModes.SNIPPET_ONLY, FeatureSelectionModes.TITLE_WITH_SNIPPET};
				
				for (FeatureSelectionModes featureSelectionMode : featureSelectionModes) {
					LOGGER.info("Working for size=" + size + ", and featureSelectionMode=" + featureSelectionMode);

					// ------------- Data 
					// This will be the input for preparation
					List<Integer> clearQueryIds = clearMeaningsWithClearQueriesForAq.stream().map(n -> n.getClearQuery().getId()).collect(Collectors.toList());
					List<RawSearchResult> rawSearchResults = data.getSearchResults(clearQueryIds, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, false, size);
					
					// This will be the input for preparation
					List<String> meaningsList = data.getMeaningsForClearQueries(clearQueryIds);
					
					
					// -------- Preparation
					WClusteringPreprocessor preprocessor = new WClusteringPreprocessor(rawSearchResults, meaningsList, query.getName(), LOGGER);
					
					// 1. 
					preprocessor.prepare(featureSelectionMode, new ArabicStemmerKhoja(), false, false, false, false, false, false, false, false);
					
					// 2.
					preprocessor.buildVectorSpaceDataset(true, 1000, 2000, false, true, 1, 1, 1);
					
					// --------- Clustering
					
					SimpleKMeans kmeansModel = new SimpleKMeans();
					int k = clearMeaningsWithClearQueriesForAq.size();
					kmeansModel.setNumClusters(k);
					kmeansModel.setDistanceFunction(new EuclideanDistance());
					
					// Build the model
					kmeansModel.buildClusterer(preprocessor.getTrainingDataSet());
					
					// --------- Evaluation
					// Evaluate against labeled data
					ClusterEvaluation eval = new ClusterEvaluation();
					eval.setClusterer(kmeansModel);
					
					Instances labeledTrainingDataset = preprocessor.getTrainingDataSetWithClassAttr();
					labeledTrainingDataset.setClassIndex(0);
					
					eval.evaluateClusterer(labeledTrainingDataset);
					
					
					// ---------- Store the results
					// Create the directory
					String dirPath = "results/k-means/" + query.getName() + "/size_" + size + "/" + featureSelectionMode.getFileLabel();
					new File(dirPath).mkdirs();
					
					// Write the evaluation
					try (BufferedWriter writer = Files.newWriter(Paths.get(dirPath + "/evaluation.txt").toFile(), Charset.forName("utf-8"))) {
						writer.write(eval.clusterResultsToString());
						
					} catch (Exception e) {
						System.err.println(e);
						LOGGER.error(e.getMessage());
					}
					
					// Create clusters
					double[] assignments = eval.getClusterAssignments();
					
					for (int i = 0; i < assignments.length; i++) {
						// This file code is differnet from above because we need to append
						try (BufferedWriter writer = new BufferedWriter
							    (new OutputStreamWriter(new FileOutputStream(Paths.get(dirPath + "/cluster_" + (int)assignments[i] + ".txt").toFile(), true), "UTF-8"))) {
							writer.write(rawSearchResults.get(i).getFormedBriefString() + "\n\n");
							
						} catch (Exception e) {
							System.err.println(e);
							LOGGER.error(e.getMessage());
						}
						
					}
					
					LOGGER.info("Done for featureSelectionMode=" + featureSelectionMode);
				}
				
				LOGGER.info("Done for size" + size);
			}
			
			LOGGER.info("Done for query" + query.getName());
		}
		
		// ------------- Data 
		// This will be the input for preparation
//		List<RawSearchResult> rawSearchResults = data.getSearchResults(Arrays.asList(new Integer[] {2, 3, 4}), SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, false, 2);
//		rawSearchResults.forEach(n -> System.out.println(n.getInnerPage()));
//		System.out.println(rawSearchResults.size());
//		
//		// This will be the input for preparation
//		List<String> meaningsList = data.getMeaningsForClearQueries(Arrays.asList(new Integer[] {2, 3, 4}));
//		
//		System.out.println(meaningsList);
		
		// Note that 2, 3, 4 ids the same for getSearchResults and getMeaningsForClearQuerirs of course :)
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		LOGGER.info("Total time for experiment 1 (Approach 3 labeled data): " + totalTime/1000 + " secs");
	}
}
