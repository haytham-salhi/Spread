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
			Location locationOfFetching) {
		
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
		
		// TODO change to size variable below later
		//fetchAndStore(fetchInnerPage, 100, queries, SearchEngineCode.GOOGLE, googleFetcher, lang, searchEngineLanguage, locationOfFetching);
		
		fetchAndStore(fetchInnerPage, 200, queries, SearchEngineCode.BING, bingFetcher, lang, searchEngineLanguage, locationOfFetching);
		
	}

	private void fetchAndStore(boolean fetchInnerPage,
			int size,
			Map<String, List<Meaning>> queries,
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
		
		int processedQueries = 0; // all queries including the clear queries
		
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
//					InnerPage innerPage = null;
//					String innerPageAsString = searchItem.getInnerPage();
//					if(innerPageAsString != null && !innerPageAsString.isEmpty()) {
//						if(innerPageAsString.length() < 134_217_728) { // 134_217_728 ~ 512 MB string!!
//							innerPage = new InnerPage(innerPageAsString);
//							try {
//								innerPage = innerPageRepository.save(innerPage);
//							} catch (Exception e) {
//								LOGGER.error(ExceptionUtils.getStackTrace(e));
//								innerPage = null;
//							}
//						} else {
//							LOGGER.info("Inner page is too long!");
//						}
//					}
					
					com.spread.persistence.rds.model.SearchResult searchResultItem = new com.spread.persistence.rds.model.SearchResult(ambiguousQuerySearchEngine, searchItem.getTitle(), searchItem.getUrl(), searchItem.getShortSummary(), searchItem.getInnerPage());
					
					searchResultRepository.save(searchResultItem);
				}
				// #### End: The 2nd step

			} else { // Otherwise, skip
				LOGGER.info("This ambiguous query was already processed! " + ambiguousQuery);
			}
			LOGGER.info("Start processing for the meanings of " + ambiguousQuery);
			
			processedQueries++;
			LOGGER.info("Number of queries processed (including the clear queries) --------> " + processedQueries);
			
			// Get the senses now 
			List<Meaning> meanings = queries.get(ambiguousQuery);
			for (Meaning meaning : meanings) {
				
				// TODO Make sure that the meaning exists in meaning table (to handle the case if you add a new meaning later)
				// You need to get the meaning by amb_query_id and name 
				QueryFormulationStartegy queryFormulationStartegy = meaning.getFormulationStartegy();
				
				LOGGER.info("Formulating the meaning: " + meaning);

				// Formulate a query based on the startegy
				String clearQuery = formulateQuery(ambiguousQuery, meaning, lang);
				
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
						
						// Update meaning // TODO Needs testing :)) (Didn't work !!)
						meaningRepository.setClearQueryFor(parentQuery.getId(), meaning.getName(), query.getId());
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
//						InnerPage innerPage = null;
//						String innerPageAsString = searchItem.getInnerPage();
//						if(innerPageAsString != null && !innerPageAsString.isEmpty()) {
//							if(innerPageAsString.length() < 134_217_728) { // 134_217_728 ~ 512 MB string!!
//								innerPage = new InnerPage(innerPageAsString);
//								try {
//									innerPage = innerPageRepository.save(innerPage);
//								} catch (Exception e) {
//									LOGGER.error(ExceptionUtils.getStackTrace(e));
//									innerPage = null;
//								}
//							} else {
//								LOGGER.info("Inner page is too long!");
//							}
//						}
						
						com.spread.persistence.rds.model.SearchResult searchResultItem = new com.spread.persistence.rds.model.SearchResult(clearQuerySearchEngine, searchItem.getTitle(), searchItem.getUrl(), searchItem.getShortSummary(), searchItem.getInnerPage());
						
						searchResultRepository.save(searchResultItem);
					}
				} else { // else skip
					LOGGER.info("This clear query was already processed! " + clearQuery);
				}
				
				processedQueries++;
				LOGGER.info("Number of queries processed (including the clear queries) --------> " + processedQueries);
			}
		}
	}

	private String formulateQuery(
			String ambiguousQuery, Meaning meaning, Language lang) {
		String query;
		if(meaning.getFormulationStartegy() == QueryFormulationStartegy.APPEND) {
			if(lang == Language.AR) {
				query = meaning.getName() + " " + ambiguousQuery; // == APPEND_RIGHT (done this because it is arabic)
			} else if (lang == Language.EN) {
				query = ambiguousQuery + " " + meaning.getName(); // == APPEND_RIGHT (done this because it is english)
			} else {
				query = ambiguousQuery + " " + meaning.getName();
			}
		//} else if(meaning.getFormulationStartegy() == QueryFormulationStartegy.APPEND_RIGHT) {
			//query = meaning.getName() + " " + ambiguousQuery;
		//} else if(meaning.getFormulationStartegy() == QueryFormulationStartegy.APPEND_LEFT) {
			//query = ambiguousQuery + " " + meaning.getName();
		} else if(meaning.getFormulationStartegy() == QueryFormulationStartegy.NO_APPEND) {
			query = meaning.getName();
		} else {
			query = meaning.getName();
		}
		
		return query;
	}

}
