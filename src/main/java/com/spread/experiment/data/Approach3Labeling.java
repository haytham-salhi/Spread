package com.spread.experiment.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.spread.experiment.RawSearchResult;
import com.spread.persistence.rds.model.Meaning;
import com.spread.persistence.rds.model.SearchResult;

/**
 * Labeling based on Approach 3 
 * 
 * @author Haytham Salhi
 *
 */
@Component
public class Approach3Labeling extends Data {
	
	@Override
	public List<RawSearchResult> labelTheResultsAndConvertToDtos(
			Integer queryId,
			List<SearchResult> searchResults) throws Exception {
		// Here, the query id should be for a clear query as per approach 3 policy
		Meaning meaning = meaningRepository.findByClearQuery_Id(queryId);
		if(meaning.getClearQuery().getIsAmbiguous() == true) {
			throw new Exception("The query is ambiguous and cannot be labled according to approach3!");
		}
		
		// These are for assigning the meaning (and the clazz) to each search result
		String meaningName = meaning.getName(); 
		String clazz = meaning.getClazz();
		
		List<RawSearchResult> searchResultDtos = new ArrayList<RawSearchResult>();
		// Converts to the DTO
		for (SearchResult searchResult : searchResults) {
			searchResultDtos.add(new RawSearchResult(searchResult.getTitle(), searchResult.getUrl(), searchResult.getSnippet(), searchResult.getInnerPage(), meaningName, clazz));
		}
		
		return searchResultDtos;
	}

}
