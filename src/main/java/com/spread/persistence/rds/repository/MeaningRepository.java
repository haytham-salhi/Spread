package com.spread.persistence.rds.repository;

import java.util.List;

import com.spread.persistence.rds.model.Meaning;

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
public interface MeaningRepository extends CrudRepository<Meaning, Integer> {
	
	@Modifying
	@Transactional
	@Query("update Meaning m set m.clearQuery.id = :clearQueryId where m.name = :name and m.query.id= :ambiguousQueryId")
	int setClearQueryFor(@Param("clearQueryId") Integer clearQueryId, @Param("name") String name, @Param("ambiguousQueryId") Integer ambiguousQueryId);
	
	/**
	 * Gets the meaning alongs with clear queries for an ambiguous query
	 * 
	 * @param parentQueryId
	 * @return
	 */
	@Query("SELECT meaning "
			+ "FROM Meaning meaning " 
			+ "JOIN FETCH meaning.clearQuery clearQuery "
			+ "WHERE clearQuery.parent.id = :parentQueryId AND clearQuery.isAmbiguous = false")
	public List<Meaning> findMeaningsWithClearQueries(@Param("parentQueryId") Integer parentQueryId);
	
	/**
	 * Should return one meaning only
	 * @param clearQueryId
	 * @return
	 */
	public Meaning findByClearQuery_Id(Integer clearQueryId);
	
	/**
	 * Should return the meanings for an ambiguous query
	 * 
	 * @param queryId
	 * @return
	 */
	public List<Meaning> findByQuery_Id(Integer queryId);
}
