package com.spread.persistence.rds.model;

import java.util.Date;

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

import org.hibernate.annotations.CreationTimestamp;

import com.spread.frontcontrollers.labeling.model.YesNoAnswer;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Entity
@Table(name = "user_search_result_assessment")
public class UserSearchResultAssessment {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "search_result_id", nullable = false)
	private SearchResult searchResult;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "is_relevant", columnDefinition = "enum('YES','NO')", nullable = false)
	private YesNoAnswer isRelevant;
	
	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;
	
	public UserSearchResultAssessment() {
	}

	public UserSearchResultAssessment(User user, SearchResult searchResult,
			YesNoAnswer isRelevant) {
		super();
		this.user = user;
		this.searchResult = searchResult;
		this.isRelevant = isRelevant;
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
	 * @return the isRelevant
	 */
	public YesNoAnswer getIsRelevant() {
		return isRelevant;
	}

	/**
	 * @param isRelevant the isRelevant to set
	 */
	public void setIsRelevant(YesNoAnswer isRelevant) {
		this.isRelevant = isRelevant;
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
