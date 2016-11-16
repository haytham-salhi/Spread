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
	
	

	@Before
	public void setUp() throws Exception {
		System.out.println("Hello Jenkinssssssss");
	}
	
	@Test
	public void googleCustomFetcherTest() throws Exception {
		Map<String, List<Meaning>> queries = new HashMap<String, List<Meaning>>();
		queries.put("عمان", 
				Arrays.asList(new Meaning[] {
						new Meaning("سلطنة عمان", "دولة عربية تقع في آسيا", "country"), 
						new Meaning("مدينة عمان", "عاصمة الأردن", "city")}));
		
		crawler.handleQueries(true,
		15,
		queries,
		Language.AR,
		SearchEngineLanguage.DEFAULT,
		Location.PALESTINE,
		QueryFormulationStartegy.NO_APPEND);
		
		
		
		queries = new HashMap<String, List<Meaning>>();
		queries.put("Java", 
				Arrays.asList(new Meaning[] {
						new Meaning("Programming Language", "Popular Programming Language", "programming language"), 
						new Meaning("Island", "An island of Indonesia", "location")}));
		
		crawler.handleQueries(true,
		15,
		queries,
		Language.EN,
		SearchEngineLanguage.DEFAULT,
		Location.PALESTINE,
		QueryFormulationStartegy.APPEND);
	}
	
}