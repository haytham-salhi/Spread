package com.spread.persistence.rds.repository;

import com.spread.persistence.rds.model.Meaning;

import org.springframework.data.repository.CrudRepository;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface MeaningRepository extends CrudRepository<Meaning, Integer> {
	
}
