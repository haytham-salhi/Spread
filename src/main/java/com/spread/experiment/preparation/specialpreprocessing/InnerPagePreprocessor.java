package com.spread.experiment.preparation.specialpreprocessing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class InnerPagePreprocessor implements TextPreprocesser {
	
	@Override
	public String process(String text) {
		if(text == null || text.isEmpty()) {
			return "";
		}
		
		// I am recieving HTML text
		Document html = Jsoup.parse(text);
		
		String title = html.title();
		String body = html.body().text();
		
		return title + " " + body;
	}

}
