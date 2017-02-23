package com.spread.frontcontrollers.labeling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.spread.experiment.data.Data;
import com.spread.fetcher.SearchEngineFetcher;
import com.spread.frontcontrollers.model.IntersectDemoData;
import com.spread.model.SearchItem;
import com.spread.model.SearchResult;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.enums.QueryFormulationStartegy;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.repository.QueryRepository;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Controller
@Scope("session")
@RequestMapping(value = "/labeling")
public class LabelingController implements Serializable {
	
	/***********************
	 * 
	 * Main Concepts:
	 * 
	 * @SesstionAttribute (to bind a session attribute to a metod param)
	 * @ModelAtrribute
	 * @RedirectAttribute
	 * @SessionAttributes (to specify which model attributes shoud be stored in the ssession)
	 * 
	 * 
	 */
	
	private static final long serialVersionUID = -3990210536900633725L;
	private static final Logger logger = LogManager.getLogger(LabelingController.class);
	
	@Autowired
	@Qualifier("approach3Labeling")
	private Data data;
	
	@Autowired
	private QueryRepository queryRepository;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	
	// One way
//	@RequestMapping(method = RequestMethod.GET)
//	public ModelAndView viewDemoForm(HttpServletRequest request) {
//		ModelAndView model = new ModelAndView();
//		
//		List<Query> ambiguousQueries = queryRepository.findByIsAmbiguous(false);
//		model.addObject("ambiguousQueries", ambiguousQueries);
//		
//		model.setViewName("labeling/main");
//		
//		logger.info(request.getRemoteAddr() + " accessed this!");
//		
//		return model;
//	}
	
	
	// Second way
	@RequestMapping(method = RequestMethod.GET)
	public String initialView(HttpServletRequest request) {
		
		logger.info(request.getRemoteAddr() + " accessed this!");
		
		
		return "labeling/initial";
	}
	
	@RequestMapping(value = "/selectQuery", method = RequestMethod.GET)
	public String selectQuery(HttpServletRequest request,
			Model model,
			@RequestParam("personName") String personName,
			@RequestParam("searchEngine") String searchEngine) {
		logger.info(request.getRemoteAddr() + " accessed this!");
		
		// Get the search results for the ambiguous query id
		
		System.out.println("Hello " + personName);
		System.out.println("Hello " + searchEngine);
		
		// Instead of the following you can actually pass them as attributes to the view! 
		// But I did this because I want to add them in the session
		request.getSession().setAttribute("personName", personName);
		request.getSession().setAttribute("searchEngine", searchEngine);
		
		List<Query> ambiguousQueries = queryRepository.findByIsAmbiguous(false);
		
		System.out.println("Getting the queiries!!");
		model.addAttribute("ambiguousQueries", ambiguousQueries);
		
		return "labeling/select-query";
	}
	
	
	
	// This can be used with both ways
	// This way each time will be invoked!
//	@ModelAttribute("ambiguousQueries")
//	public List<Query> initializeQueires() {
//		System.out.println("Getting the queiries!!");
//		List<Query> ambiguousQueries = queryRepository.findByIsAmbiguous(false);
//		
//		return ambiguousQueries;
//	}
	
	@RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
	public String searchResultsView(HttpServletRequest request,
			@PathVariable("id") int id, Model model) {
		logger.info(request.getRemoteAddr() + " accessed this!");
		
		System.out.println(id);
		
		// Get the search results for the ambiguous query id
		
		return "labeling/select-query";
	}
}
