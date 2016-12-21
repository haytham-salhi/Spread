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
import com.spread.experiment.preparation.WClusteringPreprocessorNoLabeling;
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

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ExperimentAQDifferentKsTest {
	
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
	
	private static final Logger LOGGER = LogManager.getLogger("experimentNoLabaled");
	
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
			
			int[] sizes = {10, 100};
			
			for (int size : sizes) {
				FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY, FeatureSelectionModes.SNIPPET_ONLY, FeatureSelectionModes.TITLE_WITH_SNIPPET};
				
				for (FeatureSelectionModes featureSelectionMode : featureSelectionModes) {
					LOGGER.info("Working for size=" + size + ", and featureSelectionMode=" + featureSelectionMode);

					// ------------- Data 
					// This will be the input for preparation
					List<RawSearchResult> rawSearchResults = data.getSearchResults(query.getId(), SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, false, size);
					
					
					// Difference here
					// -------- Preparation
					WClusteringPreprocessorNoLabeling preprocessor = new WClusteringPreprocessorNoLabeling(rawSearchResults);
					
					// 1. 
					preprocessor.prepare(featureSelectionMode);
					
					// 2.
					preprocessor.preprocessTrainingDataset(new ArabicStemmerKhoja(), true, 1000, false, true);
					
					// Difference here
					int[] ks = {2, 3, 4, 5, 6, 7, 8};
					
					for (int k : ks) {
						// --------- Clustering
						
						// Difference here
						SimpleKMeans kmeansModel = new SimpleKMeans();
						kmeansModel.setNumClusters(k);
						kmeansModel.setDistanceFunction(new EuclideanDistance());
						
						// Build the model
						kmeansModel.buildClusterer(preprocessor.getTrainingDataSet());
						
						
						// Difference here
						// --------- Evaluation
						// Evaluate against the same training data (unlabeled data) (subjective evaluation)
						ClusterEvaluation eval = new ClusterEvaluation();
						eval.setClusterer(kmeansModel);
						eval.evaluateClusterer(new Instances(preprocessor.getTrainingDataSet()));
						
						
						// Difference here
						// ---------- Store the results
						// Create the directory
						String dirPath = "results2/k-means/" + query.getName() + "/size_" + size + "/" + featureSelectionMode.getFileLabel() + "/" + "k_" + k;
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
							// Write the evaluation
							try (BufferedWriter writer = new BufferedWriter
								    (new OutputStreamWriter(new FileOutputStream(Paths.get(dirPath + "/cluster_" + (int)assignments[i] + ".txt").toFile(), true), "UTF-8"))) {
								writer.write(rawSearchResults.get(i).getFormedBriefString() + "\n\n");
								
							} catch (Exception e) {
								System.err.println(e);
								LOGGER.error(e.getMessage());
							}
							
						}
						
						LOGGER.info("Done for k=" + k);
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
		LOGGER.info("Total time for experiment 2 (Unlabeled data): " + totalTime/1000 + " secs");
	}
}
