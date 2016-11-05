package com.spread.frontcontrollers.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.SearchEngineCode;

public class CrawlRequestPayload {
	
	@NotNull
	@JsonProperty(value = "se")
	private SearchEngineCode searchEngine;
	
	@NotNull
	@JsonProperty(value = "lang")
	private Language language;
	
	// Mode 0 --> Means Ambig queries
	// Mode 1 --> Means all queries
	@NotNull
	@JsonProperty(value = "mode")
	private Short mode;
	
	@NotNull
	@JsonProperty(value = "size")
	private Integer size;
	
	public CrawlRequestPayload() {
	}

	/**
	 * @return the searchEngine
	 */
	public SearchEngineCode getSearchEngine() {
		return searchEngine;
	}

	/**
	 * @param searchEngine the searchEngine to set
	 */
	public void setSearchEngine(SearchEngineCode searchEngine) {
		this.searchEngine = searchEngine;
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
	 * @return the mode
	 */
	public Short getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(Short mode) {
		this.mode = mode;
	}

	/**
	 * @return the size
	 */
	public Integer getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Integer size) {
		this.size = size;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CrawlRequestPayload [searchEngine=" + searchEngine
				+ ", language=" + language + ", mode=" + mode + ", size="
				+ size + "]";
	}
}
