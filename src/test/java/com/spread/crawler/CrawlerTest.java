package com.spread.crawler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.spread.config.RootConfig;
import com.spread.data.FileDataLoader;
import com.spread.model.Meaning;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.QueryFormulationStartegy;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class CrawlerTest {
	
	@Autowired
	@Qualifier("MODE-2")
	private Crawler crawler;
	
	@Autowired
	@Qualifier("FILE-AR")
	private FileDataLoader arabicDataLoader;
	

	@Before
	public void setUp() throws Exception {
		System.out.println("Hello Jenkinssssssss");
	}
	
	@Test
	public void googleCustomFetcherTest() throws Exception {
		Map<String, List<Meaning>> queries = new HashMap<String, List<Meaning>>();
		queries.put("عمان", 
				Arrays.asList(new Meaning[] {
						new Meaning("سلطنة", "دولة عربية تقع في آسيا", "country", QueryFormulationStartegy.APPEND), 
						new Meaning("مدينة", "عاصمة الأردن", "city", QueryFormulationStartegy.APPEND)}));
		
		crawler.handleQueries(true,
		15,
		queries,
		Language.AR,
		SearchEngineLanguage.DEFAULT,
		Location.PALESTINE);
		
		
		
		queries = new HashMap<String, List<Meaning>>();
		queries.put("Java", 
				Arrays.asList(new Meaning[] {
						new Meaning("Programming Language", "Popular Programming Language", "programming language", QueryFormulationStartegy.APPEND), 
						new Meaning("Island", "An island of Indonesia", "location", QueryFormulationStartegy.APPEND)}));
		
		crawler.handleQueries(true,
		15,
		queries,
		Language.EN,
		SearchEngineLanguage.DEFAULT,
		Location.PALESTINE);
	}
	
	@Test
	public void firstTakenCrawler() throws Exception {
		Map<String, List<Meaning>> queries = arabicDataLoader.loadQueries2();
		
		crawler.handleQueries(true,
		100,
		queries,
		Language.AR,
		SearchEngineLanguage.AR, // Make sure to enable it in the application.pros to activate it (Just symbolic here)
		Location.PALESTINE); // Make sure you run from the specified country (ip address in that country)

		
	}
	
}