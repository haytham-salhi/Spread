package com.spread.persistence.rds.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.UserSearchResultMeaningAssessment;
import com.spread.persistence.rds.model.enums.SearchEngineCode;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface QueryRepository extends CrudRepository<Query, Integer> {
	public Query findByName(String name);
	
	public Query findById(Integer id);
	
	public List<Query> findByIsAmbiguous(Boolean isAmbiguous);
	
	public List<Query> findByIsAmbiguousAndIsOfficial(Boolean isAmbiguous, Boolean isOfficial, Pageable pageable);
	
	public List<Query> findByIsAmbiguousAndIsOfficialAndAllowedUser_Id(Boolean isAmbiguous, Boolean isOfficial, Integer allowedUserId);
	
	@org.springframework.data.jpa.repository.Query("SELECT distinct query "
			+ "FROM UserSearchResultMeaningAssessment userSearchResultMeaningAssessment " 
			+ "JOIN userSearchResultMeaningAssessment.searchResult searchResult "
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE searchEngine.code = :searchEngine AND searchEngine.location = 'PALESTINE' AND searchEngine.language= 'AR'")
	public List<Query> findAmbiguousQueriesWhoseResultsLabeled(@Param("searchEngine") SearchEngineCode searchEngine, Pageable pageable);
	
}
