package com.spread.frontcontrollers.ping;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("session")
@RequestMapping("/ping")
public class PingController implements Serializable {
	
	private static final long serialVersionUID = 2163890734295095413L;

	@RequestMapping(value = {"", "/"})
	public String redirectToIndex() {
		
		return "ping_index";
	}
	
}
