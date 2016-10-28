package com.spread.persistence.rds.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.spread.persistence.rds.model.enums.SearchEngineCode;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Entity
@Table(name = "search_engine")
public class SearchEngine {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "code", columnDefinition= "enum('GOOGLE','BING')", nullable = false)
	private SearchEngineCode code;
	
	public SearchEngine() {
	}

	public SearchEngine(String name, SearchEngineCode code) {
		super();
		this.name = name;
		this.code = code;
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
	 * @return the code
	 */
	public SearchEngineCode getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(SearchEngineCode code) {
		this.code = code;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchEngine [id=" + id + ", name=" + name + ", code=" + code
				+ "]";
	}
}
