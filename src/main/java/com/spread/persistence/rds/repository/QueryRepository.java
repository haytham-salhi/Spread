package com.spread.persistence.rds.repository;

import java.util.List;

import com.spread.persistence.rds.model.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface QueryRepository extends CrudRepository<Query, Integer> {
	public Query findByName(String name);
	
	public Query findById(Integer id);
	
	public List<Query> findByIsAmbiguous(Boolean isAmbiguous);
	
	public List<Query> findByIsAmbiguousAndIsOfficial(Boolean isAmbiguous, Boolean isOfficial, Pageable pageable);
	
	public List<Query> findByIsAmbiguousAndIsOfficialAndAllowedUser_Id(Boolean isAmbiguous, Boolean isOfficial, Integer allowedUserId);
	
}
