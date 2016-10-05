package com.spread.general;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class GeneralTest {
	
	private static final Logger logger = LogManager.getLogger(GeneralTest.class);

	
	@Autowired
	@Qualifier("googleCustomFetcher")
	SearchEngineFetcher googleFetcher;
	

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSpring() {
		logger.info("Hello");
		//System.out.println(googleFetcher.fetch(2));
	}
	
	@Test
	public void googleSearchApiTest() throws Exception {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		
		Customsearch customsearch = new Customsearch.Builder(httpTransport, jsonFactory, null).setApplicationName("ThesisKeyCSE").build();
		
		
		try {
			List cseList = customsearch.cse().list("Python");
			cseList.setKey("AIzaSyDfS2hy1QCxb85L8GSfS-iA4SgVp1OtZ38");
			cseList.setCx("011305709239177939329:h3wb8k8xtky");
			//cseList.setStart(80L);
			cseList.setNum(5L);
			cseList.setPrettyPrint(true);
			
			
			
			Search results = cseList.execute();
			
			if(results != null && !results.isEmpty()) {
				java.util.List<Result> resultItems = results.getItems();
				
				for (Result result : resultItems) {
					System.out.println(result);
					System.out.println("#############################");
				}
			}
			
			System.out.println(results);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Test
	public void googleSearchCseUrlTest() throws Exception {
		// Moved
	}
	
	@Test
	public void pingSearchFetchingTest() throws Exception {
		
	}
	
	// encode to application/x-www-form-urlencoded format 
	@Test
	public void testFromUtf8To() throws Exception {
		String query = "هيثم صالحي";
		System.out.println(query);
		
		try {
			System.out.println(URLEncoder.encode(query, "UTF-8"));
			
			//System.out.println(String.format(URLEncoder.encode(query, "UTF-8")));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Test
	public void testName() throws Exception {
		Document document = Jsoup.connect("https://www.google.com/search?q=haytham").timeout(30000).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").get();
		
		String countDes = document.select("div[id=\"resultStats\"]").first().text();
		System.out.println(countDes.substring(countDes.indexOf("About"), countDes.indexOf("results")));

	}
}
