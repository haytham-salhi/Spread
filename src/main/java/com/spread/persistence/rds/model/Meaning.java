package com.spread.persistence.rds.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "class")
	private String clazz;
	
	// Indicates if it is manually added, that is it is not from the Wikipedia disambiguation list
	@Column(name = "is_new")
	private Boolean isNew;
	
	// Ambiguous Query
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "query_id", nullable = false)
	private Query query;
	
	// Clear Query
	@OneToOne
	@JoinColumn(name = "clear_query_id")
	private Query clearQuery;

	
	public Meaning() {
	}

	public Meaning(String name, String description, String clazz,
			Boolean isNew, Query query) {
		super();
		this.name = name;
		this.description = description;
		this.clazz = clazz;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the clazz
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
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
	
	/**
	 * 
	 * @return clearQuery
	 */
	public Query getClearQuery() {
		return clearQuery;
	}
	
	/**
	 * 
	 * @param clearQuery
	 */
	public void setClearQuery(Query clearQuery) {
		this.clearQuery = clearQuery;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Meaning [id=" + id + ", name=" + name + ", description="
				+ description + ", clazz=" + clazz + ", isNew=" + isNew + "]";
	}
}
