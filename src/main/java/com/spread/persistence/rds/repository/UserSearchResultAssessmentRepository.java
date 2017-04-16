package com.spread.persistence.rds.repository;

import java.util.List;

import com.spread.frontcontrollers.labeling.model.YesNoAnswer;
import com.spread.persistence.rds.model.UserSearchResultAssessment;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;

import org.springframework.data.domain.Pageable;
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
	
	@Query("SELECT distinct userSearchResultAssessment.user.name "
			+ "FROM UserSearchResultAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult "
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE querySearchEngine.query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language")
	public List<String> findRespondentNamesByQueryId(@Param("queryId") Integer queryId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language);
	
	@Query("SELECT distinct userSearchResultAssessment.user.id "
			+ "FROM UserSearchResultAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult "
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE querySearchEngine.query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language")
	public List<Integer> findRespondentIdsByQueryId(@Param("queryId") Integer queryId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language);
	
	// this shoud not be here
	// To understand this query, see query # 4 and 5 in assessement.sql
	@Query("SELECT userSearchResultAssessment "
			+ "FROM UserSearchResultAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult" 
			+ ", UserSearchResultAssessment ua2 " // cross join
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE ua2.searchResult.id = userSearchResultAssessment.searchResult.id " // cross join
			+ "AND query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language "
			+ "AND userSearchResultAssessment.user.id != ua2.user.id "
			+ "AND userSearchResultAssessment.isRelevant = ua2.isRelevant "
			+ "AND userSearchResultAssessment.user.id = :firstJudgeId AND ua2.user.id = :secondJudgeId ")
	public List<UserSearchResultAssessment> findAgreedAssessmentsByQueryAndSearchEngine(@Param("queryId") Integer queryId, @Param("firstJudgeId") Integer firstJudgeId, @Param("secondJudgeId") Integer secondJudgeId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language, Pageable pageable);
	
	// this shoud not be here
	// To understand this query, see query # 4 and 5 in assessement.sql
	@Query("SELECT userSearchResultAssessment "
			+ "FROM UserSearchResultAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult" 
			+ ", UserSearchResultAssessment ua2 " // cross join
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE ua2.searchResult.id = userSearchResultAssessment.searchResult.id " // cross join
			+ "AND query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language "
			+ "AND userSearchResultAssessment.user.id != ua2.user.id "
			+ "AND userSearchResultAssessment.isRelevant != ua2.isRelevant "
			+ "AND userSearchResultAssessment.user.id = :firstJudgeId AND ua2.user.id = :secondJudgeId "
			+ "AND userSearchResultAssessment.isRelevant = :firstJudgeIsRelevant")
	public List<UserSearchResultAssessment> findNotAgreedAssessmentsByQueryAndSearchEngine(@Param("queryId") Integer queryId, @Param("firstJudgeId") Integer firstJudgeId, @Param("secondJudgeId") Integer secondJudgeId, @Param("firstJudgeIsRelevant") YesNoAnswer firstJudgeIsRelevant, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language, Pageable pageable);
	
	
	// this shoud not be here
	// To understand this query, see query # 4 and 5 in assessement.sql
	@Query("SELECT userSearchResultAssessment "
			+ "FROM UserSearchResultAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult " 
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language "
			+ "AND userSearchResultAssessment.user.id = :judgeId")
	public List<UserSearchResultAssessment> findAssessmentByQueryAndJudge(@Param("queryId") Integer queryId, @Param("judgeId") Integer judgeId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language, Pageable pageable);
}
