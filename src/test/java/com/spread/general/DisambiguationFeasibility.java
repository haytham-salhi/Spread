package com.spread.general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import com.spread.config.FetcherTestConfig;
import com.spread.fetcher.SearchEngineFetcher;
import com.spread.model.SearchItem;
import com.spread.model.SearchResult;

@ContextConfiguration(classes = { FetcherTestConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class DisambiguationFeasibility {
	
	private static final Logger logger = LogManager.getLogger(DisambiguationFeasibility.class);
	
	// googleCustomFetcher
	// bingFetcher
	@Autowired
	@Qualifier("googleCustomFetcher")
	private SearchEngineFetcher fetcher;
	

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		//String ambigousQuery = "عدنان ابراهيم";
		//List<String> meanings = Arrays.asList(new String[] {"مفكر فلسطيني", "مخرج عراقي"});
		
		String ambigousQuery = "الجامعة العربية";
		List<String> meanings = Arrays.asList(new String[] {"الجامعة العربية المفتوحة", "جامعة الدول العربية", "الجامعة العربية الامريكية جنين", "الجامعة العربية الدولية"});
		boolean appendAmbQueryWithMeaning = false;
		
		Map<String, SearchResult> meaningSearchResults = new HashMap<String, SearchResult>();
		
		// Fetch the main query
		SearchResult ambQueryResult = fetcher.fetch(ambigousQuery);
		System.out.println("Ambiguous Query: " + ambQueryResult.getSearchItems().size());
		
		// Fetch the meanings
		for (String meaning : meanings) {
			String formulatedQuery = meaning;
			
			if(appendAmbQueryWithMeaning)
				formulatedQuery = ambigousQuery + " " + meaning;
			
			SearchResult clearQueryResult = fetcher.fetch(formulatedQuery);
			System.out.println("Result size of " + formulatedQuery + " query: " + clearQueryResult.getSearchItems().size());
			
			meaningSearchResults.put(meaning, clearQueryResult);
		}
		
		// Find intersections
		for (String meaning : meaningSearchResults.keySet()) {
			List<String> commonsBetweenAmbAndClear = intersect(ambQueryResult.getSearchItems(), meaningSearchResults.get(meaning).getSearchItems());
			
			System.out.println("Number of common items between " + ambigousQuery + " and " + meaning + " is " + commonsBetweenAmbAndClear.size());
			System.out.println(commonsBetweenAmbAndClear);
		}
		
	}
	
	public List<String> intersect(List<SearchItem> searchItems1, List<SearchItem> searchItems2) {
		List<String> common = new ArrayList<String>();
		
		if(searchItems1 == null || searchItems1.isEmpty()
				|| searchItems2 == null || searchItems2.isEmpty()) {
			return common;
		}
		
		// List of urls
		List<String> startList;
		List<String> otherList;
		
		// Start with the shorter one
		if(searchItems2.size() < searchItems1.size()) {
			startList = searchItems2.stream().map(x -> x.getUrl()).collect(Collectors.toList());
			otherList = searchItems1.stream().map(x -> x.getUrl()).collect(Collectors.toList());
		} else {
			startList = searchItems1.stream().map(x -> x.getUrl()).collect(Collectors.toList());
			otherList = searchItems2.stream().map(x -> x.getUrl()).collect(Collectors.toList());
		}
		
		for (String url : startList) {
			if(otherList.contains(url)) {
				common.add(url);
			}
		}
		
		return common;
	}
	
	@Test
	public void googleSearchApiTest() throws Exception {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		
		Customsearch customsearch = new Customsearch.Builder(httpTransport, jsonFactory, null).setApplicationName("ThesisKeyCSE").build();
		
		// OMIS key: AIzaSyCHLDsUWX58Q-ptqs9UqHGG3qfzY8YE2j8
		// OMIS CX: 016425044002293401958:v2_yzgxsf-0
		
		String omisKey = "AIzaSyCHLDsUWX58Q-ptqs9UqHGG3qfzY8YE2j8";
		String omisCx = "016425044002293401958:v2_yzgxsf-0";
		
		// Spread Key: 
		// Spread CX: 
		String spreadKey = "AIzaSyDfS2hy1QCxb85L8GSfS-iA4SgVp1OtZ38";
		String spreadCx = "011305709239177939329:h3wb8k8xtky";
		
		try {
			com.google.api.services.customsearch.Customsearch.Cse.List cseList = customsearch.cse().list("عدنان ابراهيم");
			cseList.setKey(omisKey);
			cseList.setCx(omisCx);
			cseList.setStart(61L);
			cseList.setNum(10L);
			cseList.setPrettyPrint(true);
			//cseList.setFilter("0"); This to turn off filtering ;). That's why I got only 64 items for adnan ibrahim query 
			
			
			
			Search results = cseList.execute();
			
			
			if(results != null && !results.isEmpty()) {
				java.util.List<Result> resultItems = results.getItems();
				
				for (Result result : resultItems) {
					System.out.println(result);
					System.out.println("#############################");
				}
				
				System.out.println(resultItems.size());
			}
			
			//System.out.println(results);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
