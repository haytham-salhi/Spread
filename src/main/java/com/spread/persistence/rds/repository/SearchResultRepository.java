package com.spread.persistence.rds.repository;

import java.util.List;

import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface SearchResultRepository extends CrudRepository<SearchResult, Integer> {
	
	List<SearchResult> findByInnerPageIdIsNotNullAndInnerPageIsNull();
	
	@Query("SELECT searchResult "
			+ "FROM SearchResult searchResult " 
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE query.name = :name AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language")
	List<SearchResult> findByQueryAndSearchEngine(@Param("name") String name, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language);
	
	// The same as above :)
	List<SearchResult> findByQuerySearchEngine_Query_NameAndQuerySearchEngine_SearchEngine_Code(String name, SearchEngineCode code);
	
	@Modifying
	@Transactional
	@Query("update SearchResult s set s.innerPage = :innerPage where s.id = :id")
	int setInnerPageFor(@Param("innerPage") String innerPage, @Param("id") Integer id);
	
	@Query("SELECT new com.spread.persistence.rds.model.SearchResult(searchResult.id, searchResult.title, searchResult.url, searchResult.snippet) "
			+ "FROM SearchResult searchResult " 
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE query.name = :name AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language")
	List<SearchResult> findByQueryAndSearchEngineWithBasicInfo(@Param("name") String name, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language);
	
	// The same as above but by query id
	@Query("SELECT searchResult "
			+ "FROM SearchResult searchResult " 
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language")
	List<SearchResult> findByQueryAndSearchEngine(@Param("queryId") Integer queryId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language, Pageable pageable);
	
	// The same as above but by query id
	@Query("SELECT new com.spread.persistence.rds.model.SearchResult(searchResult.id, searchResult.title, searchResult.url, searchResult.snippet) "
			+ "FROM SearchResult searchResult " 
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language")
	List<SearchResult> findByQueryAndSearchEngineWithBasicInfo(@Param("queryId") Integer queryId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language, Pageable pageable);
	
	// this shoud not be here
	// To understand this query, see query # 4 and 5 in assessement.sql
	// For Yaser and Haytham
	@Query("SELECT userSearchResultAssessment.searchResult "
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
			+ "AND userSearchResultAssessment.user.id = 1 AND ua2.user.id = 2 "
			+ "AND userSearchResultAssessment.isRelevant = 'YES'")
	List<SearchResult> findAgreedRelevantByQueryAndSearchEngine(@Param("queryId") Integer queryId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language, Pageable pageable);
	
	@Query("SELECT userSearchResultAssessment.searchResult "
			+ "FROM UserSearchResultAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult "
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "WHERE query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language "
			+ "AND userSearchResultAssessment.user.id = 2 "
			+ "AND userSearchResultAssessment.isRelevant = 'YES' "
			+ "AND regexp(searchResult.title, '[؟-ي]+') = 1 " // Those in arabic
			+ "AND searchResult.innerPage IS NOT NULL") // Those having innerpage
	List<SearchResult> findRelevantArabicWithInnerPagesByQueryAndSearchEngine(@Param("queryId") Integer queryId, @Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language, Pageable pageable);
	
	@Query("SELECT new com.spread.persistence.rds.model.SearchResult(searchResult.id, searchResult.title, searchResult.url, searchResult.snippet) "
			+ "FROM UserSearchResultAssessment userSearchResultAssessment "
			+ "JOIN userSearchResultAssessment.searchResult searchResult "
			+ "JOIN searchResult.querySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "WHERE query.id = :queryId AND searchEngine.code = :code AND searchEngine.location = :location AND searchEngine.language= :language "
			+ "AND userSearchResultAssessment.user.id = :userId "
			+ "AND userSearchResultAssessment.isRelevant = 'YES' "
			+ "AND regexp(searchResult.title, '[؟-ي]+') = 1 " // Those in arabic
			+ "AND searchResult.innerPage IS NOT NULL") // Those having innerpage
	List<SearchResult> findRelevantArabicWithInnerPagesByQueryAndSearchEngine(@Param("queryId") Integer queryId, @Param("userId") Integer userId,@Param("code") SearchEngineCode code, @Param("location") Location location, @Param("language") SearchEngineLanguage language, Pageable pageable);
}
