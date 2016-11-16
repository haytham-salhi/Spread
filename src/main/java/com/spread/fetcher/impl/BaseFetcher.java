package com.spread.fetcher.impl;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 
 * @author Haytham Salhi
 *
 */
public abstract class BaseFetcher {
	
	private static final Logger LOGGER = LogManager.getLogger(BaseFetcher.class.getName());
	                                             //"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36						
	public static final String MY_PC_USERAGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";
	
	/**
	 * Fetches the the page of the url
	 * 
	 * @param url
	 * @return
	 */
	public String getInnerPage(String url) {
		LOGGER.info("Fetching " + url);

		if(url == null || url.isEmpty()) {
			LOGGER.info("The url comming to innerPage method is null");

			return null;
		}
		
		url = url.trim();
		
		// It is not correct to encode the whole url here!! You might need to query part of it(value of param of path param!)
		
		try {
			Document document = Jsoup.connect(url).timeout(30000).userAgent(MY_PC_USERAGENT).get();
			
			if(document != null) {
				return document.outerHtml();
			} else {
				LOGGER.info("The source page of the url: " + url + " is null!!!");
				LOGGER.error("The source page of the url: " + url + " is null!!!");
			}
		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
		
		return null;
	}
}
