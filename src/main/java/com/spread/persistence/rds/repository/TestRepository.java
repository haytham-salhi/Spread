package com.spread.persistence.rds.repository;

import org.springframework.data.repository.CrudRepository;

import com.spread.persistence.rds.model.Test;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface TestRepository extends CrudRepository<Test, Integer> {
	
}
