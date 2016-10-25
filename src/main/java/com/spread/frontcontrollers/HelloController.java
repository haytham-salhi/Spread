package com.spread.frontcontrollers;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Controller
@Scope("session")
public class HelloController implements Serializable {
	
	private static final Logger logger = LogManager.getLogger(HelloController.class);
	
	private static final long serialVersionUID = -4466170181566626934L;

	@RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
	public ModelAndView welcomePage(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		model.addObject("project_name", "Spread");
		model.addObject("description", "Search Result Disambiguation Engine");
		model.setViewName("hello");
		
		logger.info(request.getRemoteAddr() + " accessed this!");
		
		logger.debug("7ayyallah");
		
		
		return model;
	}
}
