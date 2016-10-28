package com.spread.persistence.rds.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Entity
@Table(name = "meaning")
public class Meaning {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	// Indicates if it is manually added, that is it is not from the Wikipedia disambiguation list
	@Column(name = "is_new")
	private Boolean isNew;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "query_id", nullable = false)
	private Query query;
	
	public Meaning() {
	}

	public Meaning(String description, Boolean isNew, Query query) {
		super();
		this.description = description;
		this.isNew = isNew;
		this.query = query;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the isNew
	 */
	public Boolean getIsNew() {
		return isNew;
	}

	/**
	 * @param isNew the isNew to set
	 */
	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Meaning [id=" + id + ", description=" + description
				+ ", isNew=" + isNew + "]";
	}
}
