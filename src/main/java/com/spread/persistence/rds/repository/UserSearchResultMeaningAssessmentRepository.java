package com.spread.persistence.rds.repository;

import java.util.List;

import com.spread.persistence.rds.model.UserSearchResultMeaningAssessment;
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
public interface UserSearchResultMeaningAssessmentRepository extends CrudRepository<UserSearchResultMeaningAssessment, Integer> {
	
	@Query("SELECT count(distinct userSearchResultAssessment.user.id) "
			+ "FROM UserSearchResultMeaningAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult "
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE querySearchEngine.query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language")
	public Integer countRespondentsByQueryId(@Param("queryId") Integer queryId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language);
	
	@Query("SELECT distinct userSearchResultAssessment.user.name "
			+ "FROM UserSearchResultMeaningAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult "
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE querySearchEngine.query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language")
	public List<String> findRespondentNamesByQueryId(@Param("queryId") Integer queryId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language);
	
	@Query("SELECT distinct userSearchResultAssessment.user.id "
			+ "FROM UserSearchResultMeaningAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult "
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE querySearchEngine.query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language")
	public List<Integer> findRespondentIdsByQueryId(@Param("queryId") Integer queryId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language);
}
