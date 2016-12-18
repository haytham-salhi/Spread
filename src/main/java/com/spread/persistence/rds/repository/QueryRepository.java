package com.spread.persistence.rds.repository;

import java.util.List;

import com.spread.persistence.rds.model.Query;

import org.springframework.data.repository.CrudRepository;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface QueryRepository extends CrudRepository<Query, Integer> {
	public Query findByName(String name);
	
	public Query findById(Integer id);
	
	public List<Query> findByIsAmbiguous(boolean isAmbiguous);
}
