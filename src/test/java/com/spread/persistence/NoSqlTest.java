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
import com.spread.config.others.MongoConfig;
import com.spread.persistence.nosql.model.InnerPage;
import com.spread.persistence.nosql.repository.InnerPageRepository;
import com.spread.persistence.rds.repository.TestRepository;

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class NoSqlTest {
	
	private static final Logger logger = LogManager.getLogger(NoSqlTest.class);
	
	@Autowired
	private InnerPageRepository innerPageRepository;

	
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
	public void testName() throws Exception {
		System.out.println(innerPageRepository.findOne("581238e9a6c6b822be2e4106"));
	}
}
