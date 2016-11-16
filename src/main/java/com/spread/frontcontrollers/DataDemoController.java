package com.spread.frontcontrollers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.spread.fetcher.SearchEngineFetcher;
import com.spread.frontcontrollers.model.IntersectDemoData;
import com.spread.model.SearchItem;
import com.spread.model.SearchResult;
import com.spread.persistence.rds.model.enums.QueryFormulationStartegy;
import com.spread.persistence.rds.model.enums.SearchEngineCode;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Controller
@Scope("session")
@RequestMapping(value = "/intersect")
public class DataDemoController implements Serializable {
	
	private static final long serialVersionUID = -3990210536900633725L;
	private static final Logger logger = LogManager.getLogger(DataDemoController.class);
	
	@Autowired
	private ApplicationContext applicationContext;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView viewDemoForm(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		
		// Model
		IntersectDemoData intersectDemoData = new IntersectDemoData();
		model.addObject("demoDataForm", intersectDemoData);
		
		// One way
		List<SearchEngineCode> searchEngines = new ArrayList<SearchEngineCode>();
		searchEngines.add(SearchEngineCode.GOOGLE);
		searchEngines.add(SearchEngineCode.BING);
		model.addObject("searchEngineList", searchEngines);
		
		// Another way
		model.addObject("queryFormulationStartegyList", QueryFormulationStartegy.values());

		model.setViewName("intersection_demo_form");
		
		logger.info(request.getRemoteAddr() + " accessed this!");
		
		return model;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView proccessSearchAndIntersection(HttpServletRequest request,
			// This variable seems to be two-ways binding, that is it bound from intersection_demo_form, and can be used then from intersection_demo_results
			// without explicitly add it to the model
			@Valid @ModelAttribute("demoDataForm") IntersectDemoData intersectDemoData,
			BindingResult result) {
		ModelAndView model = new ModelAndView();
		
		if(result.hasErrors()) {
			model.setViewName("intersection_demo_form");
		} else {
			//model.addObject("demoDataForm", intersectDemoData);
			String ambigousQuery = intersectDemoData.getQuery();
			
			String meanings = intersectDemoData.getMeanings();
			String[] splitedMeanings = meanings.split("\r\n|\n\r");
			
			QueryFormulationStartegy queryFormulatioNStrategy = intersectDemoData.getQueryFormulationStartegy();
			
			SearchEngineFetcher fetcher = null;
			
			switch (intersectDemoData.getSearchEngine()) {
			case GOOGLE:
				fetcher = (SearchEngineFetcher) applicationContext.getBean("googleCustomFetcher");
				break;
			case BING:
				fetcher = (SearchEngineFetcher) applicationContext.getBean("bingFetcher");
				break;
			default:
				break;
			}
			
			// #########################
			
			
			Map<String, SearchResult> meaningSearchResults = new HashMap<String, SearchResult>();
			
			// Fetch the main query
			SearchResult ambQueryResult = fetcher.fetch(ambigousQuery);
			logger.info("Ambiguous Query: " + ambQueryResult.getSearchItems().size());
			model.addObject("size", ambQueryResult.getSearchItems().size());
			
			// Fetch the meanings
			for (String meaning : splitedMeanings) {
				String formulatedQuery = meaning;
				
				if(queryFormulatioNStrategy == QueryFormulationStartegy.APPEND)
					formulatedQuery = ambigousQuery + " " + meaning;
				
				SearchResult clearQueryResult = fetcher.fetch(formulatedQuery);
				logger.info("Result size of " + formulatedQuery + " query: " + clearQueryResult.getSearchItems().size());
				
				meaningSearchResults.put(meaning, clearQueryResult);
			}
			model.addObject("meaningSearchResults", meaningSearchResults);
			
			// Find intersections
			Map<String, Integer> meaningIntersections = new HashMap<String, Integer>();
			Map<String, List<SearchItem>> meaningIntersectionsListOfSearchItems = new HashMap<String, List<SearchItem>>();
			for (String meaning : meaningSearchResults.keySet()) {
				List<SearchItem> commonsBetweenAmbAndClear = intersect2(ambQueryResult.getSearchItems(), meaningSearchResults.get(meaning).getSearchItems());
				
				logger.info("Number of common items between " + ambigousQuery + " and " + meaning + " is " + commonsBetweenAmbAndClear.size());
				
				meaningIntersections.put(meaning, commonsBetweenAmbAndClear.size());
				// Add the item lists as well
				meaningIntersectionsListOfSearchItems.put(meaning, commonsBetweenAmbAndClear);
			}
			model.addObject("meaningIntersections", meaningIntersections);
			model.addObject("meaningIntersectionsListOfSearchItems", meaningIntersectionsListOfSearchItems);
			
			
			// ###########################
			
			for (String string : splitedMeanings) {
				logger.info(string + " " + string.length());
			}
			
			
			model.setViewName("intersection_demo_results");
		}
		
		
		//logger.info(request.getRemoteAddr() + " accessed this!");
		return model;
	}
	
	private List<String> intersect(List<SearchItem> searchItems1, List<SearchItem> searchItems2) {
		List<String> common = new ArrayList<String>();
		
		if(searchItems1 == null || searchItems1.isEmpty()
				|| searchItems2 == null || searchItems2.isEmpty()) {
			return common;
		}
		
		// List of urls
		List<String> startList;
		List<String> otherList;
		
		// Start with the shorter one
		if(searchItems2.size() < searchItems1.size()) {
			startList = searchItems2.stream().map(x -> x.getUrl()).collect(Collectors.toList());
			otherList = searchItems1.stream().map(x -> x.getUrl()).collect(Collectors.toList());
		} else {
			startList = searchItems1.stream().map(x -> x.getUrl()).collect(Collectors.toList());
			otherList = searchItems2.stream().map(x -> x.getUrl()).collect(Collectors.toList());
		}
		
		for (String url : startList) {
			if(otherList.contains(url)) {
				common.add(url);
			}
		}
		
		return common;
	}
	
	private List<SearchItem> intersect2(List<SearchItem> searchItems1, List<SearchItem> searchItems2) {
		List<SearchItem> common = new ArrayList<SearchItem>();
		
		if(searchItems1 == null || searchItems1.isEmpty()
				|| searchItems2 == null || searchItems2.isEmpty()) {
			return common;
		}
		
		// List of urls
		List<SearchItem> startList;
		List<SearchItem> otherList;
		
		// Start with the shorter one
		if(searchItems2.size() < searchItems1.size()) {
			startList = searchItems2;
			otherList = searchItems1;
		} else {
			startList = searchItems1;
			otherList = searchItems2;
		}
		
		for (SearchItem searchItem : startList) {
			// contains will use the equals overrided in SearchItem class
			if(otherList.contains(searchItem)) {
				common.add(searchItem);
			}
		}
		
		return common;
	}
}
