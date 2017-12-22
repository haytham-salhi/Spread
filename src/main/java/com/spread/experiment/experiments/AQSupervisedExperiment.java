package com.spread.experiment.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.stemmers.Stemmer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import com.google.common.io.Files;
import com.spread.experiment.RawSearchResult;
import com.spread.experiment.data.stemmers.ArabicStemmerKhoja;
import com.spread.experiment.evaluation.OtherWayForMyMetric;
import com.spread.experiment.preparation.FeatureSelectionModes;
import com.spread.experiment.preparation.FeatureSpaceModes;
import com.spread.experiment.preparation.WClusteringPreprocessor;
import com.spread.experiment.tempuntilofficialrelease.ClassificationViaClustering108;
import com.spread.persistence.rds.model.Meaning;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.util.charts.SpreadBarChart;

/**
 * 1- Set the variables first; otherwise they will take the default ones
 * 
 * 2- Set the data source
 * 
 * 3- Set experimentName and algorithmName (to be appended in the folders names)
 * 
 * 4- Invoke Run
 * 
 * What makes this differs from EnhancedCQExperiment is that, in this we do fulleval
 * 
 * @author Haytham Salhi
 *
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("item5ExperimentBean")
public class AQSupervisedExperiment extends BaseExperiment {
	
	@Autowired
	private SearchResultRepository searchResultRepository;
	
	@Autowired
	private QueryRepository queryRepository;
	
	@Autowired
	private MeaningRepository meaningRepository;
	
	private StringBuilder anovaPctCorrect = new StringBuilder(); // One for the accuracies file (percentages)
	private StringBuilder anovaNumCorrect = new StringBuilder(); // One for the correctly classified instances numbers 
	private StringBuilder anovaWeightedPrecision = new StringBuilder();
	private StringBuilder anovaWeightedRecall = new StringBuilder();
	private StringBuilder anovaWeightedFMeasure = new StringBuilder();
	private StringBuilder anovaUnweightedMacroFmeasure = new StringBuilder();
	private StringBuilder anovaUnweightedMicroFmeasure = new StringBuilder();
	
	private static final Logger LOGGER = LogManager.getLogger("item5Experiment");
	
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
		Pageable pageRequest = null; // Default, get all official
		if(getSizeOfAmbiguousQueriesToLoaded() > 0) {
			pageRequest = new PageRequest(0, getSizeOfAmbiguousQueriesToLoaded());
		}
		List<Query> ambiguousQueries = queryRepository.findAmbiguousQueriesWhoseResultsLabeled(searchEngineCode, pageRequest);

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
						
						
						LOGGER.info("Step 1.: Building the supervised model (composed of results for AQ's clear qeuries (Training set)) for the A.Q: " + query.getName());
						// # 1. Build the supervised model
						
						// ------------- Data 
						// This will be the input for preparation
						List<Integer> clearQueryIds = clearMeaningsWithClearQueriesForAq.stream().map(n -> n.getClearQuery().getId()).collect(Collectors.toList());
						
						// If code is null, then get mixed results of google and bing
						List<RawSearchResult> trainingRawSearchResults = null;
						if(searchEngineCode == null) {
							// [CR]: innerpage variable here has no effect in the following method. Always gets the inner page
							List<RawSearchResult> googleRawSearchResults = data.getSearchResults(clearQueryIds, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, withInnerPage, size);
							List<RawSearchResult> bingRawSearchResults = data.getSearchResults(clearQueryIds, SearchEngineCode.BING, Location.PALESTINE, SearchEngineLanguage.AR, withInnerPage, size);
							
							trainingRawSearchResults =  new ArrayList<>();
							trainingRawSearchResults.addAll(googleRawSearchResults);
							trainingRawSearchResults.addAll(bingRawSearchResults);
						} else {
							// Otherwise, get the results of that search engine
							// [CR]: innerpage variable here has no effect in the following method. Always gets the inner page
							trainingRawSearchResults = data.getSearchResults(clearQueryIds, searchEngineCode, Location.PALESTINE, SearchEngineLanguage.AR, withInnerPage, size);
						}
						LOGGER.info("Size of search results is (Training set)" + trainingRawSearchResults.size());
						
						// This will be the input for preparation
						List<String> meaningsList = data.getMeaningsForClearQueries(clearQueryIds);
						
						
						// -------- Preparation
						WClusteringPreprocessor trainingDatasetPreprocesser = new WClusteringPreprocessor(trainingRawSearchResults, meaningsList, query.getName(), LOGGER);
						
						// 1. 
						trainingDatasetPreprocesser.prepare(featureSelectionSourceMode, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval,
								nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval);
						
						// 2. This will prpeare the filter only, will not convert data into VSM, also, will make copy of training dataset with classatrr and one without clasattr
						StringToWordVector toVectorsfilter = trainingDatasetPreprocesser.getVsmFilter(countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, featureSpaceMode.getMin(), featureSpaceMode.getMax(), minTermFreqToKeep);
						
						// Here just as instances, not in VSM,
						// This will be used for training the model
						Instances trainingDatasetAsInstancesWithNoClassAttr = trainingDatasetPreprocesser.getTrainingDataSet();
						// This will be used for clusters-to-classes evaluation 
						// But no need here because we will evaluate against test data
						//Instances trainingDatasetAsInstancesWithClassAttr = trainingDatasetPreprocesser.getTrainingDataSetWithClassAttr();
						
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
						//kmeansModel.buildClusterer(trainingDatasetPreprocesser.getTrainingDataSet());
						
						MultiFilter multiFilter = new MultiFilter();
						multiFilter.setFilters(new Filter[] {toVectorsfilter});
						
						FilteredClusterer fc = new FilteredClusterer();
						fc.setFilter(multiFilter);
						fc.setClusterer(kmeansModel);
						fc.buildClusterer(trainingDatasetAsInstancesWithNoClassAttr);
						
						LOGGER.info("Step 1.: Done Building the supervised model (composed of results for AQ's clear qeuries (Training set)) for the A.Q: " + query.getName());

						LOGGER.info("Step 2.: Getting and preprocessing labeled search results (Test set) for the A.Q: " + query.getName());
						// # 2.  Get search results of the ambiguous query
						
						// ------------- Data 
						// This will be the input for preparation
						// If code is null, then get mixed results of google and bing
						// These are the raw search results for A.Q
						List<RawSearchResult> testRawSearchResults = null;
						if(searchEngineCode == null) {
							// I used here the repo directly, ideally you might want to use datasource instead! ;-)
							List<RawSearchResult> googleRawSearchResults = searchResultRepository.getLabeledSearchResultsBuQueryIdAndSearchEngine(query.getId(), SearchEngineCode.GOOGLE, withInnerPage); // You can add size in future if needed!
							List<RawSearchResult> bingRawSearchResults = searchResultRepository.getLabeledSearchResultsBuQueryIdAndSearchEngine(query.getId(), SearchEngineCode.BING, withInnerPage); // You can add size in future if needed!
							
							LOGGER.info("Size of google search results is " + googleRawSearchResults.size());
							LOGGER.info("Size of bing search results is " + googleRawSearchResults.size());
							
							testRawSearchResults =  new ArrayList<>();
							testRawSearchResults.addAll(googleRawSearchResults);
							testRawSearchResults.addAll(bingRawSearchResults);
						} else {
							// Otherwise, get the results of that search engine
							testRawSearchResults = searchResultRepository.getLabeledSearchResultsBuQueryIdAndSearchEngine(query.getId(), searchEngineCode, withInnerPage); //data.getSearchResults(query.getId(), searchEngineCode, Location.PALESTINE, SearchEngineLanguage.AR, withInnerPage, size);
						}
						LOGGER.info("Size of search results of A.Q (Test set) is " + testRawSearchResults.size());
						
						// -------- Preparation
						WClusteringPreprocessor testDatasetPreprocessor = new WClusteringPreprocessor(testRawSearchResults, meaningsList, query.getName(), LOGGER);
						
						// 1. 
						testDatasetPreprocessor.prepare(featureSelectionSourceMode, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval,
								nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval);
						
						// 2. No need to build vector space hoon aslan, la2enno serna nesta5dem el filtered
						// for test data, I called this just to make copy of training data, one with class atrr and one with no class att, also, to set wtk conditonally based on if its inner ppage or not
						testDatasetPreprocessor.getVsmFilter(countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, featureSpaceMode.getMin(), featureSpaceMode.getMax(), minTermFreqToKeep);
						
						LOGGER.info("Step 2.: Done Getting and preprocessing labeled search results (Test set) for the A.Q: " + query.getName());
						
						LOGGER.info("Step 3.: Evaluating supervised models against test data set for the A.Q: " + query.getName());
						// # 3. Evaluate
						
						// --------- Evaluation
						// Evaluate against labeled test data set
						ClusterEvaluation eval = new ClusterEvaluation();
						eval.setClusterer(fc);
						// Even though I am using ClassificationViaClustering, I had to use ClusterEvaluation just for getting the assignments
						
						// Just instances without VSM
						Instances labeledTestDatasetWithClassAttr = testDatasetPreprocessor.getTrainingDataSetWithClassAttr(); // This is the test data set actually
						labeledTestDatasetWithClassAttr.setClassIndex(labeledTestDatasetWithClassAttr.numAttributes() - 1);
						
						// Just instances without VSM
						Instances trainingDatasetAsInstancesWithClassAttr = trainingDatasetPreprocesser.getTrainingDataSetWithClassAttr();
						trainingDatasetAsInstancesWithClassAttr.setClassIndex(trainingDatasetAsInstancesWithClassAttr.numAttributes() - 1);
						
						eval.evaluateClusterer(labeledTestDatasetWithClassAttr);
						String evaluationString = eval.clusterResultsToString(); 

						ClassificationViaClustering108 classificationViaClustering = new ClassificationViaClustering108();
						classificationViaClustering.setClusterer(fc);
						classificationViaClustering.buildClassifier(trainingDatasetAsInstancesWithClassAttr); // This will call kmeansModel.buildClustere but on a copy of the passed one :)
						OtherWayForMyMetric evaluation = new OtherWayForMyMetric(trainingDatasetAsInstancesWithClassAttr);
						try {
							evaluation.evaluateModel(classificationViaClustering, labeledTestDatasetWithClassAttr);
						} catch (Exception e) {
							System.err.println(e);
							LOGGER.error(e.getMessage());
						}
						
						LOGGER.info("Step 3.: Done Evaluating supervised models against test data set for the A.Q: " + query.getName());
						
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
								writer.write(testRawSearchResults.get(i).getFormedBriefString() + "\n\n");
								
							} catch (Exception e) {
								System.err.println(e);
								LOGGER.error(e.getMessage());
							}
							
						}
						
						// Store the training data set just for debugging and investigation
						storeDataset(trainingDatasetAsInstancesWithClassAttr, dirPath, "training");
						// Store Test data set
						storeDataset(labeledTestDatasetWithClassAttr, dirPath, "test");
						
						//anovaFile.append(correctlyClusteredPrc + ",");
						anovaPctCorrect.append(evaluation.pctCorrect() + ",");
						anovaNumCorrect.append(evaluation.correct() + ",");
						anovaWeightedPrecision.append(evaluation.weightedPrecision() + ",");
						anovaWeightedRecall.append(evaluation.weightedRecall() + ",");
						anovaWeightedFMeasure.append(evaluation.weightedFMeasure() + ",");
						anovaUnweightedMacroFmeasure.append(evaluation.unweightedMacroFmeasure() + ",");
						anovaUnweightedMicroFmeasure.append(evaluation.unweightedMicroFmeasure() + ",");
						
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
			
			// To go to next Subject
			anovaPctCorrect.append("\n");
			anovaNumCorrect.append("\n");
			anovaWeightedPrecision.append("\n");
			anovaWeightedRecall.append("\n");
			anovaWeightedFMeasure.append("\n");
			anovaUnweightedMacroFmeasure.append("\n");
			anovaUnweightedMicroFmeasure.append("\n");
			
			LOGGER.info("Done for query" + query.getName());
		}
		
		// Finally, after processing the subjects, persist the anova file
		wrtieAnovaString();
		
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
		LOGGER.info("Total time for item #5 experiment: " + totalTime/1000 + " secs");
	}

	private void storeDataset(Instances labeledTrainingDataset, String dirPath, String fileNamePrefix) {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(labeledTrainingDataset);
		try {
			saver.setFile(Paths.get(dirPath + "/" + fileNamePrefix + "_processed_data.arff").toFile());
			saver.writeBatch();		
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
	}
	
	private void wrtieAnovaString() {
		String dirPath = getBasePath() + getExperimentName();
		try (BufferedWriter writer = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream(Paths.get(dirPath + "/effectiveness-pct-data.txt").toFile(), true), "UTF-8"))) {
			writer.write(anovaPctCorrect.toString());
			
		} catch (Exception e) {
			System.err.println(e);
			LOGGER.error(e.getMessage());
		}
		
		try (BufferedWriter writer = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream(Paths.get(dirPath + "/effectiveness-nums-data.txt").toFile(), true), "UTF-8"))) {
			writer.write(anovaNumCorrect.toString());
			
		} catch (Exception e) {
			System.err.println(e);
			LOGGER.error(e.getMessage());
		}
		
		try (BufferedWriter writer = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream(Paths.get(dirPath + "/effectiveness-precesion-data.txt").toFile(), true), "UTF-8"))) {
			writer.write(anovaWeightedPrecision.toString());
			
		} catch (Exception e) {
			System.err.println(e);
			LOGGER.error(e.getMessage());
		}
		
		try (BufferedWriter writer = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream(Paths.get(dirPath + "/effectiveness-recall-data.txt").toFile(), true), "UTF-8"))) {
			writer.write(anovaWeightedRecall.toString());
			
		} catch (Exception e) {
			System.err.println(e);
			LOGGER.error(e.getMessage());
		}
		
		try (BufferedWriter writer = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream(Paths.get(dirPath + "/effectiveness-f-data.txt").toFile(), true), "UTF-8"))) {
			writer.write(anovaWeightedFMeasure.toString());
			
		} catch (Exception e) {
			System.err.println(e);
			LOGGER.error(e.getMessage());
		}
		
		try (BufferedWriter writer = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream(Paths.get(dirPath + "/effectiveness-macro-data.txt").toFile(), true), "UTF-8"))) {
			writer.write(anovaUnweightedMacroFmeasure.toString());
			
		} catch (Exception e) {
			System.err.println(e);
			LOGGER.error(e.getMessage());
		}
		
		try (BufferedWriter writer = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream(Paths.get(dirPath + "/effectiveness-micro-data.txt").toFile(), true), "UTF-8"))) {
			writer.write(anovaUnweightedMicroFmeasure.toString());
			
		} catch (Exception e) {
			System.err.println(e);
			LOGGER.error(e.getMessage());
		}
	}
}
