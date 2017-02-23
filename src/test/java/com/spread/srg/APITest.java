package com.spread.srg;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
public class APITest {
	
	
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
	public void dataExploration() throws Exception {
		
		int ambiguousQuerySelectedByUser = 1;
		List<Meaning> clearMeaningsWithClearQueriesForAq = meaningRepository.findMeaningsWithClearQueries(ambiguousQuerySelectedByUser); 
		
		for (Meaning meaning : clearMeaningsWithClearQueriesForAq) {
			System.out.println(meaning.getClazz() + " " + meaning.getName() + " " + meaning.getClearQuery());
		}

		// ------------- Data 
		// This will be the input for preparation
		List<Integer> clearQueryIds = clearMeaningsWithClearQueriesForAq.stream().map(n -> n.getClearQuery().getId()).collect(Collectors.toList());
		List<RawSearchResult> rawSearchResults = data.getSearchResults(clearQueryIds, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, false, 2);
		//rawSearchResults.forEach(n -> System.out.println(n.getInnerPage()));
		System.out.println(rawSearchResults.size());
		
		// This will be the input for preparation
		List<String> meaningsList = data.getMeaningsForClearQueries(clearQueryIds);
		
		System.out.println(meaningsList);
		
	}
}
