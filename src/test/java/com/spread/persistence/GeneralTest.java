package com.spread.persistence;

import static org.junit.Assert.*;

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
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.TestRepository;

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class GeneralTest {
	
	private static final Logger logger = LogManager.getLogger(GeneralTest.class);
	
	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private QueryRepository queryRepository;

	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test2() {
		logger.info("Hello");
		//System.out.println(googleFetcher.fetch(2));
		
		com.spread.persistence.rds.model.Test t = new com.spread.persistence.rds.model.Test();
		t.setName("عدي الصالحي");
		
		testRepository.save(t); 
	}
	
	@Test
	public void test1() throws Exception {
		com.spread.persistence.rds.model.Test ob = testRepository.findOne(10);
		
		System.out.println(ob.getName());
	}
	
	@Test
	public void queryTest() throws Exception {
		Query query = new Query("الله أكبر ولله الحمد", Language.EN, true, null);
		
		queryRepository.save(query);
	}
}
