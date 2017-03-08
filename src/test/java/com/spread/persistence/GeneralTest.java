package com.spread.persistence;

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
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.SearchEngine;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchEngineRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.persistence.rds.repository.TestRepository;
import com.spread.persistence.rds.repository.UserSearchResultAssessmentRepository;

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
	private UserSearchResultAssessmentRepository userSearchResultAssessmentRepository;

	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void findByQueryAndSearchEngineWithBasicInfoTest() throws Exception {
		List<SearchResult> results = searchResultRepository.findByQueryAndSearchEngineWithBasicInfo("عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
		
		results.forEach(System.out::println);
		System.out.println(results.size());
	}
	
	@Test
	public void SearchResultRepositoryTest() throws Exception {
		//searchResultRepository.findByQuerySearchEngine_Query_NameAndQuerySearchEngine_SearchEngine_Code("عمان", SearchEngineCode.GOOGLE);
		List<SearchResult> results = searchResultRepository.findByQueryAndSearchEngine("عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
		
		results.forEach(System.out::println);
		System.out.println(results.size());
	}

	@Test
	public void test2() {
		logger.info("Hello");
		//System.out.println(googleFetcher.fetch(2));
		
		com.spread.persistence.rds.model.Test t = new com.spread.persistence.rds.model.Test();
		t.setName("👽💔"); // Such these chars are solved after we append character_set_server=utf8mb4 and remove the characterEncoding=utf8
		
		testRepository.save(t); 
	}
	@Test
	public void test3() {
		logger.info("Hello");
		//System.out.println(googleFetcher.fetch(2));
		
		com.spread.persistence.rds.model.Test t = new com.spread.persistence.rds.model.Test();
		t.setName("عدي الصالحيddsda d 👽💔"); // Such these chars are solved after we append character_set_server=utf8mb4 and remove the characterEncoding=utf8
		
		testRepository.save(t); 
	}
	
	@Test
	public void test1() throws Exception {
		com.spread.persistence.rds.model.Test ob = testRepository.findOne(54);
		
		System.out.println(ob.getName());
	}
	
	@Test
	public void queryTest() throws Exception {
		Query query = new Query("الله أكبر ولله الحمد", Language.EN, true, null, null);
		
		query = queryRepository.save(query);
		
		System.out.println(query);
	}
	
	@Test
	public void findByCodeAndLanguageAndLocationTest() throws Exception {
		SearchEngine se = searchEngineRepository.findByCodeAndLanguageAndLocation(SearchEngineCode.GOOGLE, SearchEngineLanguage.AR, Location.PALESTINE);
		
		System.out.println(se);
	}
	
	@Test
	public void countRespondentsByQueryIdTest() throws Exception {
		System.out.println(userSearchResultAssessmentRepository.countRespondentsByQueryId(3, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR));
	}
}
