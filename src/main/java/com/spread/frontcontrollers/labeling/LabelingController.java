package com.spread.frontcontrollers.labeling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import com.spread.frontcontrollers.labeling.model.QueryView;
import com.spread.frontcontrollers.labeling.model.SurveyItem;
import com.spread.frontcontrollers.labeling.model.SurveyItemsWrapper;
import com.spread.frontcontrollers.labeling.model.YesNoAnswer;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.User;
import com.spread.persistence.rds.model.UserSearchResultAssessment;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.persistence.rds.repository.UserRepository;
import com.spread.persistence.rds.repository.UserSearchResultAssessmentRepository;

import de.svenjacobs.loremipsum.LoremIpsum;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Controller
@Scope("session")
@RequestMapping(value = "/assessment")
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
	
	private LoremIpsum loremIpsum = new LoremIpsum();
	
	private static final long serialVersionUID = -3990210536900633725L;
	private static final Logger logger = LogManager.getLogger(LabelingController.class);
	
	@Autowired
	private QueryRepository queryRepository;
	
	@Autowired
	private SearchResultRepository searchResultRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserSearchResultAssessmentRepository userSearchResultAssessmentRepository;
	
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
	
	
	// Flow step 1
	// Second way
	@RequestMapping(method = RequestMethod.GET)
	public String initialView(HttpServletRequest request,
			@SessionAttribute(required = false, name = "personName") String personName,
			@SessionAttribute(required = false, name = "searchEngine") String searchEngine) {
		
		if(personName != null && searchEngine != null) {
			return "redirect:/assessment/selectQuery";
		}
		
		logger.info(request.getRemoteAddr() + " accessed initialView!");
		
		return "assessment/initial";
	}
	
	// Intermediate Step
	@RequestMapping(value = "/selectQuery", method = RequestMethod.POST)
	public String selectQuery(HttpServletRequest request,
			Model model,
			@RequestParam("personName") String personName,
			@RequestParam("email") String email,
			@RequestParam("searchEngine")String searchEngineName) {
		logger.info(request.getRemoteAddr() + " accessed selectQuery!");
		
		// You should validate the input!!
		
		// Instead of the following you can actually pass them as attributes to the view! 
		// But I did this because I want to add them in the session
		
		// Register the session values
		if(request.getSession().getAttribute("personName") == null) {
			request.getSession().setAttribute("personName", personName);
			request.getSession().setAttribute("email", email);
			request.getSession().setAttribute("searchEngine", searchEngineName);
		}
		
		return "redirect:/assessment/selectQuery";
	}
	
	// Flow step 2
	@RequestMapping(value = "/selectQuery", method = RequestMethod.GET)
	public String selectQueryGet(HttpServletRequest request,
			Model model,
			@SessionAttribute(required = false, name = "personName") String personName, 
			@SessionAttribute(required = false, name = "searchEngine") String searchEngineName) {
		logger.info(request.getRemoteAddr() + " accessed selectQuery!");
		
		// You should validate the input!!
		
		// Instead of the following you can actually pass them as attributes to the view! 
		// But I did this because I want to add them in the session
		
		if(request.getSession().getAttribute("personName") == null) {
			return "redirect:/assessment"; 
		}
		
		String searchEngine = (String) request.getSession().getAttribute("searchEngine");
		
		SearchEngineCode code = null;
		// Validate
		if(searchEngine == null) {
			return "redirect:/assessment";  
		} else {
			try {
				code = SearchEngineCode.valueOf(searchEngine.toUpperCase());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				return "assessment/initial"; 
			}
		}
		
		List<Query> ambiguousQueries = queryRepository.findByIsAmbiguousAndIsOfficial(false, true);
		
		List<QueryView> queryViews = new ArrayList<QueryView>();
		for (Query query : ambiguousQueries) {
			
			queryViews.add(new QueryView(query, userSearchResultAssessmentRepository.countRespondentsByQueryId(query.getId(), code, Location.PALESTINE, SearchEngineLanguage.AR)));
		}
		
		model.addAttribute("queryViews", queryViews);
		
		return "assessment/select-query";
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
	
	// Flow step 3
	@RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
	public String searchResultsView(HttpServletRequest request,
			@PathVariable("id") int id, Model model) {
		logger.info(request.getRemoteAddr() + " accessed searchResultsView!");
		
		// You should validate the input!!
		
		// 1. Get the search results for the ambiguous query id
		String searchEngineString = (String) request.getSession().getAttribute("searchEngine");
		
		SearchEngineCode code = null;
		if(searchEngineString == null) {
			return "assessment/initial";  
		} else {
			try {
				code = SearchEngineCode.valueOf(searchEngineString.toUpperCase());
			} catch (IllegalArgumentException e) {
				return "assessment/initial"; 
			}
		}
		
		int size = 100;
		Pageable pageRequest = new PageRequest(0, size);
		
		List<SearchResult> searchItems = searchResultRepository.findByQueryAndSearchEngineWithBasicInfo(id, code, Location.PALESTINE, SearchEngineLanguage.AR, pageRequest);
		
		// The correct way is to bind searchitems, for the informative data
		
		// 2. Build the holder and the model
		// ctrl + 1
		SurveyItemsWrapper surveyItemsWrapper = new SurveyItemsWrapper();
		
		for (SearchResult searchResult : searchItems) {
			surveyItemsWrapper.getSurveyItems().add(new SurveyItem(searchResult.getId(), searchResult.getTitle(), searchResult.getUrl(), searchResult.getSnippet(), null));
		}
		
		// Get the query name
		Query query = queryRepository.findOne(id);
		surveyItemsWrapper.setQueryName(query.getName());

		// 3. Set the model
		model.addAttribute("surveyItemsWrapper", surveyItemsWrapper);
		model.addAttribute("choices", YesNoAnswer.values());
		
		return "assessment/fill-survey";
	}
	
	// Flow step 4
	@RequestMapping(value = "/query/submit", method = RequestMethod.POST)
	public String afterFilling(HttpServletRequest request,
			@Valid @ModelAttribute("surveyItemsWrapper") SurveyItemsWrapper surveyItemsWrapper,
			BindingResult result,
			Model model) {
		if(result.hasErrors()) {
			logger.info(request.getRemoteAddr() + " has errors in results");

			model.addAttribute("error", true);
			model.addAttribute("choices", YesNoAnswer.values());
			
			return "assessment/fill-survey";
		} else {
			logger.info(request.getRemoteAddr() + " has correct results");
			
			// 1. Fill the assessment objects
			// Get the session id
			String sessionId = request.getSession().getId();
			User user = userRepository.findBySessionId(sessionId);
			if(user == null) {
				// Save the user
				user = userRepository.save(new User(sessionId, (String) request.getSession().getAttribute("personName"), (String) request.getSession().getAttribute("email"), request.getRemoteAddr()));
			}
			List<UserSearchResultAssessment> userSearchResultAssessments = new ArrayList<UserSearchResultAssessment>();
			for (SurveyItem surveyItem : surveyItemsWrapper.getSurveyItems()) {
				// Get the search result
				SearchResult searchResult = new SearchResult(surveyItem.getId());
				userSearchResultAssessments.add(new UserSearchResultAssessment(user, searchResult, surveyItem.getAnswer()));
			}
			
			// 2. Save them
			userSearchResultAssessmentRepository.save(userSearchResultAssessments);
		}
		
		logger.info(request.getRemoteAddr() + " accessed afterFilling!");
		
		return "assessment/thanks";
	}
}
