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
import com.spread.config.others.MongoConfig;
import com.spread.persistence.nosql.model.InnerPage;
import com.spread.persistence.nosql.repository.InnerPageRepository;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.persistence.rds.repository.TestRepository;

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class NoSqlTest {
	
	private static final Logger logger = LogManager.getLogger(NoSqlTest.class);
	
	@Autowired
	private InnerPageRepository innerPageRepository;
	
	@Autowired
	private SearchResultRepository searchResultRepository;

	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSpring() {
		logger.info("Hello");
		
		InnerPage page = new InnerPage("<html>الله أكبر ولله الحمد</html>");
		page = innerPageRepository.save(page);
		
		System.out.println(page);
	}
	
	@Test
	public void migrationFromMongoToMySql() throws Exception {
		List<SearchResult> res = searchResultRepository.findByInnerPageIdIsNotNullAndInnerPageIsNull();
		
		int i = 0;
		
		for (SearchResult searchResult : res) {
			InnerPage innerPage = innerPageRepository.findOne(searchResult.getInnerPageId());
			
			searchResultRepository.setInnerPageFor(innerPage.getContent(), searchResult.getId());
			
			i++;
			
			System.out.println(i + " done");
		}
		
		//System.out.println(innerPageRepository.findOne("5830722bbe5256b8f87487b3"));;
	}
}
