package com.spread.experiment;

/**
 * Mainly it represents the document unit, containing some features as well as additional info like meaning (actual class), clazz
 * 
 * @author Haytham Salhi
 *
 */
public class RawSearchResult {
	
	// Useful for further investigation
	private Integer searchResultId;
	private String title;
	private String url;
	private String snippet;
	private String innerPage;
	// This will be the actual class for this object
	private String meaning; 
	// This is the class of the meaning of this object
	private String clazz;
	
//	public SearchResultDTO(String title, String url, String snippet) {
//		super();
//		this.title = title;
//		this.url = url;
//		this.snippet = snippet;
//	}
	
	public RawSearchResult(Integer searchResultId, String title, String url, String snippet,
			String innerPage, String meaning, String clazz) {
		super();
		this.searchResultId = searchResultId;
		this.title = title;
		this.url = url;
		this.snippet = snippet;
		this.innerPage = innerPage;
		this.meaning = meaning;
		this.clazz = clazz;
	}
	
	public Integer getSearchResultId() {
		return searchResultId;
	}
	
	public void setSearchResultId(Integer searchResultId) {
		this.searchResultId = searchResultId;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @return the snippet
	 */
	public String getSnippet() {
		return snippet;
	}
	/**
	 * @return the innerPage
	 */
	public String getInnerPage() {
		return innerPage;
	}
	
	public String getMeaning() {
		return meaning;
	}
	
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchResultDTO [searchResultId=" + searchResultId + ", title=" + title + ", url=" + url
				+ ", snippet=" + snippet + ", meaning=" + meaning + ", clazz="
				+ clazz + "]";
	}
	
	public String getFormedBriefString() {
		return "Title: " + title + "\n"
				+ "Url: " + url + "\n"
				+ "Snippet: " + snippet + "\n"
				+ "Meaning: " + meaning + "\n"
				+ "Class: " + clazz;
	}
}
