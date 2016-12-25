package com.spread.persistence.rds.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Entity
@Table(name = "user")
public class User {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name = "session_id")
	private String sessionId;
	
	@Column(name = "name")
	private String name;
	
	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;
	
	
	public User() {
	}

	public User(String sessionId, String name) {
		super();
		this.sessionId = sessionId;
		this.name = name;
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
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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
		return "User [id=" + id + ", sessionId=" + sessionId + ", name=" + name
				+ ", createdDate=" + createdDate + "]";
	}
}
