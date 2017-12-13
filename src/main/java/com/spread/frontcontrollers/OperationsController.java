package com.spread.frontcontrollers;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.core.Validate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spread.crawler.Crawler;
import com.spread.data.FileDataLoader;
import com.spread.experiment.data.stemmers.LightStemmer;
import com.spread.experiment.preparation.specialpreprocessing.InnerPagePreprocessor;
import com.spread.frontcontrollers.model.CrawlRequestPayload;
import com.spread.persistence.rds.model.Meaning;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.persistence.rds.repository.TestRepository;
import com.spread.util.nlp.arabic.SpreadArabicPreprocessor;

import weka.core.stemmers.Stemmer;

@Controller
@Scope("session")
@RequestMapping("/operations")
public class OperationsController implements Serializable {
	
	private static final long serialVersionUID = 9221051043005653530L;
	
	private static final Logger LOGGER = LogManager.getLogger(OperationsController.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private SearchResultRepository searchResultRepository;
	
	@Autowired
	private QueryRepository queryRepository;
	
	@Autowired
	private MeaningRepository meaningRepository;
	
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
	
	@RequestMapping(value = "/estimate", method = RequestMethod.GET)
	public ResponseEntity<String> estimateTerms(
			@RequestParam(required = false, defaultValue = "false") boolean puncutationRemovalForWords,
			@RequestParam(required = false, defaultValue = "false") boolean nonArabicWordsRemovalForWords,
			@RequestParam(required = false, defaultValue = "false") boolean arabicNumbersRemovalForWords,
			@RequestParam(required = false, defaultValue = "false") boolean stopWordsRemovalForWords,
			@RequestParam(required = false, defaultValue = "100") int sizePerQuery
			) {
		// Text preprocessing related
		Stemmer stemmer = new LightStemmer(); 
		boolean letterNormalization = true;
		boolean diacriticsRemoval = true;
		boolean puncutationRemoval = true;
		boolean nonArabicWordsRemoval = true;
		boolean arabicNumbersRemoval = true;
		boolean nonAlphabeticWordsRemoval = true;
		boolean stopWordsRemoval = true;
		
		StringBuffer sb = new StringBuffer();
		
		SearchEngineCode[] engines = {SearchEngineCode.GOOGLE, SearchEngineCode.BING};
		
		for (SearchEngineCode searchEngineCode : engines) {
			
			//PageRequest pr = new PageRequest(0, 10300);
			//List<SearchResult> results = searchResultRepository.findOfficialBySearchEngineWithBasicInfo(searchEngineCode, Location.PALESTINE, SearchEngineLanguage.AR, pr);
			
			// prepare the data
			PageRequest pr = new PageRequest(0, sizePerQuery);
			List<Query> ambiguousQueries = queryRepository.findByIsAmbiguousAndIsOfficial(true, true, null);
			List<SearchResult> results = new ArrayList<>();
			for (Query query : ambiguousQueries) {
				List<Meaning> clearMeaningsWithClearQueriesForAq = meaningRepository.findOfficialMeaningsWithClearQueries(query.getId());
				List<Integer> clearQueryIds = clearMeaningsWithClearQueriesForAq.stream().map(n -> n.getClearQuery().getId()).collect(Collectors.toList());
				
				results.addAll(searchResultRepository.findByQueryAndSearchEngine(query.getId(), searchEngineCode, Location.PALESTINE, SearchEngineLanguage.AR, pr));
				
				for (Integer integer : clearQueryIds) {
					results.addAll(searchResultRepository.findByQueryAndSearchEngine(integer, searchEngineCode, Location.PALESTINE, SearchEngineLanguage.AR, pr));
				}
			}
			
			//System.out.println("For " + searchEngineCode + ":");
			sb.append("<br /><br />For " + searchEngineCode + ":" + "<br /><br />");

			SpreadArabicPreprocessor spreadArabicPreprocessor = new SpreadArabicPreprocessor();
			
			// title
			int countOfWords = 0;
			int countOfResultsThatHasWordsAfterPreprocesssing = 0;
			int countOfTerms = 0;
			int countOfResultsThatHasTermsAfterPreprocesssing = 0;
			for (SearchResult seacrhResult : results) {
				String text0 = spreadArabicPreprocessor.process(seacrhResult.getTitle(), null, false, false, puncutationRemovalForWords, nonArabicWordsRemovalForWords, arabicNumbersRemovalForWords, false, stopWordsRemovalForWords, null);
				ArrayList<String> words = spreadArabicPreprocessor.tokenizeBySpaceAndRemoveExcessiveSpaces(text0);
				if(words != null && !words.isEmpty()) {
					countOfWords += words.size();
					countOfResultsThatHasWordsAfterPreprocesssing++;
				}
				
				String text = spreadArabicPreprocessor.process(seacrhResult.getTitle(), stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval, nonAlphabeticWordsRemoval, stopWordsRemoval, null);
				ArrayList<String> terms = spreadArabicPreprocessor.tokenizeBySpaceAndRemoveExcessiveSpaces(text);
				if(terms != null && !terms.isEmpty()) {
					countOfTerms += terms.size();
					countOfResultsThatHasTermsAfterPreprocesssing++;
				}
			}
			//System.out.println("Number of words detected (title) = " + countOfWords);
			sb.append("Number of words detected (title) = " + countOfWords + "<br />");
			//System.out.println("Number of resutls that has words after preprocessing (hint: just a very few preprocessing steps): " + countOfResultsThatHasWordsAfterPreprocesssing + " out of " + results.size());
			sb.append("Number of resutls that has words after preprocessing: " + countOfResultsThatHasWordsAfterPreprocesssing + " out of " + results.size() + "<br />");
			//System.out.println("Average: " + (double)countOfWords / countOfResultsThatHasWordsAfterPreprocesssing);
			sb.append("Average: " + (double)countOfWords / countOfResultsThatHasWordsAfterPreprocesssing + "<br />");
			
			//System.out.println("Number of terms detected (title) = " + countOfTerms);
			sb.append("Number of terms detected (title) = " + countOfTerms + "<br />");
			//System.out.println("Number of resutls that has terms after preprocessing: " + countOfResultsThatHasTermsAfterPreprocesssing + " out of " + results.size());
			sb.append("Number of resutls that has terms after preprocessing: " + countOfResultsThatHasTermsAfterPreprocesssing + " out of " + results.size() + "<br />");
			//System.out.println("Average: " + (double)countOfTerms / countOfResultsThatHasTermsAfterPreprocesssing);
			sb.append("Average: " + (double)countOfTerms / countOfResultsThatHasTermsAfterPreprocesssing + "<br />");
			
			// snippet 
			countOfWords = 0;
			countOfResultsThatHasWordsAfterPreprocesssing = 0;
			countOfTerms = 0;
			countOfResultsThatHasTermsAfterPreprocesssing = 0;
			for (SearchResult seacrhResult : results) {
				String text0 = spreadArabicPreprocessor.process(seacrhResult.getSnippet(), null, false, false, puncutationRemovalForWords, nonArabicWordsRemovalForWords, arabicNumbersRemovalForWords, false, stopWordsRemovalForWords, null);
				ArrayList<String> words = spreadArabicPreprocessor.tokenizeBySpaceAndRemoveExcessiveSpaces(text0);
				if(words != null && !words.isEmpty()) {
					countOfWords += words.size();
					countOfResultsThatHasWordsAfterPreprocesssing++;
				}
				
				String text = spreadArabicPreprocessor.process(seacrhResult.getSnippet(), stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval, nonAlphabeticWordsRemoval, stopWordsRemoval, null);
				ArrayList<String> terms = spreadArabicPreprocessor.tokenizeBySpaceAndRemoveExcessiveSpaces(text);
				if(terms != null && !terms.isEmpty()) {
					countOfTerms += terms.size();
					countOfResultsThatHasTermsAfterPreprocesssing++;
				}
			}
			//System.out.println("Number of words detected (snippet) = " + countOfWords);
			sb.append("Number of words detected (snippet) = " + countOfWords + "<br />");
			//System.out.println("Number of resutls that has words after preprocessing: " + countOfResultsThatHasWordsAfterPreprocesssing + " out of " + results.size());
			sb.append("Number of resutls that has words after preprocessing: " + countOfResultsThatHasWordsAfterPreprocesssing + " out of " + results.size() + "<br />");
			//System.out.println("Average: " + (double)countOfWords / countOfResultsThatHasWordsAfterPreprocesssing);
			sb.append("Average: " + (double)countOfWords / countOfResultsThatHasWordsAfterPreprocesssing + "<br />");
			
			//System.out.println("Number of terms detected (snippet) = " + countOfTerms);
			sb.append("Number of terms detected (snippet) = " + countOfTerms + "<br />");
			//System.out.println("Number of resutls that has terms after preprocessing: " + countOfResultsThatHasTermsAfterPreprocesssing + " out of " + results.size());
			sb.append("Number of resutls that has terms after preprocessing: " + countOfResultsThatHasTermsAfterPreprocesssing + " out of " + results.size() + "<br />");
			//System.out.println("Average: " + (double)countOfTerms / countOfResultsThatHasTermsAfterPreprocesssing);
			sb.append("Average: " + (double)countOfTerms / countOfResultsThatHasTermsAfterPreprocesssing + "<br />");

			// title + snippet
			countOfWords = 0;
			countOfResultsThatHasWordsAfterPreprocesssing = 0;
			countOfTerms = 0;
			countOfResultsThatHasTermsAfterPreprocesssing = 0;
			for (SearchResult seacrhResult : results) {
				String text0 = spreadArabicPreprocessor.process(seacrhResult.getTitle() + " " + seacrhResult.getSnippet(), null, false, false, puncutationRemovalForWords, nonArabicWordsRemovalForWords, arabicNumbersRemovalForWords, false, stopWordsRemovalForWords, null);
				ArrayList<String> words = spreadArabicPreprocessor.tokenizeBySpaceAndRemoveExcessiveSpaces(text0);
				if(words != null && !words.isEmpty()) {
					countOfWords += words.size();
					countOfResultsThatHasWordsAfterPreprocesssing++;
				}
				
				String text = spreadArabicPreprocessor.process(seacrhResult.getTitle() + " " + seacrhResult.getSnippet(), stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval, nonAlphabeticWordsRemoval, stopWordsRemoval, null);
				ArrayList<String> terms = spreadArabicPreprocessor.tokenizeBySpaceAndRemoveExcessiveSpaces(text);
				if(terms != null && !terms.isEmpty()) {
					countOfTerms += terms.size();
					countOfResultsThatHasTermsAfterPreprocesssing++;
				}
			}
			//System.out.println("Number of words detected (title + snippet) = " + countOfWords);
			sb.append("Number of words detected (title + snippet) = " + countOfWords + "<br />");
			//System.out.println("Number of resutls that has words after preprocessing: " + countOfResultsThatHasWordsAfterPreprocesssing + " out of " + results.size());
			sb.append("Number of resutls that has words after preprocessing: " + countOfResultsThatHasWordsAfterPreprocesssing + " out of " + results.size() + "<br />");
			//System.out.println("Average: " + (double)countOfWords / countOfResultsThatHasWordsAfterPreprocesssing);
			sb.append("Average: " + (double)countOfWords / countOfResultsThatHasWordsAfterPreprocesssing + "<br />");
			
			//System.out.println("Number of terms detected (title + snippet) = " + countOfTerms);
			sb.append("Number of terms detected (title + snippet) = " + countOfTerms + "<br />");
			//System.out.println("Number of resutls that has terms after preprocessing: " + countOfResultsThatHasTermsAfterPreprocesssing + " out of " + results.size());
			sb.append("Number of resutls that has terms after preprocessing: " + countOfResultsThatHasTermsAfterPreprocesssing + " out of " + results.size() + "<br />");
			//System.out.println("Average: " + (double)countOfTerms / countOfResultsThatHasTermsAfterPreprocesssing);
			sb.append("Average: " + (double)countOfTerms / countOfResultsThatHasTermsAfterPreprocesssing + "<br />");
			
			// innerpage
			countOfWords = 0;
			countOfResultsThatHasWordsAfterPreprocesssing = 0;
			countOfTerms = 0;
			countOfResultsThatHasTermsAfterPreprocesssing = 0;
			for (SearchResult seacrhResult : results) {
				// convert to plain text
				String innerPageAsPlainText = new InnerPagePreprocessor().process(seacrhResult.getInnerPage()); 
				
				String text0 = spreadArabicPreprocessor.process(innerPageAsPlainText, null, false, false, puncutationRemovalForWords, nonArabicWordsRemovalForWords, arabicNumbersRemovalForWords, false, stopWordsRemovalForWords, null);
				ArrayList<String> words = spreadArabicPreprocessor.tokenizeBySpaceAndRemoveExcessiveSpaces(text0);
				if(words != null && !words.isEmpty()) {
					countOfWords += words.size();
					countOfResultsThatHasWordsAfterPreprocesssing++;
				}
				
				String text = spreadArabicPreprocessor.process(innerPageAsPlainText, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval, nonAlphabeticWordsRemoval, stopWordsRemoval, null);
				ArrayList<String> terms = spreadArabicPreprocessor.tokenizeBySpaceAndRemoveExcessiveSpaces(text);
				if(terms != null && !terms.isEmpty()) {
					countOfTerms += terms.size();
					countOfResultsThatHasTermsAfterPreprocesssing++;
				}
			}
			//System.out.println("Number of words detected (innerpage) = " + countOfWords);
			sb.append("Number of words detected (innerpage) = " + countOfWords + "<br />");
			//System.out.println("Number of resutls that has words after preprocessing: " + countOfResultsThatHasWordsAfterPreprocesssing + " out of " + results.size());
			sb.append("Number of resutls that has words after preprocessing: " + countOfResultsThatHasWordsAfterPreprocesssing + " out of " + results.size() + "<br />");
			//System.out.println("Average: " + (double)countOfWords / countOfResultsThatHasWordsAfterPreprocesssing);
			sb.append("Average: " + (double)countOfWords / countOfResultsThatHasWordsAfterPreprocesssing + "<br />");
			
			//System.out.println("Number of terms detected (innerpage) = " + countOfTerms);
			sb.append("Number of terms detected (innerpage) = " + countOfTerms + "<br />");
			//System.out.println("Number of resutls that has terms after preprocessing: " + countOfResultsThatHasTermsAfterPreprocesssing + " out of " + results.size());
			sb.append("Number of resutls that has terms after preprocessing: " + countOfResultsThatHasTermsAfterPreprocesssing + " out of " + results.size() + "<br />");
			//System.out.println("Average: " + (double)countOfTerms / countOfResultsThatHasTermsAfterPreprocesssing);
			sb.append("Average: " + (double)countOfTerms / countOfResultsThatHasTermsAfterPreprocesssing + "<br />");
		}
		
		ResponseEntity<String> re = new ResponseEntity<String>(sb.toString(), HttpStatus.OK);

		return re;
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public ResponseEntity<String> test() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("Haytham" + "<br />");
		sb.append("Hello");
		
		ResponseEntity<String> re = new ResponseEntity<String>(sb.toString(), HttpStatus.OK);

		return re;
	}
}
