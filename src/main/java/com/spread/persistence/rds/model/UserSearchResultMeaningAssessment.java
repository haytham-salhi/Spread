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
@Table(name = "user_search_result_meaning_assessment")
public class UserSearchResultMeaningAssessment {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "search_result_id", nullable = false)
	private SearchResult searchResult;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "meaning_id", nullable = true)
	private Meaning meaning;
	
	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;
	
	public UserSearchResultMeaningAssessment() {
	}

	public UserSearchResultMeaningAssessment(User user, SearchResult searchResult,
			Meaning meaning) {
		super();
		this.user = user;
		this.searchResult = searchResult;
		this.meaning = meaning;
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
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the searchResult
	 */
	public SearchResult getSearchResult() {
		return searchResult;
	}

	/**
	 * @param searchResult the searchResult to set
	 */
	public void setSearchResult(SearchResult searchResult) {
		this.searchResult = searchResult;
	}
	
	/**
	 * @return the meaning
	 */
	public Meaning getMeaning() {
		return meaning;
	}

	/**
	 * @param meaning the meaning to set
	 */
	public void setMeaning(Meaning meaning) {
		this.meaning = meaning;
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
	
}
