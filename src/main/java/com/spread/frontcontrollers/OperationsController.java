package com.spread.frontcontrollers;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.core.Validate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spread.crawler.Crawler;
import com.spread.data.FileDataLoader;
import com.spread.frontcontrollers.model.CrawlRequestPayload;
import com.spread.persistence.rds.model.enums.Language;

@Controller
@Scope("session")
@RequestMapping("/operations")
public class OperationsController implements Serializable {
	
	private static final long serialVersionUID = 9221051043005653530L;
	
	private static final Logger LOGGER = LogManager.getLogger(OperationsController.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	
	@RequestMapping(value = "/crawl", method = RequestMethod.POST)
	public ResponseEntity<Void> crawl(@Valid @RequestBody CrawlRequestPayload payload) {
		Language lang = payload.getLanguage();
		short mode = payload.getMode();
		
		try {
			FileDataLoader loader = (FileDataLoader) applicationContext.getBean("FILE-" + lang);
			Map<String, List<String>> queries = loader.loadQueries();
			
			Crawler crawler = (Crawler) applicationContext.getBean("MODE-" + mode);
		} catch (BeansException e) {
			LOGGER.error(e);
			
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
