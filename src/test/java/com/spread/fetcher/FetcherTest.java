package com.spread.fetcher;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.Customsearch.Cse.List;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import com.spread.config.RootConfig;
import com.spread.fetcher.SearchEngineFetcher;
import com.spread.frontcontrollers.HelloController;
import com.spread.model.SearchResult;

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FetcherTest {
	
	@Autowired
	@Qualifier("googleCustomFetcher")
	private SearchEngineFetcher googleCustomFetcher;
	
	@Autowired
	@Qualifier("bingFetcher")
	private SearchEngineFetcher bingFetcher;
	
	@Autowired
	@Qualifier("googleFetcher")
	private SearchEngineFetcher googleFetcher;
	
	

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void googleCustomFetcherTest() throws Exception {
		SearchResult result = googleCustomFetcher.fetch("Radi Jarrar");
		
		result.getSearchItems().forEach(System.out::println);
		
		System.out.println(result.getSearchItems().size());
		
		Thread.sleep(3000);
		
		SearchResult result1 = googleCustomFetcher.fetch("Haytham Salhi");
		
		result1.getSearchItems().forEach(System.out::println);
		
		System.out.println(result1.getSearchItems().size());
	}
	
	@Test
	public void bingFetcherTest() throws Exception {
		SearchResult result = bingFetcher.fetch("Adel");
		
		result.getSearchItems().forEach(System.out::println);
	}
	
	@Test
	public void googleFetcherTest() throws Exception {
		SearchResult result = googleFetcher.fetch("Haytham");
		
		result.getSearchItems().forEach(System.out::println);
		System.out.println(result.getSearchItems().size());
	}
}