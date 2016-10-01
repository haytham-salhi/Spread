package com.spread.frontcontrollers.google;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("session")
@RequestMapping("/google")
public class GoogleController implements Serializable {
	
	private static final long serialVersionUID = -7119754277036775182L;

	@RequestMapping(value = {"", "/"})
	public String redirectToIndex() {
		
		return "google_index";
	}
	
}
