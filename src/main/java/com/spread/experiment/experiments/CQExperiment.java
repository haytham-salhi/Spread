package com.spread.experiment.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.stemmers.Stemmer;

import com.google.common.io.Files;
import com.spread.experiment.RawSearchResult;
import com.spread.experiment.data.Data;
import com.spread.experiment.data.stemmers.ArabicStemmerKhoja;
import com.spread.experiment.preparation.FeatureSelectionModes;
import com.spread.experiment.preparation.WClusteringPreprocessor;
import com.spread.persistence.rds.model.Meaning;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchEngineRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.persistence.rds.repository.TestRepository;
import com.spread.util.WekaHelper;
import com.spread.util.charts.SpreadBarChart;

/**
 * 1- Set the variables first; otherwise they will take the default ones
 * 
 * 2- Set experimentName and algorithmName (to be appended in the folders names)
 * 
 * 3- Invoke Run
 * 
 * @author Haytham Salhi
 *
 */
@Component
public class CQExperiment extends BaseExperiment {
	
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
	
	// Variables
	private int[] sizes = {10, 100}; // Mainly we change this in this experiment
	private FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY,
			FeatureSelectionModes.SNIPPET_ONLY,
			FeatureSelectionModes.TITLE_WITH_SNIPPET}; // Mainly we change this in this experiment
	// These are usually neutralized
	private SearchEngineCode searchEngineCode = SearchEngineCode.GOOGLE;
	private boolean withInnerPage = false;
	private Stemmer stemmer = new ArabicStemmerKhoja(); 
	private boolean countWords = true;
	private int wordsToKeep = 1000;
	private boolean TF = false;
	private boolean IDF = true;
	
	public void setVariables(int[] sizes,
			FeatureSelectionModes[] featureSelectionModes,
			SearchEngineCode searchEngineCode,
			boolean withInnerPage,
			Stemmer stemmer,
			boolean countWords,
			int wordsToKeep,
			boolean tF,
			boolean iDF) {
		this.sizes = sizes;
		this.featureSelectionModes = featureSelectionModes;
		this.searchEngineCode = searchEngineCode;
		this.withInnerPage = withInnerPage;
		this.stemmer = stemmer;
		this.countWords = countWords;
		this.wordsToKeep = wordsToKeep;
		this.TF = tF;
		this.IDF = iDF;
	}

	@Override
	public void run() throws Exception {
		if(getExperimentName() == null || getAlgorithmName() == null) {
			throw new Exception("Experiment name or algorithm name is not set!");
		}
		
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
			
			// Define a chart dataset
			DefaultCategoryDataset chartDataset = new DefaultCategoryDataset();
			
			for (int size : sizes) {
				
				for (FeatureSelectionModes featureSelectionMode : featureSelectionModes) {
					LOGGER.info("Working for size=" + size + ", and featureSelectionMode=" + featureSelectionMode);

					// ------------- Data 
					// This will be the input for preparation
					List<Integer> clearQueryIds = clearMeaningsWithClearQueriesForAq.stream().map(n -> n.getClearQuery().getId()).collect(Collectors.toList());
					List<RawSearchResult> rawSearchResults = data.getSearchResults(clearQueryIds, searchEngineCode, Location.PALESTINE, SearchEngineLanguage.AR, withInnerPage, size);
					
					// This will be the input for preparation
					List<String> meaningsList = data.getMeaningsForClearQueries(clearQueryIds);
					
					
					// -------- Preparation
					WClusteringPreprocessor preprocessor = new WClusteringPreprocessor(rawSearchResults, meaningsList);
					
					// 1. 
					preprocessor.prepare(featureSelectionMode);
					
					// 2.
					preprocessor.preprocessTrainingDataset(stemmer, countWords, wordsToKeep, TF, IDF);
					
					// --------- Clustering
					// TODO Refactor this for other algorithms
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
					String evaluationString = eval.clusterResultsToString(); 
					
					
					// ---------- Store the results
					// Create the directory
					String dirPath = getExperimentName() + "/" + getAlgorithmName() + "/" + query.getName() + "/size_" + size + "/" + featureSelectionMode.getFileLabel();
					new File(dirPath).mkdirs();
					
					// Write the evaluation
					try (BufferedWriter writer = Files.newWriter(Paths.get(dirPath + "/evaluation.txt").toFile(), Charset.forName("UTF-8"))) {
						writer.write(evaluationString);
						
					} catch (Exception e) {
						System.err.println(e);
						LOGGER.error(e.getMessage());
					}
					
					// Add it to the chart data set -------------------- Charts step
					double[] evaluationValues = WekaHelper.getIncorrectlyClassifiedInstances(evaluationString);
					chartDataset.addValue((100.0 - evaluationValues[1]), new Integer(size), featureSelectionMode);
					
					// Create clusters
					double[] assignments = eval.getClusterAssignments();
					
					for (int i = 0; i < assignments.length; i++) {
						// This file code is different from above because we need to append
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
			
			// ============ Persist a chart -------------------- Charts step
			JFreeChart barChart = new SpreadBarChart(0, 100, query.getName(), "Features", "Percentage of Correctly Clustered Instances (%)", chartDataset, PlotOrientation.VERTICAL, true, true, false).getBarChart();
			int width = 640; 
			int height = 480;  
			File barChartFile = new File(getExperimentName() + "/" + getAlgorithmName() + "/" + query.getName() + "/" + "evaluation_summary.png"); 
			ChartUtilities.saveChartAsPNG(barChartFile, barChart, width, height);
			
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
