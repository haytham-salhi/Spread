package com.spread.experiment.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.stemmers.Stemmer;

import com.google.common.io.Files;
import com.spread.experiment.RawSearchResult;
import com.spread.experiment.data.Data;
import com.spread.experiment.data.stemmers.ArabicStemmerKhoja;
import com.spread.experiment.preparation.FeatureSelectionModes;
import com.spread.experiment.preparation.WClusteringPreprocessorNoLabeling;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchEngineRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.persistence.rds.repository.TestRepository;
import com.spread.util.charts.SpreadPieChart;

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
public class AQDifferentKsExperiment extends BaseExperiment {
	
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
	
	// Differs here
	@Autowired
	@Qualifier("noLabeling")
	private Data data;
	
	private static final Logger LOGGER = LogManager.getLogger("experimentNoLabaled");
	
	// Variables
	private int[] sizes = {10, 100}; // Mainly we change this in this experiment
	private FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY,
			FeatureSelectionModes.SNIPPET_ONLY,
			FeatureSelectionModes.TITLE_WITH_SNIPPET}; // Mainly we change this in this experiment
	// Difference here
	int[] ks = {2, 3, 4, 5, 6, 7, 8};
	
	// These are usually neutralized
	private SearchEngineCode searchEngineCode = SearchEngineCode.GOOGLE;
	private boolean withInnerPage = false;
	
	// Text Preprocessing related 
	private Stemmer stemmer = new ArabicStemmerKhoja(); 
	boolean letterNormalization;
	boolean diacriticsRemoval;
	boolean puncutationRemoval;
	boolean nonArabicWordsRemoval;
	boolean arabicNumbersRemoval;
	boolean nonAlphabeticWordsRemoval;
	boolean stopWordsRemoval;
	boolean ambiguousQueryRemoval;
	
	// Vector-space representation related
	private boolean countWords = true;
	private int wordsToKeep = 1000;
	int wordsToKeepInCaseOfInnerPage = 2000;
	private boolean TF = false;
	private boolean IDF = true;
	int nGramMinSize = 1; // 1 and 1 mean tokenize 1 gram (1 word), 2 and 2 mean toenize 2-gram words 
	int nGramMaxSize = 1; // If you specify a range 1, 2. That means 1-gram and 2-gram will be included in the dictionary (lexicon)
	int minTermFreqToKeep = 1;
	
	
	public void setVariables(int[] sizes,
			FeatureSelectionModes[] featureSelectionModes,
			int[] ks,
			SearchEngineCode searchEngineCode,
			boolean withInnerPage,
			Stemmer stemmer,
			boolean letterNormalization,
			boolean diacriticsRemoval,
			boolean puncutationRemoval,
			boolean nonArabicWordsRemoval,
			boolean arabicNumbersRemoval,
			boolean nonAlphabeticWordsRemoval,
			boolean stopWordsRemoval,
			boolean ambiguousQueryRemoval,
			boolean countWords,
			int wordsToKeep,
			int wordsToKeepInCaseOfInnerPage,
			boolean tF,
			boolean iDF,
			int nGramMinSize, // min=1 and max=1 mean tokenize 1 gram (1 word), 2 and 2 mean toenize 2-gram words // If you specify a range 1, 2. That means 1-gram and 2-gram will be included in the dictionary (lexicon)
			int nGramMaxSize,
			int minTermFreqToKeep) {
		this.sizes = sizes;
		this.featureSelectionModes = featureSelectionModes;
		this.ks = ks;
		this.searchEngineCode = searchEngineCode;
		this.withInnerPage = withInnerPage;
		
		// Text Preprocessing related 
		this.stemmer = stemmer;
		this.letterNormalization = letterNormalization;
		this.diacriticsRemoval = diacriticsRemoval;
		this.puncutationRemoval = puncutationRemoval;
		this.nonArabicWordsRemoval = nonArabicWordsRemoval;
		this.arabicNumbersRemoval = arabicNumbersRemoval;
		this.nonAlphabeticWordsRemoval = nonAlphabeticWordsRemoval;
		this.stopWordsRemoval = stopWordsRemoval;
		this.ambiguousQueryRemoval = ambiguousQueryRemoval;
		
		// Vector-space representation related
		this.countWords = countWords;
		this.wordsToKeep = wordsToKeep;
		this.wordsToKeepInCaseOfInnerPage = wordsToKeepInCaseOfInnerPage;
		this.TF = tF;
		this.IDF = iDF;
		this.nGramMinSize = nGramMinSize;
		this.nGramMaxSize = nGramMaxSize;
		this.minTermFreqToKeep = minTermFreqToKeep;
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
			
			for (int size : sizes) {
				
				for (FeatureSelectionModes featureSelectionMode : featureSelectionModes) {
					LOGGER.info("Working for size=" + size + ", and featureSelectionMode=" + featureSelectionMode);

					// ------------- Data 
					// This will be the input for preparation
					List<RawSearchResult> rawSearchResults = data.getSearchResults(query.getId(), searchEngineCode, Location.PALESTINE, SearchEngineLanguage.AR, withInnerPage, size);
					
					
					// Difference here
					// -------- Preparation
					WClusteringPreprocessorNoLabeling preprocessor = new WClusteringPreprocessorNoLabeling(rawSearchResults, query.getName());
					
					// 1. 
					preprocessor.prepare(featureSelectionMode, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval, nonAlphabeticWordsRemoval,
							stopWordsRemoval, ambiguousQueryRemoval);
					
					// 2.
					preprocessor.buildVectorSpaceDataset(countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, nGramMinSize, nGramMaxSize, minTermFreqToKeep);
					
					
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
						String dirPath = getExperimentName() + "/" + getAlgorithmName() + "/" + query.getName() + "/size_" + size + "/" + featureSelectionMode.getFileLabel() + "/" + "k_" + k;
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
						
						// ------------- Create the cluster analysis chart
						// Example double[] assignments = {1.0, 2.0, 1.0, 3.0, 3.0, 3.0};
						// The output of counts is {2.0=1, 1.0=2, 3.0=3}
						Map<Double, Long> counts = Arrays.stream(assignments).boxed().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
						
						// Fill the chart data set
						DefaultPieDataset dataset = new DefaultPieDataset();
						counts.keySet().forEach(key -> dataset.setValue("C" + key.intValue() + " = " + counts.get(key), counts.get(key)));
						
						JFreeChart pieChart = new SpreadPieChart("Cluster Analysis (k = " + k + ") for " + query.getName(), dataset, true, true, true).getPieChart();
						int width = 640; 
						int height = 480;  
						File pieChartFile = new File(dirPath + "/" + "cluster_analysis.png"); 
						ChartUtilities.saveChartAsPNG(pieChartFile, pieChart, width, height);
						
						LOGGER.info("Done for k=" + k);
					}
					
					// Store the training data set just for debugging and investigation
					storeTrainingDataset(preprocessor.getTrainingDataSet(), getExperimentName() + "/" + getAlgorithmName() + "/" + query.getName() + "/size_" + size + "/" + featureSelectionMode.getFileLabel());
					
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
	
	private void storeTrainingDataset(Instances labeledTrainingDataset, String dirPath) {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(labeledTrainingDataset);
		try {
			saver.setFile(Paths.get(dirPath + "/processed_data.arff").toFile());
			saver.writeBatch();		
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
	}
}
