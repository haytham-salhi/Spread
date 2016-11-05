package com.spread.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVReader;

/**
 * 
 * @author Haytham Salhi
 *
 */
public abstract class FileDataLoader {
	
	private static final Logger LOGGER = LogManager.getLogger(FileDataLoader.class);

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
