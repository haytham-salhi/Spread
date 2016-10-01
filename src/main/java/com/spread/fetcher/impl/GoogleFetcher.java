package com.spread.fetcher.impl;

import java.io.IOException;
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

@Component
public class GoogleFetcher implements SearchEngineFetcher {
	
	private static final String NOT_FOUND = "not found";

	private static final Logger LOGGER = LogManager.getLogger(GoogleFetcher.class.getName());

	@Value("${google.custome.search.engine.url}")
	private String cseEndPoint;
	
	@Override
	public SearchResult fetch(String query) {
		LOGGER.trace("fetch: query=" + query);
		
		SearchResult searchResult = new SearchResult();
		
		// num can be 10 as well
		int num = 20;
		int start = 0;
		
		int pageNumber = 0;
		
		JsonNode cursorNode;
		do {
			// Prepare the endPoint
			String preparedEndPoint = cseEndPoint.replace("{num}", String.valueOf(num)).replace("{start}", String.valueOf(start)).replace("{query}", query);
			
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
			
			cursorNode = rootNode.path("cursor");
			
			// If empty, return empty
			if(cursorNode.path("pages").size() < 1) {
				LOGGER.info("No pages!");
				LOGGER.trace("fetch: retrurning");
				return searchResult;
			}
			
			LOGGER.info("Fetching data of with start=" + start + " and page=" + pageNumber);
			
			String resultsCount = cursorNode.path("resultCount").asText(NOT_FOUND);
			searchResult.setTotalResults(resultsCount);

			Iterator<JsonNode> searchElements = rootNode.path("results").elements();
			while (searchElements.hasNext()) {
				JsonNode searchElement = searchElements.next();
				
				String title = searchElement.path("titleNoFormatting").asText(NOT_FOUND);
				String url = searchElement.path("url").asText(NOT_FOUND);
				String snippet = searchElement.path("contentNoFormatting").asText(NOT_FOUND);
				
				// Create and add
				SearchItem searchItem = new SearchItem();
				searchItem.setTitle(title);
				searchItem.setUrl(url);
				searchItem.setShortSummary(snippet);

				searchResult.addSearchItem(searchItem);
			}
			
			start += num; // to loop over 20, 40, 60, 80
			pageNumber++;
			// Start from label 2 which is index 1 because you are already fetched the previous one
		} while (pageNumber < cursorNode.path("pages").size());
		
		
		LOGGER.trace("fetch: retrurning");
		return searchResult;
	}
	
	
}
