package com.spread.fetcher.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
public class YahooFetcher extends BaseFetcher implements SearchEngineFetcher {

	private static final long serialVersionUID = -3509508446307464331L;

	/* Begin: Place Holders Variables */
	private static final String QUERY_PLACE_HOLDER = "{query}";

	private static final String FIRST_PLACE_HOLDER = "{first}";
	/* End: Place Holders Variables */

	/* Begin: CSS Queries Variables */
	private static final String RESULTS_COUNT_CSS_QUERY = "ol.searchBottom span";

	private static final String RESULTS_CSS_QUERY = "ol.searchCenterMiddle li div.dd.algo";

	private static final String SNIPPET_CSS_QUERY = "div.compText";

	private static final String URL_CSS_QUERY = "h3.title a[href]";
	
	private static final String CITE_CSS_QUERY = "span.fz-ms";

	private static final String TITLE_CSS_QUERY = "h3.title";
	/* End: CSS Queries Variables */
	
	private static final Logger LOGGER = LogManager.getLogger(YahooFetcher.class.getName());
	
	@Value("${yahoo.search.engine.url}")
	private String endPoint;
	
	@Override
	public SearchResult fetch(String query, boolean fetchInnerPage) {
		LOGGER.trace("query=" + query);
		
		SearchResult results = fetch(query, 200, fetchInnerPage);
		
		LOGGER.trace("retrurning");
		return results;
	}
	
	@Override
	public SearchResult fetch(String query, int maxNumOfResultsToFetch, boolean fetchInnerPage) {
		LOGGER.trace("query=" + query + ", maxNumOfResultsToFetch=" + maxNumOfResultsToFetch);

		SearchResult searchResult = new SearchResult();
		
		// Let's fetch 10's
		int num = 10;
		
		// The start here differs from google as they start from 1, 11, 21, 31,..., 91, and so on
		int first = 1;
		int pageNumber = 0;
		int actualNumberOfItemsFetched = 0; // Counter
		boolean emptyResults = false;
		
		long totalCountAsInt = -1;
		
		try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
		}

		// Prepare the endPoint
		String preparedEndPoint = endPoint.replace(FIRST_PLACE_HOLDER, String.valueOf(first)).replace(QUERY_PLACE_HOLDER, query);
		
		System.out.println(preparedEndPoint);
		
		// Connect and fetch
		Document document = null;
		try {
			document = Jsoup.connect(preparedEndPoint).timeout(30000).userAgent(MY_PC_USERAGENT).get();
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
					emptyResults = true;
					LOGGER.info("Empty results!");
				}
				
				// An additional condition actualNumberOfItemsFetched < maxNumOfResultsToFetch is added here to make sure that we are not returning a value larger than it
				for (int i = 0; i < resultElements.size() && actualNumberOfItemsFetched < maxNumOfResultsToFetch; i++) {
					// Whenever you modify here, don't forget below
					Element resultElement = resultElements.get(i);
					parseAndAddToSearchResult(searchResult, resultElement, fetchInnerPage);
					
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
		while(first <= totalCountAsInt && actualNumberOfItemsFetched < maxNumOfResultsToFetch && !emptyResults) {
			LOGGER.info("Fetching data of with first=" + first + " and page=" + pageNumber);

			// Prepare the endPoint
			preparedEndPoint = endPoint.replace(FIRST_PLACE_HOLDER, String.valueOf(first)).replace(QUERY_PLACE_HOLDER, query);
			
			// Connect and fetch
			document = null; 
			try {
				document = Jsoup.connect(preparedEndPoint).timeout(30000).userAgent(MY_PC_USERAGENT).get();
			} catch (IOException e) {
				LOGGER.error(ExceptionUtils.getStackTrace(e));
			}
			
			if(document != null) {
				Elements resultElements = document.select(RESULTS_CSS_QUERY);
				
				if(resultElements.isEmpty()) {
					// No results
					emptyResults = true;

					LOGGER.info("Empty results!");
				}
				
				// An additional condition actualNumberOfItemsFetched < maxNumOfResultsToFetch is added here to make sure that we are not returning a value larger than it
				for (int i = 0; i < resultElements.size() && actualNumberOfItemsFetched < maxNumOfResultsToFetch; i++) {
					// Whenever you modify here, don't forget above
					Element resultElement = resultElements.get(i);
					parseAndAddToSearchResult(searchResult, resultElement, fetchInnerPage);
					
					// Accumulate the fetched items
					actualNumberOfItemsFetched++;
				}
			} else {
				LOGGER.info("Document is null. Giving opportunity to next page to be fetched!");
			}
			
			// IWf the document is null, give opportunity to next page. That's why these two lines are kept out of the above if
			first+= num; // to loop over 11, 21, 31, and so on
			pageNumber++;
		}
		
		LOGGER.trace("retrurning");
		return searchResult;
	}

	private void parseAndAddToSearchResult(SearchResult searchResult,
			Element resultElement, boolean fetchInnerPage) {
		String title = resultElement.select(TITLE_CSS_QUERY).text();
		String url = resultElement.select(URL_CSS_QUERY).attr("href").trim();
		String snippet = resultElement.select(SNIPPET_CSS_QUERY).text();
		String cite = resultElement.select(CITE_CSS_QUERY).text();
		
		// Decode the url
		try {
			url = URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
		
		// Create and add
		SearchItem searchItem = new SearchItem();
		searchItem.setTitle(title);
		searchItem.setCite(cite);
		searchItem.setUrl(url);
		searchItem.setShortSummary(snippet);
		
		if(fetchInnerPage) {
			searchItem.setInnerPage(getInnerPage(url));
		}

		searchResult.addSearchItem(searchItem);
	}
}
