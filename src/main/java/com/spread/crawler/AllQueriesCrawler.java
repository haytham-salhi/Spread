package com.spread.crawler;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.spread.fetcher.SearchEngineFetcher;
import com.spread.model.SearchItem;
import com.spread.model.SearchResult;
import com.spread.model.Meaning;
import com.spread.persistence.nosql.model.InnerPage;
import com.spread.persistence.nosql.repository.InnerPageRepository;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.QuerySearchEngine;
import com.spread.persistence.rds.model.SearchEngine;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.QueryFormulationStartegy;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.QuerySearchEngineRepository;
import com.spread.persistence.rds.repository.SearchEngineRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;

/**
 * 
 * @author Haytham Salhi
 *
 */
// Mode 2
@Component("MODE-2")
public class AllQueriesCrawler extends Crawler {
	
	private static final Logger LOGGER = LogManager.getLogger(AllQueriesCrawler.class);
	
	@Autowired
	private QuerySearchEngineRepository querySearchEngineRepository;
	
	@Autowired
	private SearchEngineRepository searchEngineRepository;
	
	@Autowired
	private QueryRepository queryRepository;
	
	@Autowired
	private InnerPageRepository innerPageRepository;
	
	@Autowired
	private SearchResultRepository searchResultRepository;
	
	@Autowired
	private MeaningRepository meaningRepository;
	
	@Autowired
	@Qualifier("googleCustomFetcher")
	private SearchEngineFetcher googleFetcher;
	
	@Autowired
	@Qualifier("bingFetcher")
	private SearchEngineFetcher bingFetcher;
	
	@Override
	public void handleQueries(
			boolean fetchInnerPage,
			int size,
			Map<String, List<Meaning>> queries,
			Language lang,
			SearchEngineLanguage searchEngineLanguage,
			Location locationOfFetching,
			QueryFormulationStartegy queryFormulationStartegy) {
		
		// For all search engines
		
		// For each ambiguous query
			// Check if the by (query + selang + loc + se + query formulation) has entries in query search engine table
			// In other words if (q_id and se_id + startegy) exists in query_search_engine table then don't fetch!
			// If yes
				// Then skip
			// If no 
				// Then fetch its results and store
			// Get the list of senses
				// For each sense
				// Check if (q_id and se_id + startegy) exists in query_search_engine table
		
		fetchAndStore(fetchInnerPage, size, queries, queryFormulationStartegy, SearchEngineCode.GOOGLE, googleFetcher, lang, searchEngineLanguage, locationOfFetching);
		
		//fetchAndStore(fetchInnerPage, size, queries, queryFormulationStartegy, SearchEngineCode.BING, bingFetcher, lang, searchEngineLanguage, locationOfFetching);
		
	}

	private void fetchAndStore(boolean fetchInnerPage,
			int size,
			Map<String, List<Meaning>> queries,
			QueryFormulationStartegy queryFormulationStartegy,
			SearchEngineCode sec,
			SearchEngineFetcher fetcher,
			Language lang, 
			SearchEngineLanguage searchEngineLanguage,
			Location locationOfFetching) {
		if(queries == null) {
			LOGGER.info("Queries list is empty!");
		}
		
		LOGGER.info("Getting the search engine with the details: code=" + sec + ", searchEngineLanguage=" + searchEngineLanguage + ", locationOfFetching=" + locationOfFetching);
		SearchEngine searchEngine = searchEngineRepository.findByCodeAndLanguageAndLocation(sec, searchEngineLanguage, locationOfFetching);
		
		if(searchEngine != null) {
			LOGGER.info("Found!");
		} else {
			LOGGER.info("Not found! Exiting...");
			return;
		}
		
		for (String ambiguousQuery : queries.keySet()) {
			
			LOGGER.info("Start processing for ambiguous query: " + ambiguousQuery);
			
			// Find the unqiue query-qse-se records for ambigous query where formulation_startegt is null (i.e., Not Applicable)
			QuerySearchEngine ambiguousQuerySearchEngine = querySearchEngineRepository.find(ambiguousQuery, sec, searchEngineLanguage, locationOfFetching);
			
			if(ambiguousQuerySearchEngine == null) {
				
				LOGGER.info("The ambiguous query has no unqiue query-qse-se record! Storing the query and its meanings and QSE...");
				
				// ##### Begin: The first step is to store the query combined with meaning and QSE
				// Store QSE record
				// Create and store the query
				// If the query exists, then don't stroe it with the meaning again
				Query query = queryRepository.findByName(ambiguousQuery);
				if(query == null) {
					query = new Query(ambiguousQuery, lang, true, null, null);
					query = queryRepository.save(query);
					
					// Store the meanings
					List<Meaning> meanings = queries.get(ambiguousQuery);
					for (Meaning meaning : meanings) {
						meaningRepository.save(new com.spread.persistence.rds.model.Meaning(meaning.getName(), meaning.getDecription(), meaning.getClazz(), false, query));
					}
				}
				// ##### End: The first step
				
				// #### Begin: The 2nd step
				// Create and store the QSE
				ambiguousQuerySearchEngine = new QuerySearchEngine(query, searchEngine);
				ambiguousQuerySearchEngine = querySearchEngineRepository.save(ambiguousQuerySearchEngine);
				
				LOGGER.info("Fetching the search results...");
				
				// Fetch
				SearchResult searchResult = fetcher.fetch(ambiguousQuery, size, fetchInnerPage);
				
				LOGGER.info("Storing...");
				
				if(searchResult.getSearchItemsSize() == 0) {
					LOGGER.info("Fetched search items are 0!!!! Why?");
					LOGGER.error("Fetched search items are 0!!!! Why?");
				}
				
				// Store the items 
				for (SearchItem searchItem : searchResult.getSearchItems()) {
					// Store the inner page in mongo
					InnerPage innerPage = null;
					// TODO
					//String innerPageAsString = searchItem.getInnerPage();
					String innerPageAsString = searchItem.getShortSummary();
					if(innerPageAsString != null && !innerPageAsString.isEmpty()) {
						innerPage = new InnerPage(innerPageAsString);
						innerPage = innerPageRepository.save(innerPage);
					}
					
					com.spread.persistence.rds.model.SearchResult searchResultItem = new com.spread.persistence.rds.model.SearchResult(searchItem.getTitle(), searchItem.getUrl(), searchItem.getShortSummary(), innerPage != null ? innerPage.getId() : null, ambiguousQuerySearchEngine);
					
					searchResultRepository.save(searchResultItem);
				}
				// #### End: The 2nd step

			} // Otherwise, skip
			
			LOGGER.info("Start processing for the meanings of " + ambiguousQuery);
			
			// Get the senses now 
			List<Meaning> meanings = queries.get(ambiguousQuery);
			for (Meaning meaning : meanings) {
				
				LOGGER.info("Formulating the meaning: " + meaning);

				// Formulate a query based on the startegy
				String clearQuery = formulateQuery(queryFormulationStartegy, ambiguousQuery, meaning);
				
				LOGGER.info("The clear query after formulating: " + clearQuery);
				
				// Find the unqiue query-qse-se records for CLEAR query where formulation_startegt is null (i.e., Not Applicable)
				QuerySearchEngine clearQuerySearchEngine = querySearchEngineRepository.find(clearQuery, queryFormulationStartegy, sec, searchEngineLanguage, locationOfFetching);
				
				if(clearQuerySearchEngine == null) {
					
					LOGGER.info("The clear query has no unqiue query-qse-se record! Storing the query and its QSE...");

					// ##### Begin: The first step is to store the query combined with QSE only
					// Create and store the query
					// Get the parent (ambiguous)
					Query parentQuery = queryRepository.findByName(ambiguousQuery);
					// You might check if it exists or not
					Query query = queryRepository.findByName(clearQuery); // To get by name is sufficient here because (Java Prog Lang, Apppend) or (Prog Lnag, No Apped) will definitely differ in name 
					if(query == null) {
						query = new Query(clearQuery, lang, false, queryFormulationStartegy, parentQuery);
						query = queryRepository.save(query);
					}
					
					// ##### End: The first step
					
					// #### Begin: The 2nd step
					clearQuerySearchEngine = new QuerySearchEngine(query, searchEngine);
					clearQuerySearchEngine = querySearchEngineRepository.save(clearQuerySearchEngine);
					
					LOGGER.info("Fetching the search results...");
					
					// Fetch
					SearchResult searchResult = fetcher.fetch(clearQuery, size, fetchInnerPage);
					
					LOGGER.info("Storing...");
					
					if(searchResult.getSearchItemsSize() == 0) {
						LOGGER.info("Fetched search items are 0!!!! Why?");
						LOGGER.error("Fetched search items are 0!!!! Why?");
					}
					
					// Store the items 
					for (SearchItem searchItem : searchResult.getSearchItems()) {
						// Store the inner page in mongo
						InnerPage innerPage = null;
						// TODO
						//String innerPageAsString = searchItem.getInnerPage();
						String innerPageAsString = searchItem.getShortSummary();
						if(innerPageAsString != null && !innerPageAsString.isEmpty()) {
							innerPage = new InnerPage(innerPageAsString);
							innerPage = innerPageRepository.save(innerPage);
						}
						
						com.spread.persistence.rds.model.SearchResult searchResultItem = new com.spread.persistence.rds.model.SearchResult(searchItem.getTitle(), searchItem.getUrl(), searchItem.getShortSummary(), innerPage != null ? innerPage.getId() : null, clearQuerySearchEngine);
						
						searchResultRepository.save(searchResultItem);
					}
				} // else skip
			}
		}
	}

	private String formulateQuery(
			QueryFormulationStartegy queryFormulationStartegy,
			String ambiguousQuery, Meaning meaning) {
		String query;
		if(queryFormulationStartegy == QueryFormulationStartegy.APPEND) {
			query = ambiguousQuery + " " + meaning.getName();
		} else if(queryFormulationStartegy == QueryFormulationStartegy.NO_APPEND) {
			query = meaning.getName();
		} else {
			query = meaning.getName();
		}
		
		return query;
	}

}
