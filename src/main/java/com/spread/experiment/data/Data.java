package com.spread.experiment.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.spread.experiment.RawSearchResult;
import com.spread.persistence.rds.model.Meaning;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;

/**
 * Base of Data API
 * 
 * @author Haytham Salhi
 *
 */
public abstract class Data {
	
	@Autowired
	private SearchResultRepository searchResultRepository;
	
	@Autowired
	protected MeaningRepository meaningRepository;
	
	public abstract List<RawSearchResult> labelTheResultsAndConvertToDtos(Integer queryId, List<SearchResult> searchResults) throws Exception;
	
	/**
	 * Get search results labeled for a specific query-search engine
	 * 
	 * @param queryId
	 * @param code
	 * @param location
	 * @param language
	 * @param withInnerPage
	 * @return
	 * @throws Exception 
	 */
	public List<RawSearchResult> getSearchResults(Integer queryId,
			SearchEngineCode code, Location location,
			SearchEngineLanguage language, boolean withInnerPage, int size) throws Exception {
		
		List<SearchResult> searchResults = null;
		
		Pageable pageRequest = new PageRequest(0, size);
		
		if(withInnerPage) {
			searchResults = searchResultRepository.findByQueryAndSearchEngine(queryId, code, location, language, pageRequest);
		} else {
			searchResults = searchResultRepository.findByQueryAndSearchEngineWithBasicInfo(queryId, code, location, language, pageRequest);
		}
		
		List<RawSearchResult> searchResultDtos;
		searchResultDtos = labelTheResultsAndConvertToDtos(queryId, searchResults);
		
		return searchResultDtos;
	}
	
	/**
	 * For more than query (this is actually will be used for approach 3)
	 * 
	 * @param queryIds
	 * @param code
	 * @param location
	 * @param language
	 * @param withInnerPage
	 * @return
	 * @throws Exception 
	 */
	public List<RawSearchResult> getSearchResults(List<Integer> queryIds,
			SearchEngineCode code, Location location,
			SearchEngineLanguage language, boolean withInnerPage, int size) throws Exception {
		ArrayList<RawSearchResult> searchResultDtos = new ArrayList<RawSearchResult>();
		
		for (Integer queryId : queryIds) {
			searchResultDtos.addAll(getSearchResults(queryId, code, location, language, withInnerPage, size));
		}
		
		return searchResultDtos;
	}
	
	/**
	 * Get meanings names for a list of clear queries
	 * 
	 * @param queryIds
	 * @return
	 */
	public List<String> getMeaningsForClearQueries(List<Integer> queryIds) {
		List<String> meanings = new ArrayList<String>();

		for (Integer queryId : queryIds) {
			meanings.add(getMeaningForClearQuery(queryId));
		}
		
		return meanings;
	}
	
	/**
	 * Get meaning name for a clear query
	 * 
	 * @param queryId
	 * @return
	 */
	public String getMeaningForClearQuery(Integer queryId) {
		return meaningRepository.findByClearQuery_Id(queryId).getName();
	}
	
	/**
	 * Get a list of meanings names for an ambiguous query
	 * 
	 * @param queryId
	 * @return
	 */
	public List<String> getMeaningsForAmbiguousQuery(Integer queryId) {
		List<String> meanings = new ArrayList<String>();
		
		List<Meaning> meaningObjects = meaningRepository.findByQuery_Id(queryId);
		
		meaningObjects.forEach(n -> meanings.add(n.getName()));
		
		return meanings;
	}
}
