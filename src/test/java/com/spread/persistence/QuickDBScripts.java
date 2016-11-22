package com.spread.persistence;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.spread.config.RootConfig;
import com.spread.data.FileDataLoader;
import com.spread.model.Meaning;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.util.QueryHelper;

@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class QuickDBScripts {
	
	private static final Logger logger = LogManager.getLogger(QuickDBScripts.class);
	
	@PersistenceContext(unitName = "spreadPU")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("FILE-AR")
	private FileDataLoader arabicDataLoader;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void findIntersectionsCount() {
		Map<String, List<Meaning>> queries = arabicDataLoader.loadQueriesAsLinkedHashMap();
		
		SearchEngineCode sec = SearchEngineCode.BING;
		Location loc = Location.PALESTINE;
		
		for (String ambQuery : queries.keySet()) {
			
			List<Meaning> meanings = queries.get(ambQuery);
			
			System.out.println("For " + ambQuery);
			
			for (Meaning meaning : meanings) {
				String formulatedQuery = QueryHelper.formulateQuery(ambQuery, meaning, Language.AR);
				
				int count = ((Number) entityManager.createNativeQuery("select COUNT(search_result.id) from search_result inner join query_search_engine on search_result.query_search_engine_id = query_search_engine.id inner join query on query_search_engine.query_id = query.id inner join search_engine on query_search_engine.search_engine_id = search_engine.id where query.name = '" + ambQuery + "' and search_engine.code = '" + sec.name() + "' and search_engine.location = '" + loc.name() + "' and search_result.url in ( select search_result.url from search_result inner join query_search_engine on search_result.query_search_engine_id = query_search_engine.id inner join query on query_search_engine.query_id = query.id inner join search_engine on query_search_engine.search_engine_id = search_engine.id where query.name = '" + formulatedQuery + "' and search_engine.code = '" + sec.name() + "' and search_engine.location = '" + loc.name() + "')").getSingleResult()).intValue();
				
				System.out.println("Intersection with " + formulatedQuery + ": " + count);
			}
		}
	}
	
	@Test
	public void findSearchResultsSizesForEachQuery() {
		Map<String, List<Meaning>> queries = arabicDataLoader.loadQueriesAsLinkedHashMap();
		
		SearchEngineCode sec = SearchEngineCode.BING;
		Location loc = Location.PALESTINE;
		
		for (String ambQuery : queries.keySet()) {
			
			List<Meaning> meanings = queries.get(ambQuery);
			
			int count = ((Number) entityManager.createNativeQuery("select COUNT(search_result.id) from search_result inner join query_search_engine on search_result.query_search_engine_id = query_search_engine.id inner join query on query_search_engine.query_id = query.id inner join search_engine on query_search_engine.search_engine_id = search_engine.id where query.name = '" + ambQuery + "' and search_engine.code = '" + sec.name() + "' and search_engine.location = '" + loc.name() + "'").getSingleResult()).intValue();
			
			//System.out.println("For ambQuery: " + count);
			System.out.println(count);
			
			for (Meaning meaning : meanings) {
				String formulatedQuery = QueryHelper.formulateQuery(ambQuery, meaning, Language.AR);
				
				count = ((Number) entityManager.createNativeQuery("select COUNT(search_result.id) from search_result inner join query_search_engine on search_result.query_search_engine_id = query_search_engine.id inner join query on query_search_engine.query_id = query.id inner join search_engine on query_search_engine.search_engine_id = search_engine.id where query.name = '" + formulatedQuery + "' and search_engine.code = '" + sec.name() + "' and search_engine.location = '" + loc.name() + "'").getSingleResult()).intValue();
				
				//System.out.println("For " + formulatedQuery + ": " + count);
				System.out.println(count);
			}
		}
		
	}
}
