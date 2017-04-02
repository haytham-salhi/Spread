package com.spread.experiment.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import weka.classifiers.Evaluation;
import weka.classifiers.meta.ClassificationViaClustering;
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
import com.spread.experiment.preparation.FeatureSpaceModes;
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
import com.spread.util.charts.SpreadBarChart;

/**
 * 1- Set the variables first; otherwise they will take the default ones
 * 
 * 2- Set experimentName and algorithmName (to be appended in the folders names)
 * 
 * 3- Invoke Run
 * 
 * What makes this differs from EnhancedCQExperiment is that, in this we do fulleval
 * 
 * @author Haytham Salhi
 *
 */
@Component
public class EnhancedCQExperiment2 extends BaseExperiment {
	
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
	
	// [CR]: Change to new data
	@Autowired
	@Qualifier("relevantApproach3Labeling")
	private Data data;
	
	private static final Logger LOGGER = LogManager.getLogger("experimentApproach3");
	
	// Variables
	private int[] sizes = {10, 100}; // Mainly we change this in this experiment
	private FeatureSelectionModes[] featureSelectionModes = {FeatureSelectionModes.TITLE_ONLY,
			FeatureSelectionModes.SNIPPET_ONLY,
			FeatureSelectionModes.TITLE_WITH_SNIPPET}; // Mainly we change this in this experiment
	private FeatureSpaceModes[] featureSpaceModes = {FeatureSpaceModes.SINGLE_WORDS, 
			FeatureSpaceModes.PHRASES_OF_TWO_GRAMS,
			FeatureSpaceModes.SINGLE_WORDS_WITH_TWO_GRAMS,
			FeatureSpaceModes.SINGLE_WORDS_WITH_TWO_GRAMS_WITH_THREE_GRAMS};
	
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
	int minTermFreqToKeep = 1;
	
	
	public void setVariables(int[] sizes,
			FeatureSelectionModes[] featureSelectionModes,
			FeatureSpaceModes[] featureSpaceModes,
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
			int minTermFreqToKeep) { 
		this.sizes = sizes;
		this.featureSelectionModes = featureSelectionModes;
		this.featureSpaceModes = featureSpaceModes;
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
		// [CR]: to official
		List<Query> ambiguousQueries = queryRepository.findByIsAmbiguousAndIsOfficial(true, true);

		for (Query query : ambiguousQueries) {
			LOGGER.info("Processing for A.Q: " + query.getName());
			
			// [CR]: to official
			List<Meaning> clearMeaningsWithClearQueriesForAq = meaningRepository.findOfficialMeaningsWithClearQueries(query.getId());
			
			LOGGER.info("Its meanings are: " + clearMeaningsWithClearQueriesForAq.stream().map(n -> n.getName()).collect(Collectors.toList()));
			
			// Define a chart dataset
			DefaultCategoryDataset chartDataset = new DefaultCategoryDataset();
			
			for (int size : sizes) {
				
				for (FeatureSelectionModes featureSelectionSourceMode : featureSelectionModes) {
					
					for (FeatureSpaceModes featureSpaceMode : featureSpaceModes) {
						
						LOGGER.info("Working for size=" + size + ", featureSelectionMode=" + featureSelectionSourceMode + ", and featureSpaceMode=" + featureSpaceMode);

						// ------------- Data 
						// This will be the input for preparation
						List<Integer> clearQueryIds = clearMeaningsWithClearQueriesForAq.stream().map(n -> n.getClearQuery().getId()).collect(Collectors.toList());
						// [CR]: innerpage variable here has no effect in the following method. Always gets the inner page
						List<RawSearchResult> rawSearchResults = data.getSearchResults(clearQueryIds, searchEngineCode, Location.PALESTINE, SearchEngineLanguage.AR, withInnerPage, size);
						
						// This will be the input for preparation
						List<String> meaningsList = data.getMeaningsForClearQueries(clearQueryIds);
						
						
						// -------- Preparation
						WClusteringPreprocessor preprocessor = new WClusteringPreprocessor(rawSearchResults, meaningsList, query.getName());
						
						// 1. 
						preprocessor.prepare(featureSelectionSourceMode, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval,
								nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval);
						
						// 2.
						preprocessor.buildVectorSpaceDataset(countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, featureSpaceMode.getMin(), featureSpaceMode.getMax(), minTermFreqToKeep);
						
						// --------- Clustering
						// TODO Refactor this for other algorithms
						SimpleKMeans kmeansModel = new SimpleKMeans();
						int k = clearMeaningsWithClearQueriesForAq.size();
						kmeansModel.setNumClusters(k);
						kmeansModel.setDistanceFunction(new EuclideanDistance());
						// [CR]: Use k-means++ as an initialization method
						String[] opts= {"-init", "1"};
						kmeansModel.setOptions(opts);
						// Build the model
						kmeansModel.buildClusterer(preprocessor.getTrainingDataSet());
						
						// --------- Evaluation
						// Evaluate against labeled data
						ClusterEvaluation eval = new ClusterEvaluation();
						eval.setClusterer(kmeansModel);
						// Even though I am using ClassificationViaClustering, I had to use ClusterEvaluation just for getting the assignments
						
						Instances labeledTrainingDataset = preprocessor.getTrainingDataSetWithClassAttr();
						labeledTrainingDataset.setClassIndex(0);
						
						eval.evaluateClusterer(labeledTrainingDataset);
						String evaluationString = eval.clusterResultsToString(); 

						ClassificationViaClustering classificationViaClustering = new ClassificationViaClustering();
						classificationViaClustering.setClusterer(kmeansModel);
						classificationViaClustering.buildClassifier(labeledTrainingDataset); // This will call kmeansModel.buildClustere but on a copy of the passed one :)
						Evaluation evaluation = new Evaluation(labeledTrainingDataset);
						try {
							evaluation.evaluateModel(classificationViaClustering, labeledTrainingDataset);
						} catch (Exception e) {
							System.err.println(e);
							LOGGER.error(e.getMessage());
						}
						
						// ---------- Store the results
						// Create the directory
						String dirPath = getBasePath() + getExperimentName() + "/" + getAlgorithmName() + "/" + query.getName() + "/size_" + size + "/" + featureSelectionSourceMode.getFileLabel() + "/" + featureSpaceMode.getFileLabel();
						new File(dirPath).mkdirs();
						
						// Write the evaluation
						try (BufferedWriter writer = Files.newWriter(Paths.get(dirPath + "/evaluation.txt").toFile(), Charset.forName("UTF-8"))) {
							writer.write(evaluationString);
							
							writer.write("\nIn addition:");							
							
							writer.write("\n" + classificationViaClustering.toString());
							
							writer.write("\nSummaries:");
							writer.write(evaluation.toSummaryString(false) + "\n");
							writer.write("Weighted precesion = " + evaluation.weightedPrecision() + "\n");
							writer.write("Weighted recall = " + evaluation.weightedRecall() + "\n");
							writer.write("Weighted Macro F measure = " + evaluation.weightedFMeasure() + "\n");
							writer.write("Averaged Macro F measure = " + evaluation.unweightedMacroFmeasure()+ "\n");
							writer.write("Averaged Micro F measure = " + evaluation.unweightedMicroFmeasure() + "\n");
							writer.write(evaluation.toMatrixString() + "\n");
							 /**
						     * Calculate the precesion for some class
						     */
						    //int classNumber = labeledTrainingDataset.classAttribute().indexOfValue("meaning1");
						    //System.out.println(evaluation.precision(classNumber));
						    //System.out.println(evaluation.recall(classNumber));
						    //System.out.println(evaluation.fMeasure(classNumber));
						} catch (Exception e) {
							System.err.println(e);
							LOGGER.error(e.getMessage());
						}
						
						// Add it to the chart data set -------------------- Charts step
						chartDataset.addValue(evaluation.pctCorrect(), featureSpaceMode.getName(), featureSelectionSourceMode);
						
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
						
						// Store the training data set just for debugging and investigation
						storeTrainingDataset(labeledTrainingDataset, dirPath);
						
						LOGGER.info("Done for featureSpaceMode=" + featureSpaceMode);
					}
					
					LOGGER.info("Done for featureSelectionMode=" + featureSelectionSourceMode);
				}
				
				LOGGER.info("Done for size" + size);
			}
			
			// ============ Persist a chart -------------------- Charts step
			JFreeChart barChart = new SpreadBarChart(0, 100, query.getName(), "Features", "Percentage of Correctly Clustered Instances (%)", chartDataset, PlotOrientation.VERTICAL, true, true, false).getBarChart();
			int width = 800; 
			int height = 500;  
			File barChartFile = new File(getBasePath() + getExperimentName() + "/" + getAlgorithmName() + "/" + query.getName() + "/" + "evaluation_summary.png"); 
			ChartUtilities.saveChartAsPNG(barChartFile, barChart, width, height);
			
			LOGGER.info("Done for query" + query.getName());
			
			if(isJustOneQueryTest()) {
				break;
			}
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
