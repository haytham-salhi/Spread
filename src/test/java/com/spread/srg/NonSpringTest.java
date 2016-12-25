package com.spread.srg;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.spread.config.RootConfig;
import com.spread.experiment.RawSearchResult;
import com.spread.persistence.rds.model.Meaning;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.SearchEngine;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchEngineRepository;
import com.spread.persistence.rds.repository.SearchResultRepository;
import com.spread.persistence.rds.repository.TestRepository;

public class NonSpringTest {
	
	
	@Before
	public void setUp() throws Exception {
	}
	
	
	@Test
	public void testName1() throws Exception {
		
		Path path = Paths.get("Results/someFile/ss.txt");
		
		new File("Results/someFile/mam/sas").mkdirs();
		
		// This creates the file each time is executed
//		try (BufferedWriter writer = Files.newWriter(path.toFile(), Charset.forName("utf-8"))) {
//			writer.append("Hello mamss\n");
//			
//			writer.append("Hello mam");
//			FileWriter writer2 = new FileWriter(fileName, append);
//			
//		} catch (Exception e) {
//			System.out.println(e);
//		}
		
		try (BufferedWriter out = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream(path.toFile(), true),"UTF-8"))){
			
			out.write("\n7ayyallah");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	@Test
	public void testname()  {
		  Pattern p = Pattern.compile("\\p{Punct}");

	        Matcher m = p.matcher("One day! # when I was walking. I found your pants? just kidding...");
	        int count = 0;
	        while (m.find()) {
	            count++;
	            System.out.println("\nMatch number: " + count);
	            System.out.println("start() : " + m.start());
	            System.out.println("end()   : " + m.end());
	            System.out.println("group() : " + m.group());
	        }
	}
	
	@Test
	public void arabicTest()  {
		  Pattern p = Pattern.compile("[\\p{Punct}،؛,]");

	        Matcher m = p.matcher("   حيك يا حبيبي , ولكن  م؛ ماذا تقول~السلام عليكم !وحياكم الله ، واستودعكم الله");
	        int count = 0;
	        while (m.find()) {
	            count++;
	            System.out.println("\nMatch number: " + count);
	            System.out.println("start() : " + m.start());
	            System.out.println("end()   : " + m.end());
	            System.out.println("group() : " + m.group());
	        }
	}
	
	@Test
	public void testSplit() throws Exception {
		String[] values = "Incorrectly clustered instances :	11.0	 68.75   %".split("[ \r\n\t]");
		
		for (String string : values) {
			System.out.println(string + " >>" + string.length());
			
		}
		
		System.out.println(values.length);
	}
	
	@Test
	public void testGrouping() throws Exception {
		double[] assignments = {1.0, 2.0, 1.0, 3.0, 3.0, 3.0};
		
		Map<Double, Long> counts = Arrays.stream(assignments).boxed().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		System.out.println(counts);
	}
		
}
