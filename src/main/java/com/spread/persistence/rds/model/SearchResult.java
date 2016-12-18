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
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Entity
@Table(name = "search_result")
public class SearchResult {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "url")
	private String url;
	
	@Column(name = "snippet")
	private String snippet;
	
	@Column(name = "inner_page", columnDefinition = "longtext")
	private String innerPage;
	
	@Column(name = "inner_page_id")
	private String innerPageId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "query_search_engine_id", nullable = false)
	private QuerySearchEngine querySearchEngine;
	
	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "updated_date")
	private Date updatedDate;
	
	public SearchResult() {
	}
	
	public SearchResult(String title, String url, String snippet) {
		super();
		this.title = title;
		this.url = url;
		this.snippet = snippet;
	}

	public SearchResult(String title, String url, String snippet,
			String innerPageId, QuerySearchEngine querySearchEngine) {
		super();
		this.title = title;
		this.url = url;
		this.snippet = snippet;
		this.innerPageId = innerPageId;
		this.querySearchEngine = querySearchEngine;
	}
	
	public SearchResult(QuerySearchEngine querySearchEngine, String title, String url, 
			String snippet, String innerPage) {
		super();
		this.title = title;
		this.url = url;
		this.snippet = snippet;
		this.innerPage = innerPage;
		this.querySearchEngine = querySearchEngine;
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the snippet
	 */
	public String getSnippet() {
		return snippet;
	}

	/**
	 * @param snippet the snippet to set
	 */
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	
	/**
	 * @return the innerPage
	 */
	public String getInnerPage() {
		return innerPage;
	}

	/**
	 * @param innerPage the innerPage to set
	 */
	public void setInnerPage(String innerPage) {
		this.innerPage = innerPage;
	}

	/**
	 * @return the innerPageId
	 */
	public String getInnerPageId() {
		return innerPageId;
	}

	/**
	 * @param innerPageId the innerPageId to set
	 */
	public void setInnerPageId(String innerPageId) {
		this.innerPageId = innerPageId;
	}

	/**
	 * @return the querySearchEngine
	 */
	public QuerySearchEngine getQuerySearchEngine() {
		return querySearchEngine;
	}

	/**
	 * @param querySearchEngine the querySearchEngine to set
	 */
	public void setQuerySearchEngine(QuerySearchEngine querySearchEngine) {
		this.querySearchEngine = querySearchEngine;
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

	/**
	 * @return the updatedDate
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * @param updatedDate the updatedDate to set
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchResult [id=" + id + ", title=" + title + ", url=" + url
				+ ", snippet=" + snippet + ", innerPageId=" + innerPageId
				+ ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + "]";
	}
}
