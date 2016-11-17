package com.spread.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.spread.model.Meaning;

/**
 * 
 * @author Haytham Salhi
 *
 */
public abstract class FileDataLoader {
	
	private static final Logger LOGGER = LogManager.getLogger(FileDataLoader.class);
	
	/**
	 * File structure: 
	 * 
	 * @return
	 */
	public Map<String, List<Meaning>> loadQueries2() {
		Map<String, List<Meaning>> queryMap = new HashMap<String, List<Meaning>>();
		
		File file = new File(getClass().getClassLoader().getResource(getFileName()).getFile());
		
		try (CSVReader reader = new CSVReader(new FileReader(file), getSeperator(), CSVParser.DEFAULT_QUOTE_CHARACTER, 1)) {
			
			String ambiguousQuery = null;
			String [] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				
				if(!nextLine[0].isEmpty()) {
					ambiguousQuery = nextLine[0].trim();
				}
				
				if(!nextLine[0].isEmpty()) {
					ArrayList<Meaning> meanings = new ArrayList<Meaning>();
					meanings.add(new Meaning(nextLine[1].trim(), nextLine[2].trim(), nextLine[3].trim()));
					
					queryMap.put(ambiguousQuery, meanings);
				} else {
					queryMap.get(ambiguousQuery).add(new Meaning(nextLine[1].trim(), nextLine[2].trim(), nextLine[3].trim()));
				}
				
		    }
			
		} catch (IOException e) {
			LOGGER.error(e);
		}
		
		return queryMap;
	}
	
	/**
	 * File structure: ambig_query, clear_query1, clear_quer2, ...
	 * 
	 * @return
	 */
	public Map<String, List<String>> loadQueries() {
		Map<String, List<String>> queryMap = new HashMap<String, List<String>>();
		
		File file = new File(getClass().getClassLoader().getResource(getFileName()).getFile());
		
		try (CSVReader reader = new CSVReader(new FileReader(file), getSeperator())) {
			
			String [] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				queryMap.put(nextLine[0], Arrays.asList(nextLine).subList(1, nextLine.length));
		    }
			
		} catch (IOException e) {
			LOGGER.error(e);
		}
		
		return queryMap;
	}
	
	protected abstract String getFileName();

	private char getSeperator() {
		return ',';
	}
	
}
