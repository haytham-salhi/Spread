package com.spread.fetcher.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spread.fetcher.SearchEngineFetcher;
import com.spread.model.SearchItem;
import com.spread.model.SearchResult;
/**
 * Implemented by parsing a JSON response
 * 
 * @author Haytham Salhi
 *
 */
@Component
public class GoogleCustomAPIFetcher extends BaseFetcher implements SearchEngineFetcher {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1799458810192899640L;

	/* Begin: Place Holders Variables */
	private static final String QUERY_PLACE_HOLDER = "{query}";

	private static final String START_PLACE_HOLDER = "{start}";

	private static final String NUM_PLACE_HOLDER = "{num}";
	/* End: Place Holders Variables */
	
	/* Begin: JSON Path Variables */
	
	/* End: JSON Path Variables */
	
	private static final Logger LOGGER = LogManager.getLogger(GoogleCustomAPIFetcher.class.getName());

	@Value("${google.custom.search.engine.url}")
	private String cseEndPoint;
	
	@Override
	public SearchResult fetch(String query, boolean fetchInnerPage) {
		LOGGER.trace("query=" + query);
		
		SearchResult results = fetch(query, 100, fetchInnerPage);
		
		LOGGER.trace("retrurning");
		return results;
	}

	@Override
	public SearchResult fetch(String query, int maxNumOfResultsToFetch, boolean fetchInnerPage) {
		LOGGER.trace("query=" + query + ", maxNumOfResultsToFetch=" + maxNumOfResultsToFetch);
		
		SearchResult searchResult = new SearchResult();
		
		// num can be 10 as well, let it be 20 to reduce the calls numbers
		int num = 10;
		// The max number of results that can be fetched is 100
		
		int actualNumberOfItemsFetched = 0; // Counter
		
		int start = 1;
		int pageNumber = 0;
		
		// Don't encode the query here as Google will be unhappy
		
		LOGGER.info("Encoded query: " + query);
		
		// Prepare the endPoint
		String preparedEndPoint = cseEndPoint.replace(NUM_PLACE_HOLDER, String.valueOf(num)).replace(START_PLACE_HOLDER, String.valueOf(start)).replace(QUERY_PLACE_HOLDER, query);
		
		RestTemplate restTemplate = new RestTemplate();
		String jsonResponse = restTemplate.getForObject(preparedEndPoint, String.class);
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = null;
		
		try {
			rootNode = objectMapper.readTree(jsonResponse);
		} catch (JsonProcessingException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
		
		String resultsCount = rootNode.path("searchInformation").path("totalResults").asText(NOT_FOUND);
		searchResult.setTotalResults(resultsCount);
		
		//int numberOfPages = cursorNode.path(NUMBER_OF_PAGES_JSON_PATH).size();
		
			//LOGGER.info("No pages!!");
			
		JsonNode itemsPath = rootNode.path("items");
		
		while(!itemsPath.isMissingNode()) {
			LOGGER.info("Fetching data of with start=" + start + " and page=" + pageNumber);
			
			Iterator<JsonNode> searchElements = itemsPath.elements();
			while (searchElements.hasNext() && actualNumberOfItemsFetched < maxNumOfResultsToFetch) {
				JsonNode searchElement = searchElements.next();
				
				parseAndAddToSearchResult(searchResult, searchElement, fetchInnerPage);
				
				// Accumulate the fetched items
				actualNumberOfItemsFetched++;
			}
			
			start += num; // To loop over 20, 40, 60, 80
			pageNumber++;
			
			if(pageNumber > 9) {
				// DO not proceed and break
				break;
			}
			
			preparedEndPoint = cseEndPoint.replace(NUM_PLACE_HOLDER, String.valueOf(num)).replace(START_PLACE_HOLDER, String.valueOf(start)).replace(QUERY_PLACE_HOLDER, query);
			jsonResponse = restTemplate.getForObject(preparedEndPoint, String.class);
			
			objectMapper = new ObjectMapper();
			rootNode = null;
			try {
				rootNode = objectMapper.readTree(jsonResponse);
			} catch (JsonProcessingException e) {
				LOGGER.error(ExceptionUtils.getStackTrace(e));
				
				LOGGER.trace("retrurning");
				return searchResult;
			} catch (IOException e) {
				LOGGER.error(ExceptionUtils.getStackTrace(e));
				
				LOGGER.trace("retrurning");
				return searchResult;
			}
			
			itemsPath = rootNode.path("items");
		}
			
		
		LOGGER.trace("retrurning");
		return searchResult;
	}

	private void parseAndAddToSearchResult(SearchResult searchResult,
			JsonNode searchElement, boolean fetchInnerPage) {
		String title = searchElement.path("title").asText(NOT_FOUND);
		String url = searchElement.path("link").asText(NOT_FOUND).trim();
		String snippet = searchElement.path("snippet").asText(NOT_FOUND);
		String formattedUrl = searchElement.path("formattedUrl").asText(NOT_FOUND);
		
		// Decode the url
		try {
			url = URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
		
		// Create and add
		SearchItem searchItem = new SearchItem();
		searchItem.setTitle(title);
		searchItem.setCite(formattedUrl);
		searchItem.setUrl(url);
		searchItem.setShortSummary(snippet);
		
		if(fetchInnerPage) {
			searchItem.setInnerPage(getInnerPage(url));
		}

		searchResult.addSearchItem(searchItem);
	}
	
	
	/**
	 * A do while implementation
	 */
//	@Override
//	public SearchResult fetch(String query) {
//		LOGGER.trace("fetch: query=" + query);
//		
//		SearchResult searchResult = new SearchResult();
//		
//		// num can be 10 as well
//		int num = 20;
//		int start = 0;
//		
//		boolean ranOnce = false;
//		
//		int pageNumber = 0;
//		
//		JsonNode cursorNode;
//		do {
//			// Prepare the endPoint
//			String preparedEndPoint = cseEndPoint.replace("{num}", String.valueOf(num)).replace("{start}", String.valueOf(start)).replace("{query}", query);
//			
//			RestTemplate restTemplate = new RestTemplate();
//			String jsonResponse = restTemplate.getForObject(preparedEndPoint, String.class);
//			
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode rootNode = null;
//			
//			try {
//				rootNode = objectMapper.readTree(jsonResponse);
//			} catch (JsonProcessingException e) {
//				LOGGER.error(ExceptionUtils.getStackTrace(e));
//			} catch (IOException e) {
//				LOGGER.error(ExceptionUtils.getStackTrace(e));
//			}
//			
//			cursorNode = rootNode.path("cursor");
//			
//			// If empty, return empty
//			if(cursorNode.path("pages").size() < 1) {
//				LOGGER.info("No pages!");
//				LOGGER.trace("fetch: retrurning");
//				return searchResult;
//			}
//			
//			LOGGER.info("Fetching data of with start=" + start + " and page=" + pageNumber);
//			
//			if(!ranOnce) {
//				String resultsCount = cursorNode.path("resultCount").asText(NOT_FOUND);
//				searchResult.setTotalResults(resultsCount);
//				
//				// Don't run them again
//				ranOnce = true;
//			}
//
//			Iterator<JsonNode> searchElements = rootNode.path("results").elements();
//			while (searchElements.hasNext()) {
//				JsonNode searchElement = searchElements.next();
//				
//				String title = searchElement.path("titleNoFormatting").asText(NOT_FOUND);
//				String url = searchElement.path("url").asText(NOT_FOUND);
//				String snippet = searchElement.path("contentNoFormatting").asText(NOT_FOUND);
//				
//				// Create and add
//				SearchItem searchItem = new SearchItem();
//				searchItem.setTitle(title);
//				searchItem.setUrl(url);
//				searchItem.setShortSummary(snippet);
//
//				searchResult.addSearchItem(searchItem);
//			}
//			
//			start += num; // to loop over 20, 40, 60, 80
//			pageNumber++;
//			// Start from label 2 which is index 1 because you are already fetched the previous one
//		} while (pageNumber < cursorNode.path("pages").size());
//		
//		
//		LOGGER.trace("fetch: retrurning");
//		return searchResult;
//	}
}
