package com.spread.frontcontrollers.ping;

import java.io.Serializable;
import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.spread.fetcher.SearchEngineFetcher;
import com.spread.model.SearchResult;

@Controller
@Scope("session")
@RequestMapping("/bing")
public class PingController implements Serializable {
	
	private static final long serialVersionUID = 2163890734295095413L;
	
	@Autowired
	@Qualifier("bingFetcher")
	private SearchEngineFetcher bingSearchEngineFetcher;

	@RequestMapping(value = {"", "/"})
	public String redirectToIndex() {
		
		return "bing_index";
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search(@RequestParam("query")String query) throws Exception {
		long startTime = System.currentTimeMillis();
		
		String s = "هيثم";
		System.out.println(s); // The console will print it ????, you have to change the encoding for tomcat launch config
		System.out.println(query.equals(s));
		System.out.println("query: " + query);

		ModelAndView model = new ModelAndView("bing_index");
		
		// Check input
		if(query == null || query.isEmpty()) {
			return model;
		}
		
		// Process the query
		//query = StringEscapeUtils.unescapeHtml4(query);

		// Retrieve the documents
		SearchResult searchResult = bingSearchEngineFetcher.fetch(query);
		model.addObject("results", searchResult.getSearchItems());
		
		long endTime = System.currentTimeMillis();
		long executedTime = endTime - startTime;
		
		System.out.println("Time in Millis = " + executedTime );
		
//		String time = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(executedTime), TimeUnit.MILLISECONDS.toSeconds(executedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(executedTime)));
//		System.out.println("Time = " + time);
		
		double sec=executedTime/1000.0;
		DecimalFormat df = new DecimalFormat("#0.000"); 
		
		String timeSec= df.format(sec) + " sec";
		model.addObject("time", timeSec);
		
		
		return model;
	}
	
}
