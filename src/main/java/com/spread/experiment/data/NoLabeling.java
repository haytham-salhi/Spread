package com.spread.experiment.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.spread.experiment.RawSearchResult;
import com.spread.persistence.rds.model.SearchResult;
/**
 * The meaning and the clazz are set null (might be used for testing different ks for results of some ambiguous query)
 * 
 * @author Haytham Salhi
 *
 */
@Component
public class NoLabeling extends Data {
	
	@Override
	public List<RawSearchResult> labelTheResultsAndConvertToDtos(
			Integer queryId,
			List<SearchResult> searchResults) throws Exception {
		List<RawSearchResult> searchResultDtos = new ArrayList<RawSearchResult>();
		// Converts to the DTO
		for (SearchResult searchResult : searchResults) {
			searchResultDtos.add(new RawSearchResult(searchResult.getId(), searchResult.getTitle(), searchResult.getUrl(), searchResult.getSnippet(), searchResult.getInnerPage(), null, null));
		}
		
		return searchResultDtos;
	}
}
