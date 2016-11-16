package com.spread.persistence.rds.repository;

import com.spread.persistence.rds.model.QuerySearchEngine;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.QueryFormulationStartegy;
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
public interface QuerySearchEngineRepository extends CrudRepository<QuerySearchEngine, Integer>{
	
	// Used for non-ambiguous queries
	@Query("SELECT querySearchEngine "
			+ "FROM QuerySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE query.name = :query "
			+ "AND query.queryFormulationStartegy = :queryFormulationStartegy "
			+ "AND searchEngine.code = :searchEngineCode "
			+ "AND searchEngine.language = :searchEngineLanguage "
			+ "AND searchEngine.location = :locationOfFetching")
	public QuerySearchEngine find(@Param("query") String query,
			@Param("queryFormulationStartegy") QueryFormulationStartegy queryFormulationStartegy,
			@Param("searchEngineCode") SearchEngineCode searchEngineCode,
			@Param("searchEngineLanguage") SearchEngineLanguage searchEngineLanguage,
			@Param("locationOfFetching") Location locationOfFetching);
	
	// Used for ambiguous query
	@Query("SELECT querySearchEngine "
			+ "FROM QuerySearchEngine querySearchEngine "
			+ "JOIN querySearchEngine.query query "
			+ "JOIN querySearchEngine.searchEngine searchEngine "
			+ "WHERE query.name = :query "
			+ "AND query.queryFormulationStartegy IS NULL "
			+ "AND searchEngine.code = :searchEngineCode "
			+ "AND searchEngine.language = :searchEngineLanguage "
			+ "AND searchEngine.location = :locationOfFetching")
	public QuerySearchEngine find(@Param("query") String query,
			@Param("searchEngineCode") SearchEngineCode searchEngineCode,
			@Param("searchEngineLanguage") SearchEngineLanguage searchEngineLanguage,
			@Param("locationOfFetching") Location locationOfFetching);

}
