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
	
}
