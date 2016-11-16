package com.spread.persistence.rds.repository;

import com.spread.persistence.rds.model.SearchEngine;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;

import org.springframework.data.repository.CrudRepository;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface SearchEngineRepository extends CrudRepository<SearchEngine, Integer>{
	
	SearchEngine findByCodeAndLanguageAndLocation(SearchEngineCode code, SearchEngineLanguage language, Location location);
}
