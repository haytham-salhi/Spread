package com.spread.persistence.rds.repository;

import com.spread.persistence.rds.model.User;

import org.springframework.data.repository.CrudRepository;

/**
 * 
 * @author Haytham Salhi
 *
 */
public interface UserRepository extends CrudRepository<User, Integer> {
	public User findBySessionId(String sessionId);
}
