package com.spread.frontcontrollers;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.spread.views.ResponseStatusHolder;


/**
 * 
 * @author Haytham Salhi
 *
 */
@RestController
@Scope("session")
public class HelloRestController implements Serializable {
	
	private static final long serialVersionUID = 6179923380121110725L;

	@RequestMapping(value = "/foo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseStatusHolder fixUserLocationTable() throws Exception {
		ResponseStatusHolder response = new ResponseStatusHolder();
		
		response.setData("Hello");
		response.setStatus(HttpStatus.ACCEPTED.name());
		response.setStatusCode(HttpStatus.ACCEPTED.value());
		
		return response;
	}
}
