package com.spread.data;

import org.springframework.stereotype.Component;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Component("FILE-EN")
public class EnglishDataLoader extends FileDataLoader {

	@Override
	protected String getFileName() {
		return "english-queries.txt";
	}

}
