package com.spread.srg;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.spread.config.RootConfig;
import com.spread.experiment.RawSearchResult;
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
public class GeneralTest {
	
	private static final Logger logger = LogManager.getLogger(GeneralTest.class);
	
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
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void SearchResultRepositoryTest() throws Exception {
		//searchResultRepository.findByQuerySearchEngine_Query_NameAndQuerySearchEngine_SearchEngine_Code("عمان", SearchEngineCode.GOOGLE);
		List<SearchResult> ambigQueryResults = searchResultRepository.findByQueryAndSearchEngine("عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
		List<SearchResult> clearQueryResults1 = searchResultRepository.findByQueryAndSearchEngine("مدينة عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
		List<SearchResult> clearQueryResults2 = searchResultRepository.findByQueryAndSearchEngine("سلطنة عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);

		//results.forEach(System.out::println);
		System.out.println(ambigQueryResults.size());
		System.out.println(clearQueryResults1.size());
		System.out.println(clearQueryResults2.size());

	}
	
	@Test
	public void testName() throws Exception {
		List<SearchResult> results = searchResultRepository.findByQueryAndSearchEngine("عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
		
		System.out.println(results.size());
		System.out.println(results.get(0).toString());
	}
	
	@Test
	public void testName1() throws Exception {
		Meaning meaning = meaningRepository.findByClearQuery_Id(2);
		
		System.out.println(meaning);
		System.out.println(meaning.getClearQuery());
	}
}
