package com.spread.data;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import com.spread.config.DataTestConfig;
import com.spread.model.Meaning;

@ContextConfiguration(classes = { DataTestConfig.class })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FileDataLoaderTest {
	
	@Autowired
	@Qualifier("FILE-AR")
	private FileDataLoader arabicDataLoader;
	
	

	@Before
	public void setUp() throws Exception {
		System.out.println("Hello Jenkinssssssss");
	}
	
	@Test
	public void loadQueries2Test() throws Exception {
		Map<String, java.util.List<Meaning>> map = arabicDataLoader.loadQueries2();
		
		for (String key : map.keySet()) {
			System.out.println("Amb query: " + key);
			System.out.println(map.get(key));
		}
	}
}