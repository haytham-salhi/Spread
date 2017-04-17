package com.spread.persistence;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.spread.config.RootConfig;
import com.spread.frontcontrollers.labeling.model.YesNoAnswer;
import com.spread.persistence.rds.model.Meaning;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.SearchEngine;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.User;
import com.spread.persistence.rds.model.UserSearchResultAssessment;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchEngineRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.persistence.rds.repository.TestRepository;
import com.spread.persistence.rds.repository.UserRepository;
import com.spread.persistence.rds.repository.UserSearchResultAssessmentRepository;

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class GeneralTest {
	
	private static final Logger logger = LogManager.getLogger(GeneralTest.class);
	
	@Autowired
	private SearchResultRepository searchResultRepository;
	
	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private QueryRepository queryRepository;
	
	@Autowired
	private SearchEngineRepository searchEngineRepository;
	
	@Autowired
	private UserSearchResultAssessmentRepository userSearchResultAssessmentRepository;
	
	@Autowired
	private MeaningRepository meaningRepository;
	
	@Autowired
	private UserRepository userRepository;

	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void findByQueryAndSearchEngineWithBasicInfoTest() throws Exception {
		List<SearchResult> results = searchResultRepository.findByQueryAndSearchEngineWithBasicInfo("عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
		
		results.forEach(System.out::println);
		System.out.println(results.size());
	}
	
	@Test
	public void SearchResultRepositoryTest() throws Exception {
		//searchResultRepository.findByQuerySearchEngine_Query_NameAndQuerySearchEngine_SearchEngine_Code("عمان", SearchEngineCode.GOOGLE);
		List<SearchResult> results = searchResultRepository.findByQueryAndSearchEngine("عمان", SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
		
		results.forEach(System.out::println);
		System.out.println(results.size());
	}

	@Test
	public void test2() {
		logger.info("Hello");
		//System.out.println(googleFetcher.fetch(2));
		
		com.spread.persistence.rds.model.Test t = new com.spread.persistence.rds.model.Test();
		t.setName("👽💔"); // Such these chars are solved after we append character_set_server=utf8mb4 and remove the characterEncoding=utf8
		
		testRepository.save(t); 
	}
	@Test
	public void test3() {
		logger.info("Hello");
		//System.out.println(googleFetcher.fetch(2));
		
		com.spread.persistence.rds.model.Test t = new com.spread.persistence.rds.model.Test();
		t.setName("عدي الصالحيddsda d 👽💔"); // Such these chars are solved after we append character_set_server=utf8mb4 and remove the characterEncoding=utf8
		
		testRepository.save(t); 
	}
	
	@Test
	public void test1() throws Exception {
		com.spread.persistence.rds.model.Test ob = testRepository.findOne(54);
		
		System.out.println(ob.getName());
	}
	
	@Test
	public void queryTest() throws Exception {
		Query query = new Query("الله أكبر ولله الحمد", Language.EN, true, null, null);
		
		query = queryRepository.save(query);
		
		System.out.println(query);
	}
	
	@Test
	public void findByCodeAndLanguageAndLocationTest() throws Exception {
		SearchEngine se = searchEngineRepository.findByCodeAndLanguageAndLocation(SearchEngineCode.GOOGLE, SearchEngineLanguage.AR, Location.PALESTINE);
		
		System.out.println(se);
	}
	
	@Test
	public void countRespondentsByQueryIdTest() throws Exception {
		System.out.println(userSearchResultAssessmentRepository.countRespondentsByQueryId(3, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR));
	}
	
	@Test
	public void findAgreedRelevantByQueryAndSearchEngineTest() throws Exception {
		List<SearchResult> result = searchResultRepository.findAgreedRelevantByQueryAndSearchEngine(6, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, null);
		
		System.out.println(result.size());
	}
	
	@Test
	public void findByIsAmbiguousAndIsOfficialAndAllowedUserTest() throws Exception {
		List<Query> result = queryRepository.findByIsAmbiguousAndIsOfficialAndAllowedUser_Id(false, true, 5);
		
		System.out.println(result.size());
		
		System.out.println(result.get(0).getAllowedUser().getName());
	}
	
	@Test
	public void findRelevantArabicWithInnerPagesByQueryAndSearchEngineTest() throws Exception {
		
		List<Query> queries = queryRepository.findByIsAmbiguousAndIsOfficial(true, true, null);
		
		for (Query query : queries) {
			
			List<Meaning> clearMeaningsWithClearQueriesForAq = meaningRepository.findOfficialMeaningsWithClearQueries(query.getId());
			
			for (Meaning meaning : clearMeaningsWithClearQueriesForAq) {
				
				List<SearchResult> result = searchResultRepository.findRelevantArabicWithInnerPagesByQueryAndSearchEngine(meaning.getClearQuery().getId(), SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, null);
				//result.forEach(n -> System.out.println(n.getTitle()));

				System.out.println(result.size());
			}
			
		}
		
		
		
		
	}
	
	
	@Test
	public void testName() throws Exception {
		List<Query> queries = queryRepository.findByIsAmbiguousAndIsOfficial(true, true, null);
		
		for (Query query : queries) {
			
			List<Meaning> clearMeaningsWithClearQueriesForAq = meaningRepository.findOfficialMeaningsWithClearQueries(query.getId());
			
			System.out.println(clearMeaningsWithClearQueriesForAq.size());
		}
		
	}
	
	@Test
	public void check() throws Exception {
		
		Integer clearQueryId = 2;

		Integer userId = 2;
		Integer userId2 = 1;
		
		Pageable pageRequest = new PageRequest(0, 30);
		
		List<SearchResult> results1 = searchResultRepository.findRelevantArabicWithInnerPagesByQueryAndSearchEngine(clearQueryId, userId ,SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, pageRequest);
		
		List<SearchResult> results2 = searchResultRepository.findRelevantArabicWithInnerPagesByQueryAndSearchEngine(clearQueryId, userId2 ,SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, pageRequest);
		
		for (SearchResult searchResult : results1) {
			if(!results2.contains(searchResult)) {
				
				System.out.print(searchResult.getId() + ", ");
				
			}
		}
	
	}
	
	@Test
	public void findRelevanceAgreementStatsScript() throws Exception {
		
		// Get all ambiguous queries
		List<Query> queries = queryRepository.findByIsAmbiguousAndIsOfficial(true, true, null);
		
		for (Query query : queries) {
			
			List<Meaning> clearMeaningsWithClearQueriesForAq = meaningRepository.findOfficialMeaningsWithClearQueries(query.getId());
			
			// For each clear query
			for (Meaning meaning : clearMeaningsWithClearQueriesForAq) {
				
				List<Integer> respondentsIds = userSearchResultAssessmentRepository.findRespondentIdsByQueryId(meaning.getClearQuery().getId(), SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR);
				System.out.println("Respondents (users) for " + meaning.getClearQuery().getName() + ": " + respondentsIds);
				
				int judge1 = 2; // Haytham
				String judge1Name = "Haytham";
				respondentsIds.remove(new Integer(2));
				int judge2 = respondentsIds.get(0);
				User judge2Obj = userRepository.findOne(judge2);
				
				// Get data of agreements between the two judges
				List<UserSearchResultAssessment> agreedResults = userSearchResultAssessmentRepository.findAgreedAssessmentsByQueryAndSearchEngine(meaning.getClearQuery().getId(), judge1, judge2, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, null);
				System.out.println("Number of agreements: " + agreedResults.size());
				int agreedYeses = (int) agreedResults.stream().filter(n -> n.getIsRelevant() == YesNoAnswer.YES).count();
				int agreedNoes = (int) agreedResults.stream().filter(n -> n.getIsRelevant() == YesNoAnswer.NO).count();
				System.out.println("Number of agreed yes's: " + agreedYeses); // --- > x1
				System.out.println("Number of agreed no's: " + agreedNoes); // ---> x2
				
				// Get data for judge 1
				List<UserSearchResultAssessment> judge1Data = userSearchResultAssessmentRepository.findAssessmentByQueryAndJudge(meaning.getClearQuery().getId(), judge1, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, null);
				System.out.println("Number of " + judge1Name + "'s yes's: " + judge1Data.stream().filter(n -> n.getIsRelevant() == YesNoAnswer.YES).count());
				System.out.println("Number of " + judge1Name + "'s no's: " + judge1Data.stream().filter(n -> n.getIsRelevant() == YesNoAnswer.NO).count());
				
				// Get data for judge 2
				List<UserSearchResultAssessment> judge2Data = userSearchResultAssessmentRepository.findAssessmentByQueryAndJudge(meaning.getClearQuery().getId(), judge2, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, null);
				System.out.println("Number of " + judge2Obj.getName() + "'s yes's: " + judge2Data.stream().filter(n -> n.getIsRelevant() == YesNoAnswer.YES).count());
				System.out.println("Number of " + judge2Obj.getName() + "'s no's: " + judge2Data.stream().filter(n -> n.getIsRelevant() == YesNoAnswer.NO).count());
				
				// Number of assessments where judge1 --> Yes Judge2 --> NO
				List<UserSearchResultAssessment> disagreementsJudge1YesJudge2No = userSearchResultAssessmentRepository.findNotAgreedAssessmentsByQueryAndSearchEngine(meaning.getClearQuery().getId(), judge1, judge2, YesNoAnswer.YES, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, null); // --> x3
				System.out.println("Number of assessments where " + judge1Name + " --> Yes " + judge2Obj.getName() + " --> NO: " + disagreementsJudge1YesJudge2No.size());
				// Number of assessments judge1 --> NO Judge2 --> YES
				List<UserSearchResultAssessment> disagreementsJudge1NoJudge2Yes = userSearchResultAssessmentRepository.findNotAgreedAssessmentsByQueryAndSearchEngine(meaning.getClearQuery().getId(), judge1, judge2, YesNoAnswer.NO, SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, null); // --> x4
				System.out.println("Number of assessments where " + judge1Name + " --> NO " + judge2Obj.getName() + " --> Yes: " + disagreementsJudge1NoJudge2Yes.size());
				
				// Calculate Kappa
				int total = agreedResults.size() + disagreementsJudge1YesJudge2No.size() + disagreementsJudge1NoJudge2Yes.size();
				float proportionOfAgreements = agreedResults.size() / (float)total;
				float relevantMarginal = (agreedYeses + disagreementsJudge1YesJudge2No.size() + agreedYeses + disagreementsJudge1NoJudge2Yes.size()) / ((float)total * 2);
				float nonRelevantMarginal = (agreedNoes + disagreementsJudge1YesJudge2No.size() + agreedNoes + disagreementsJudge1NoJudge2Yes.size()) / ((float)total * 2);

				float pOfE = (float) (Math.pow(relevantMarginal, 2) + Math.pow(nonRelevantMarginal, 2));
				// x1 + x2 + x3 + x4 = total of assessed results (100)
				float k = (proportionOfAgreements - pOfE)/(1 - pOfE);
				System.out.println("Kappa statistic: " + k);
				
				System.out.println("-------------------------------");
				System.out.println("-------------------------------");

			}
			
		}
		
//		Integer clearQueryId = 2;
//
//		Integer userId = 2;
//		Integer userId2 = 1;
//		
//		Pageable pageRequest = new PageRequest(0, 30);
//		
//		List<SearchResult> results1 = searchResultRepository.findRelevantArabicWithInnerPagesByQueryAndSearchEngine(clearQueryId, userId ,SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, pageRequest);
//		
//		List<SearchResult> results2 = searchResultRepository.findRelevantArabicWithInnerPagesByQueryAndSearchEngine(clearQueryId, userId2 ,SearchEngineCode.GOOGLE, Location.PALESTINE, SearchEngineLanguage.AR, pageRequest);
//		
//		for (SearchResult searchResult : results1) {
//			if(!results2.contains(searchResult)) {
//				
//				System.out.print(searchResult.getId() + ", ");
//				
//			}
//		}
	
	}
}
