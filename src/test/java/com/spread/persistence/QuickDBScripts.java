package com.spread.persistence;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.spread.config.RootConfig;

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class QuickDBScripts {
	
	private static final Logger logger = LogManager.getLogger(QuickDBScripts.class);
	
	@PersistenceContext(unitName = "spreadPU")
	private EntityManager entityManager;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test2() {
		
	}
}
