package com.spread.persistence.rds.repository;

import java.util.List;

import com.spread.persistence.rds.model.SearchResult;

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
	
	@Modifying
	@Transactional
	@Query("update SearchResult s set s.innerPage = :innerPage where s.id = :id")
	int setInnerPageFor(@Param("innerPage") String innerPage, @Param("id") Integer id);
	
}
