package com.spread.persistence.rds.repository;

import com.spread.persistence.rds.model.SearchResult;

import org.springframework.data.repository.CrudRepository;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface SearchResultRepository extends CrudRepository<SearchResult, Integer> {
	
}
