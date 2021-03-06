package com.spread.general;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.Customsearch.Cse.List;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import com.opencsv.CSVReader;
import com.spread.config.RootConfig;
import com.spread.fetcher.SearchEngineFetcher;
import com.spread.frontcontrollers.HelloController;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.util.nlp.arabic.SpreadArabicPreprocessor;

public class GeneralNonSpringTest {
	
	private static final Logger logger = LogManager.getLogger(GeneralNonSpringTest.class);

	
	@Before
	public void setUp() throws Exception {
	}

	
	@Test
	public void innerPageFetchingyTesting() throws Exception {
		
		// Url: http://arabou.edu.sa/  the case where there is iframe tage!!
		
		//Document document = Jsoup.connect("https://ar.wikipedia.org/wiki/جامعة_الدول_العربية").timeout(30000).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").get();
		
		Document document = Jsoup.connect("http://www.aljazeera.net/programs/arab-present-situation/2016/2/28/%D8%A7%D9%84%D8%AC%D8%A7%D9%85%D8%B9%D8%A9-%D8%A7%D9%84%D8%B9%D8%B1%D8%A8%D9%8A%D8%A9-%D9%85%D9%86-%D9%8A%D8%AE%D9%84%D9%81-%D9%86%D8%A8%D9%8A%D9%84-%D8%A7%D9%84%D8%B9%D8%B1%D8%A8%D9%8A")
				.timeout(30000)
				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36")
				//.referrer("http://www.google.com")
				//.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				//.header("Accept-Encoding", "gzip, deflate, sdch")
				//.header("Accept-Language", "en-US,en;q=0.8,ar;q=0.6")
				//.header("Cookie", "__gads=ID=2de4d9ab30813a5f:T=1478995992:S=ALNI_MZtn2XADSAhdFwm9fvEUIjHroX_pw; _cb_ls=1; mf_0735d2ef-61eb-441f-bf50-fa0c11e4c547=-1; __asc=ed50be011586ac5cb8223b92396; __auc=f39094f51585b3ec05a0ddd33cd; _ga=GA1.2.650286486.1478999589; _gat=1; _em_t=true; _em_vt=cc75fb053df469516af3931670c25501e571d418e6-87334037582bac67; _em_v=79c2fe3cb3fb7de1fd6578e42eff582ba9bafef5d2-27738072582bac67; owa_v=cdh%3D%3E2ed1b53e%7C%7C%7Cvid%3D%3E1478999597361649466%7C%7C%7Cfsts%3D%3E1478999597%7C%7C%7Cdsfs%3D%3E3%7C%7C%7Cnps%3D%3E2; owa_s=cdh%3D%3E2ed1b53e%7C%7C%7Clast_req%3D%3E1479260790%7C%7C%7Csid%3D%3E1479260108884397022%7C%7C%7Cdsps%3D%3E3%7C%7C%7Creferer%3D%3E%28none%29%7C%7C%7Cmedium%3D%3Edirect%7C%7C%7Csource%3D%3E%28none%29%7C%7C%7Csearch_terms%3D%3E%28none%29; _cb=DRA_WYDVtLelefZhd; _chartbeat2=.1430780611391.1479260789639.0000000000001001.BYNtqNBBPnWeDs9CJwDrhfwIBmqC_H; BC_BANDWIDTH=1479260111011X6725")
				//.header("Cache-Control", "max-age=0")
				//.header("Connection", "keep-alive")
				//.header("If-Modified-Since", "Wed, 16 Nov 2016 00:43:26 GMT")
				//.header("Upgrade-Insecure-Requests", "1")
				//.header("Host", "www.aljazeera.net")
				.get();
		
		System.out.println(document.outerHtml());
		//System.out.println(document.text());
		
		//System.out.println(Jsoup.parse(document.outerHtml()).text());;
		
	}
	
	@Test
	public void testWikipedia() throws Exception {
		java.util.List<String> senses = getSensesFromArabicWikipedia("الملك عبد الله", false);
		
		for (String sense : senses) {
			System.out.println(sense);
		}
	}
	
	java.util.List<String> getSensesFromArabicWikipedia(String arabicQuery, boolean debug) {
		//String englishQuery = "chef";
		//String arabicQuery = "أمازون";
		
		// english url
		//String urlEndpoint = "https://en.wikipedia.org/w/api.php?action=query&titles=" + englishQuery + "_(disambiguation)&prop=extracts&format=json&redirects=true";
		String urlEndpoint = "https://ar.wikipedia.org/w/api.php?action=query&titles=" + arabicQuery + "_(توضيح)&prop=extracts&format=json&redirects=true";
		
		//Document document = Jsoup.connect(urlEndpoint)
		//		.ignoreContentType(true)
		//		.get();
		
		RestTemplate restTemplate = new RestTemplate();
		String jsonResponse = restTemplate.getForObject(urlEndpoint, String.class);
		
		if(debug)
			System.out.println(jsonResponse);
		
		//String jsonResponse = document.body().
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = objectMapper.readTree(jsonResponse);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Get extract field, which its value is html 
		String extract = rootNode.findValue("extract").asText(); 
		
		// Parse
		Document doc = Jsoup.parse(extract);
		Elements elements = doc.select("li");
		
		// Convert them to list of strings
		return elements.stream().map(element -> element.text()).collect(Collectors.toList());
	}
	
	@Test
	public void testName() throws Exception {
		File file = new File("src/main/resources/Ambigous Queries Data Set - Collection.csv");
		
		CSVReader reader  = new CSVReader(new FileReader(file), ',');
		String [] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
	    	System.out.println(nextLine[4] + " ");
	    }
	}
	
	@Test
	public void testName1() throws Exception {
		String query = "عدنان ابراهيم";
		
		System.out.println(query + " " + "المخرج");
		
		System.out.println("Adnan Ibrahim"  + " " + "ALMO5REJ" );
	}
	
	@Test
	public void testName2() throws Exception {
		System.out.println(132_217_728);
	}
	
	@Test
	public void testName3() throws Exception {
		File file = new File(getClass().getClassLoader().getResource("stopwords.txt").getFile());
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line;
		while ((line = reader.readLine()) != null)
			System.out.println(line);	
		}
	
	@Test
	public void testEnum() throws Exception {
		String s = "Google";
		
		System.out.println(SearchEngineCode.valueOf("GOsOGLE"));
	}
	
	@Test
	public void fileWriting() throws Exception {
		
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
		
		
		int[][] values = {{1,2,3,4,5,6,7,8}, {11,2,3,4,5,6,7,8}, {12,2,3,4,5,6,7,8}};
		
		for (int[] is : values) {
			for (int i : is) {
				try (BufferedWriter out = new BufferedWriter
					    (new OutputStreamWriter(new FileOutputStream(path.toFile(), true),"UTF-8"))){
					
					out.write(i + ",");
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			try (BufferedWriter out = new BufferedWriter
				    (new OutputStreamWriter(new FileOutputStream(path.toFile(), true),"UTF-8"))){
				
				out.write("\n");
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		
		
		
	}
	
	@Test
	public void testWriting() throws Exception {
		String s = "Haytham";
		
		Files.write(Paths.get("test.txt"), (s + System.lineSeparator()).getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

	}
	
	@Test
	public void testInter() throws Exception {
		SpreadArabicPreprocessor spreadArabicPreprocessor = new SpreadArabicPreprocessor();
		
		String s1 = "هل هل هل يي سي ";//"حين يقال رحم الله فلان كان وكان كان اسماؤنا وبها سمت اسماؤنا";
		String s2 = "ماو ماااا ماااااا";//"اعمارنا اعمالتا وبها سمت اسماؤنا نمضي وتبقى ها وتبقى هنا ";
			
		java.util.List<String> list1 = spreadArabicPreprocessor.tokenizeBySpaceAndRemoveExcessiveSpaces(s1);	
		java.util.List<String> list2 = spreadArabicPreprocessor.tokenizeBySpaceAndRemoveExcessiveSpaces(s2);	
		
		
		System.out.println(list1);
		System.out.println(list2);
		
		Set<String> set1 = list1.stream().collect(Collectors.toSet());
		Set<String> set2 = list2.stream().collect(Collectors.toSet());	
		
		System.out.println(set1);
		System.out.println(set2);
		
		System.out.println(intersection(set1, set2));
		System.out.println(union(set1, set2));

		
		
		


		
		
		

		

		
	}
	
	public static int intersection(Set<String> a, Set<String> b) {
		Set<String> intersection = new HashSet<>(a);
		intersection.retainAll(b);
		
		return intersection.size();
	}
	
	public static int union(Set<String> a, Set<String> b) {
		Set<String> union = new HashSet<>(a);
		union.addAll(b);
		
		return union.size();
	}
}
