package com.spread.model;

/**
 * Represents the item to be fetched from a search engine for further processing
 * 
 * @author Haytham Salhi
 *
 */
public class SearchItem {
	
	private String title;
	private String url;
	private String shortSummary;
	private String innerPage;
	
	public SearchItem() {
	
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
	 * @return the shortSummary
	 */
	public String getShortSummary() {
		return shortSummary;
	}

	/**
	 * @param shortSummary the shortSummary to set
	 */
	public void setShortSummary(String shortSummary) {
		this.shortSummary = shortSummary;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchItem [title=" + title + ", url=" + url
				+ ", shortSummary=" + shortSummary + "]";
	}
}
