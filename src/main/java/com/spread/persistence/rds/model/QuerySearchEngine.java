package com.spread.persistence.rds.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Entity
@Table(name = "query_search_engine")
public class QuerySearchEngine {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "query_id", nullable = false)
	private Query query;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "search_engine_id", nullable = false)
	private SearchEngine searchEngine;
	
	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;
	
	public QuerySearchEngine() {
	}
	
	public QuerySearchEngine(Query query, SearchEngine searchEngine) {
		super();
		this.query = query;
		this.searchEngine = searchEngine;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	/**
	 * @return the searchEngine
	 */
	public SearchEngine getSearchEngine() {
		return searchEngine;
	}

	/**
	 * @param searchEngine the searchEngine to set
	 */
	public void setSearchEngine(SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QuerySearchEngine [id=" + id + ", createdDate=" + createdDate
				+ "]";
	}
}
