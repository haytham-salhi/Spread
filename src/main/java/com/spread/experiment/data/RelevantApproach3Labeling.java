package com.spread.experiment.data;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spread.experiment.RawSearchResult;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;

/**
 * Labeling based on Approach 3 but relevant 
 * 
 * @author Haytham Salhi
 *
 */
@Component
public class RelevantApproach3Labeling extends Approach3Labeling {
	
	/**
	 * Inner page has no effect here
	 */
	@Override
	public List<RawSearchResult> getSearchResults(Integer queryId,
			SearchEngineCode code, Location location,
			SearchEngineLanguage language, boolean withInnerPage, int size)
			throws Exception {
		List<SearchResult> searchResults = null;
		
		Pageable pageRequest = new PageRequest(0, size);
		
		searchResults = searchResultRepository.findRelevantArabicWithInnerPagesByQueryAndSearchEngine(queryId, code, location, language, pageRequest);
		
		List<RawSearchResult> searchResultDtos;
		searchResultDtos = labelTheResultsAndConvertToDtos(queryId, searchResults);
		
		return searchResultDtos;
	}

}
