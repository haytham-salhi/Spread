package com.spread.persistence.rds.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.QueryFormulationStartegy;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Entity
@Table(name = "query")
public class Query {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "language", columnDefinition = "enum('AR','EN')", nullable = false)
	private Language language;
	
	@Column(name = "is_ambiguous")
	private Boolean isAmbiguous;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "query_formulation_strategy", columnDefinition = "enum('APPEND','NO_APPEND')", nullable = false)
	private QueryFormulationStartegy queryFormulationStartegy;
	
	// is_ambiguous = true, parent = null --> the ambiguous query
	// is_ambiguous = false, parent = someId --> the clear query and someId is the ambiguous one
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Query parent;
	
	public Query() {
	}

	public Query(String name, Language language, Boolean isAmbiguous,
			QueryFormulationStartegy queryFormulationStartegy, Query parent) {
		super();
		this.name = name;
		this.language = language;
		this.isAmbiguous = isAmbiguous;
		this.queryFormulationStartegy = queryFormulationStartegy;
		this.parent = parent;
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
	 * @return the language
	 */
	public Language getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(Language language) {
		this.language = language;
	}

	/**
	 * @return the isAmbiguous
	 */
	public Boolean getIsAmbiguous() {
		return isAmbiguous;
	}

	/**
	 * @param isAmbiguous the isAmbiguous to set
	 */
	public void setIsAmbiguous(Boolean isAmbiguous) {
		this.isAmbiguous = isAmbiguous;
	}

	/**
	 * @return the queryFormulationStartegy
	 */
	public QueryFormulationStartegy getQueryFormulationStartegy() {
		return queryFormulationStartegy;
	}

	/**
	 * @param queryFormulationStartegy the queryFormulationStartegy to set
	 */
	public void setQueryFormulationStartegy(
			QueryFormulationStartegy queryFormulationStartegy) {
		this.queryFormulationStartegy = queryFormulationStartegy;
	}

	/**
	 * @return the parent
	 */
	public Query getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Query parent) {
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Query [id=" + id + ", name=" + name + ", language=" + language
				+ ", isAmbiguous=" + isAmbiguous
				+ ", queryFormulationStartegy=" + queryFormulationStartegy
				+ "]";
	}
}
