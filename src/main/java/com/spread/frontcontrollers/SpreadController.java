package com.spread.frontcontrollers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spread.experiment.RawSearchResult;
import com.spread.experiment.data.stemmers.LightStemmer;
import com.spread.experiment.preparation.FeatureSelectionModes;
import com.spread.experiment.preparation.FeatureSpaceModes;
import com.spread.experiment.preparation.WClusteringPreprocessor;
import com.spread.fetcher.SearchEngineFetcher;
import com.spread.model.SearchItem;
import com.spread.model.SearchResult;
import com.spread.senses.KnowledgeBase;
import com.spread.senses.Wikipedia;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.stemmers.Stemmer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.StringToWordVector;

@RestController
@Scope("session")
@RequestMapping("/spread")
public class SpreadController implements Serializable {
	
	private static final long serialVersionUID = 3814562521768165031L;

	private static final Logger LOGGER = LogManager.getLogger(SpreadController.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	@Qualifier("googleCustomAPIFetcher")
	private SearchEngineFetcher googleFetcher;
	
	@Autowired
	@Qualifier("bingFetcher")
	private SearchEngineFetcher bingFetcher;
	

	@RequestMapping(value = {"/", ""}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<Integer, List<RawSearchResult>>> item5Run(
			@RequestParam(defaultValue= "عمان", required = false) String query,
			@RequestParam(defaultValue= "B", required = false) String engine) throws Exception {
		System.out.println(query);
		
		// 1. Get the senses of query from wikipedia
		KnowledgeBase knowledgeBase = new Wikipedia();
		List<String> senses = knowledgeBase.getSenses(query, "AR");
		if(senses == null || senses.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.OK); 
		}
		
		if(query.equals("عمان")) {
			senses.remove("عمان (نطاق أنترنت)");
		}
		
		// Map senses to numbers
		Map<String, String> sensesClassesMap = new HashMap<>();
		int i = 0;
		for (String sense : senses) {
			sensesClassesMap.put(sense, "Class" + i++);
		}
		
		// 2. Crawl search results for each sense from Google, Bing or Yahoo
		List<RawSearchResult> trainingRawSearchResults = new ArrayList<RawSearchResult>();
		i = 0;
		for (String sense : senses) {
			SearchResult searchResultSet = fetchSearchResults(sense, engine);
			//   new com.spread.persistence.rds.model.SearchResult(ambiguousQuerySearchEngine, searchItem.getTitle(), searchItem.getUrl(), searchItem.getShortSummary(), searchItem.getInnerPage());
			if(searchResultSet == null || searchResultSet.getSearchItemsSize() == 0) {
				System.err.println("Search results is 0. Why!!");
			}
			
			// 3. For search results of sense_i, convert them to raw search results and specify the meaning for each one
			for (SearchItem searchItem : searchResultSet.getSearchItems()) {
				trainingRawSearchResults.add(new RawSearchResult(i++, searchItem.getTitle(), searchItem.getUrl(), searchItem.getShortSummary(), searchItem.getInnerPage(), sensesClassesMap.get(sense), "N/A"));
			}
		}
		
		// 4. 
			// Build the supervised model
				// Specify featureSelectionSourceMode and featureSpaceMode
		FeatureSelectionModes featureSelectionMode = FeatureSelectionModes.TITLE_WITH_SNIPPET;
		FeatureSpaceModes featureSpaceMode = FeatureSpaceModes.SINGLE_WORDS;
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
		
		// Data 
		// List<RawSearchResult> trainingRawSearchResults = ... // This will represent all search results labeled for all senses
		List<String> meaningsList = new ArrayList<>(sensesClassesMap.values()); // This will be the input for preparation
		// Preparation
	 	WClusteringPreprocessor trainingDatasetPreprocesser = new WClusteringPreprocessor(trainingRawSearchResults, meaningsList, query, LOGGER);
	 	// 1. 
		trainingDatasetPreprocesser.prepare(featureSelectionMode, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval,
				nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval);
		
		// 2. This will prpeare the filter only, will not convert data into VSM, also, will make copy of training dataset with classatrr and one without clasattr
		StringToWordVector toVectorsfilter = trainingDatasetPreprocesser.getVsmFilter(countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, featureSpaceMode.getMin(), featureSpaceMode.getMax(), minTermFreqToKeep);	
	 	
		// Here just as instances, not in VSM,
		// This will be used for training the model
		Instances trainingDatasetAsInstancesWithNoClassAttr = trainingDatasetPreprocesser.getTrainingDataSet();
	 	
		// --------- Clustering
		// TODO Refactor this for other algorithms
		SimpleKMeans kmeansModel = new SimpleKMeans();
		int k = senses.size();
		kmeansModel.setNumClusters(k);
		kmeansModel.setDistanceFunction(new EuclideanDistance());
		// [CR]: Use k-means++ as an initialization method
		String[] opts= {"-init", "1"};
		kmeansModel.setOptions(opts);
		
		MultiFilter multiFilter = new MultiFilter();
		multiFilter.setFilters(new Filter[] {toVectorsfilter});
		
		FilteredClusterer fc = new FilteredClusterer();
		fc.setFilter(multiFilter);
		fc.setClusterer(kmeansModel);
		fc.buildClusterer(trainingDatasetAsInstancesWithNoClassAttr);
		
		// 5. Now cluster search results of the ambiguous query
		String NA = "NA";
		SearchResult ambiguousQuerySearchResultSet = fetchSearchResults(query, engine);
		// Convert them to raw
		List<RawSearchResult> testRawSearchResults = new ArrayList<>();
		if(ambiguousQuerySearchResultSet == null || ambiguousQuerySearchResultSet.getSearchItemsSize() == 0) {
			System.err.println("Amiguous Query Search results is 0. Why!!");
		}
		i = 0;
		for (SearchItem searchItem : ambiguousQuerySearchResultSet.getSearchItems()) {
			System.out.println(i + " " + searchItem);
			testRawSearchResults.add(new RawSearchResult(i++, searchItem.getTitle(), searchItem.getUrl(), searchItem.getShortSummary(), searchItem.getInnerPage(), NA, NA));
		}
		
		List<String> aqMeaningsList = Arrays.asList(new String[] {NA});
		// -------- Preparation
		WClusteringPreprocessor testDatasetPreprocessor = new WClusteringPreprocessor(testRawSearchResults, aqMeaningsList, query, LOGGER);
		// 1.
		testDatasetPreprocessor.prepare(featureSelectionMode, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval,
				nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQueryRemoval);
		// 2. No need to build vector space hoon aslan, la2enno serna nesta5dem el filtered
		// for test data, I called this just to make copy of training data, one with class atrr and one with no class att, also, to set wtk conditonally based on if its inner ppage or not
		testDatasetPreprocessor.getVsmFilter(countWords, wordsToKeep, wordsToKeepInCaseOfInnerPage, TF, IDF, featureSpaceMode.getMin(), featureSpaceMode.getMax(), minTermFreqToKeep);
		
		// This is to evaluate below i.e. to cluster them noramlly without class atrr 
		// becuase orginally there is no class for a.q search results
		Instances testDatasetAsInstancesWithNoClassAttr = testDatasetPreprocessor.getTrainingDataSet();

		
		// -----------------This code snippet can be removed from here, it was added later!--------------------------------------------
		// --------- Evaluate the training dta set just to see the classes-to-clusters of training data
		// Evaluate against labeled test data set
		System.out.println("------Training data clustering evaluation:------");
		ClusterEvaluation trainingEval = new ClusterEvaluation();
		trainingEval.setClusterer(fc);
		// Just instances without VSM
		Instances trainingDatasetAsInstancesWithClassAttr = trainingDatasetPreprocesser.getTrainingDataSetWithClassAttr();
		trainingDatasetAsInstancesWithClassAttr.setClassIndex(trainingDatasetAsInstancesWithClassAttr.numAttributes() - 1);
		
		trainingEval.evaluateClusterer(trainingDatasetAsInstancesWithClassAttr);
		String eval1String = trainingEval.clusterResultsToString(); 
		System.out.println(eval1String);
		
		System.out.println("Senses to classes map: " + sensesClassesMap);
		
		//----------------------------------------------------------------
		
		// --------- Evaluation
		// Evaluate against labeled test data set
		System.out.println("------Test data clustering evaluation:------");
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(fc);
		eval.evaluateClusterer(new Instances(testDatasetAsInstancesWithNoClassAttr));
		
		String evalString = eval.clusterResultsToString();
		System.out.println(evalString);
		
		
		Map<Integer, List<RawSearchResult>> clusteredTestSearchResults = new HashMap<>();
		
		// Create the clusters
		double[] assignments = eval.getClusterAssignments();
		
		for (int j = 0; j < assignments.length; j++) {
			int clusterNumber = (int) assignments[j];
			List<RawSearchResult> clusterList = clusteredTestSearchResults.get(clusterNumber);
			if(clusterList == null) {
				// Create and add
				clusterList = new ArrayList<>();
				clusterList.add(testRawSearchResults.get(j));
				clusteredTestSearchResults.put(clusterNumber, clusterList);
			} else {
				clusterList.add(testRawSearchResults.get(j));
			}
			
		}
		
		// Continue as in AQSupervisedExperiment
		return new ResponseEntity<>(clusteredTestSearchResults, HttpStatus.OK);
		
	}
	
	private SearchResult fetchSearchResults(String query, String engine) {
		SearchResult searchResultSet = null;
		System.out.println("Fetching for " + query + " from the engine " + engine);
		if(engine.equals("B")) {
			searchResultSet = bingFetcher.fetch(query, false);
		} else {
			searchResultSet = googleFetcher.fetch(query, false);
		}
		
		return searchResultSet;
	}
}
