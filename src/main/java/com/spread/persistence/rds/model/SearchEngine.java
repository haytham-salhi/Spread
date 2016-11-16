package com.spread.persistence.rds.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;

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
	
	@Enumerated(EnumType.STRING)
	@Column(name = "language", columnDefinition= "enum('DEFAULT','EN','AR')")
	private SearchEngineLanguage language;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "location", columnDefinition= "enum('PALESTINE','IRELAND')")
	private Location location;
	
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
	
	public SearchEngineLanguage getLanguage() {
		return language;
	}
	
	public void setLanguage(
			SearchEngineLanguage language) {
		this.language = language;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchEngine [id=" + id + ", name=" + name + ", code=" + code
				+ ", language=" + language + ", location=" + location + "]";
	}
}
