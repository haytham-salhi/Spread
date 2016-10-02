package com.spread.fetcher.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.spread.fetcher.SearchEngineFetcher;
import com.spread.model.SearchItem;
import com.spread.model.SearchResult;

/**
 * Implemented by parsing HTML response
 * 
 * @author Haytham Salhi
 *
 */
@Component
public class BingFetcher implements SearchEngineFetcher {
	
	/* Begin: Place Holders Variables */
	private static final String QUERY_PLACE_HOLDER = "{query}";

	private static final String FIRST_PLACE_HOLDER = "{first}";
	/* End: Place Holders Variables */

	/* Begin: CSS Queries Variables */
	private static final String RESULTS_COUNT_CSS_QUERY = "div[id=\"b_content\"] > div[id=\"b_tween\"] > span.sb_count";

	private static final String RESULTS_CSS_QUERY = "li.b_algo";

	private static final String SNIPPET_CSS_QUERY = "li.b_algo p";

	private static final String URL_CSS_QUERY = "li.b_algo cite";

	private static final String TITLE_CSS_QUERY = "li.b_algo h2";
	/* End: CSS Queries Variables */
	
	private static final Logger LOGGER = LogManager.getLogger(BingFetcher.class.getName());
	
	@Value("${bing.search.engine.url}")
	private String endPoint;
	
	@Override
	public SearchResult fetch(String query) {
		LOGGER.trace("query=" + query);
		
		SearchResult results = fetch(query, 200);
		
		LOGGER.trace("retrurning");
		return results;
	}
	
	@Override
	public SearchResult fetch(String query, int maxNumOfResultsToFetch) {
		LOGGER.trace("query=" + query + ", maxNumOfResultsToFetch=" + maxNumOfResultsToFetch);

		SearchResult searchResult = new SearchResult();
		
		// Bing fetches data in 10's only, not as google as it has two options either in 10's or in 20's
		int num = 10;
		
		// The start here differs from google as they start from 1, 11, 21, 31,..., 91, and so on
		int first = 1;
		int pageNumber = 0;
		int actualNumberOfItemsFetched = 0; // Counter
		
		int totalCountAsInt = -1;
		
		try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
		}

		// Prepare the endPoint
		String preparedEndPoint = endPoint.replace(FIRST_PLACE_HOLDER, String.valueOf(first)).replace(QUERY_PLACE_HOLDER, query);
		
		// Connect and fetch
		Document document = null;
		try {
			document = Jsoup.connect(preparedEndPoint).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36").get();
		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			
			LOGGER.trace("retrurning");
			return searchResult;
		}
		
		// If empty, return empty
		Element countElement = document.select(RESULTS_COUNT_CSS_QUERY).first();
		
		if(countElement != null) { 
			// TODO Check it
			totalCountAsInt = Integer.parseInt(countElement.text().replaceAll("[^0-9]", ""));
			searchResult.setTotalResults(totalCountAsInt + "");
			
			if(totalCountAsInt > 0) {
				
				LOGGER.info("Fetching data of with first=" + first + " and page=" + pageNumber);
				
				Elements resultElements = document.select(RESULTS_CSS_QUERY);
				
				if(resultElements.isEmpty()) {
					// No results
					LOGGER.info("Empty results!");
					
					LOGGER.trace("retrurning");
					return searchResult;
				}
				
				// An additional condition actualNumberOfItemsFetched < maxNumOfResultsToFetch is added here to make sure that we are not returning a value larger than it
				for (int i = 0; i < resultElements.size() && actualNumberOfItemsFetched < maxNumOfResultsToFetch; i++) {
					String title = resultElements.get(i).select(TITLE_CSS_QUERY).text();
					String url = resultElements.get(i).select(URL_CSS_QUERY).text();
					String snippet = resultElements.get(i).select(SNIPPET_CSS_QUERY).text();
					
					// Create and add
					SearchItem searchItem = new SearchItem();
					searchItem.setTitle(title);
					searchItem.setUrl(url);
					searchItem.setShortSummary(snippet);

					searchResult.addSearchItem(searchItem);
					
					// Accumulate the fetched items
					actualNumberOfItemsFetched++;
				}
				
				first+= num; // to loop over 11, 21, 31, and so on
				pageNumber++;
				
			} else {
				LOGGER.info("No results!");
			}
		} else {
			LOGGER.info("No results!");
		}
		
		// Make sure you are fetching maxNumOfResultsToFetch or so
		while(first <= totalCountAsInt && actualNumberOfItemsFetched < maxNumOfResultsToFetch) {
			LOGGER.info("Fetching data of with first=" + first + " and page=" + pageNumber);

			// Prepare the endPoint
			preparedEndPoint = endPoint.replace(FIRST_PLACE_HOLDER, String.valueOf(first)).replace(QUERY_PLACE_HOLDER, query);
			
			// Connect and fetch
			try {
				document = Jsoup.connect(preparedEndPoint).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36").get();
			} catch (IOException e) {
				LOGGER.error(ExceptionUtils.getStackTrace(e));
				
				LOGGER.trace("retrurning");
				return searchResult;
			}
			
			Elements resultElements = document.select(RESULTS_CSS_QUERY);
			
			if(resultElements.isEmpty()) {
				// No results
				LOGGER.info("Empty results!");
				
				LOGGER.trace("retrurning");
				return searchResult;
			}
			
			// An additional condition actualNumberOfItemsFetched < maxNumOfResultsToFetch is added here to make sure that we are not returning a value larger than it
			for (int i = 0; i < resultElements.size() && actualNumberOfItemsFetched < maxNumOfResultsToFetch; i++) {
				String title = resultElements.get(i).select(TITLE_CSS_QUERY).text();
				String url = resultElements.get(i).select(URL_CSS_QUERY).text();
				String snippet = resultElements.get(i).select(SNIPPET_CSS_QUERY).text();
				
				// Create and add
				SearchItem searchItem = new SearchItem();
				searchItem.setTitle(title);
				searchItem.setUrl(url);
				searchItem.setShortSummary(snippet);

				searchResult.addSearchItem(searchItem);
				
				// Accumulate the fetched items
				actualNumberOfItemsFetched++;
			}
			
			first+= num; // to loop over 11, 21, 31, and so on
			pageNumber++;
		}
		
		LOGGER.trace("retrurning");
		return searchResult;
	}
}
