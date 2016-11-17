package com.spread.model;

/**
 * 
 * @author Haytham Salhi
 *
 */
public class Meaning {
	
	private String name;
	private String decription;
	private String clazz;
	
	public Meaning() {
	}
	
	public Meaning(String name, String decription, String clazz) {
		super();
		this.name = name;
		this.decription = decription;
		this.clazz = clazz;
	}

	/**
	 * @return the query
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param query the query to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the decription
	 */
	public String getDecription() {
		return decription;
	}

	/**
	 * @param decription the decription to set
	 */
	public void setDecription(String decription) {
		this.decription = decription;
	}

	/**
	 * @return the clazz
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Meaning [name=" + name + ", decription=" + decription
				+ ", clazz=" + clazz + "]";
	}
}
