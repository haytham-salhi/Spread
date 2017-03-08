package com.spread.persistence.rds.repository;

import com.spread.persistence.rds.model.UserSearchResultAssessment;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface UserSearchResultAssessmentRepository extends CrudRepository<UserSearchResultAssessment, Integer> {
	
	@Query("SELECT count(distinct userSearchResultAssessment.user.id) "
			+ "FROM UserSearchResultAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult "
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE querySearchEngine.query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language")
	public Integer countRespondentsByQueryId(@Param("queryId") Integer queryId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language);
	
}
