package com.spread.persistence.nosql.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.spread.persistence.nosql.model.InnerPage;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface InnerPageRepository extends MongoRepository<InnerPage, String>{
	
}