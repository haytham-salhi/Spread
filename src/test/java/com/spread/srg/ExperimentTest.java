package com.spread.srg;

import static org.junit.Assert.*;

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

import com.spread.config.RootConfig;
import com.spread.experiment.RawSearchResult;
import com.spread.experiment.data.Data;
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
		List<RawSearchResult> rawSearchResults = data.getSearchResults(Arrays.asList(new Integer[] {2, 3, 4}), SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, true, 2);
		rawSearchResults.forEach(n -> System.out.println(n.getInnerPage()));
		System.out.println(rawSearchResults.size());
		
		// This will be the input for preparation
		List<String> meaningsList = data.getMeaningsForClearQueries(Arrays.asList(new Integer[] {2, 3, 4}));
		
		System.out.println(meaningsList);
		
		// Note that 2, 3, 4 ids the same for getSearchResults and getMeaningsForClearQuerirs of course :)
		
		
		// -------- Preparation
		WClusteringPreprocessor preprocessor = new WClusteringPreprocessor(rawSearchResults, meaningsList);
		
		preprocessor.prepare(FeatureSelectionModes.TITLE_ONLY);
		
		preprocessor.preprocessTrainingDataset(null, false, 1000, false, false);
		
		System.out.println(preprocessor.getTrainingDataSet());;
		
		
		// --------- Clustering
		
		
		
		// --------- Evaluation
		
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
