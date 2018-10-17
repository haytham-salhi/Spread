package com.spread.senses;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Wikipedia implements KnowledgeBase {

	@Override
	public List<String> getSenses(String word, String language) {
		//String englishQuery = "chef";
		//String arabicQuery = "أمازون";
		
		// english url
		//String urlEndpoint = "https://en.wikipedia.org/w/api.php?action=query&titles=" + englishQuery + "_(disambiguation)&prop=extracts&format=json&redirects=true";
		String urlEndpoint = "https://ar.wikipedia.org/w/api.php?action=query&titles=" + word + "_(توضيح)&prop=extracts&format=json&redirects=true";
		
		//Document document = Jsoup.connect(urlEndpoint)
		//		.ignoreContentType(true)
		//		.get();
		
		RestTemplate restTemplate = new RestTemplate();
		String jsonResponse = restTemplate.getForObject(urlEndpoint, String.class);
		
		//String jsonResponse = document.body().
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = objectMapper.readTree(jsonResponse);
			
			// Get extract field, which its value is html 
			String extract = rootNode.findValue("extract").asText(); 
			
			// Parse
			Document doc = Jsoup.parse(extract);
			Elements elements = doc.select("li");
			
			// Convert them to list of strings
			return elements.stream().map(element -> element.text()).collect(Collectors.toList());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();		
		}
		
		return null;
	}

}
