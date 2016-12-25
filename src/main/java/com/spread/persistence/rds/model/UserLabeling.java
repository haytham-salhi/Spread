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

import com.spread.persistence.rds.model.enums.LabelingType;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Entity
@Table(name = "user_labeling")
public class UserLabeling {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "search_result_id", nullable = false)
	private SearchResult searchResult;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meaning_id", nullable = false)
	private Meaning meaning;
	
	// Just to indicate that the user assign this result more than one meaning so you will have for example (user_id=1;result_id=1;meaning_id=2) and (user_id=1;result_id=1;meaning_id=3)
	@Enumerated(EnumType.STRING)
	@Column(name = "labeling_type", columnDefinition = "enum('SINGLE','MULTI')", nullable = false)
	private LabelingType labelingType;
	
	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;
	
	public UserLabeling() {
	}

	public UserLabeling(User user, SearchResult searchResult, Meaning meaning,
			LabelingType labelingType) {
		super();
		this.user = user;
		this.searchResult = searchResult;
		this.meaning = meaning;
		this.labelingType = labelingType;
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
	 * @return the labelingType
	 */
	public LabelingType getLabelingType() {
		return labelingType;
	}

	/**
	 * @param labelingType the labelingType to set
	 */
	public void setLabelingType(LabelingType labelingType) {
		this.labelingType = labelingType;
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
		return "UserLabeling [id=" + id + ", labelingType=" + labelingType
				+ ", createdDate=" + createdDate + "]";
	}
}
