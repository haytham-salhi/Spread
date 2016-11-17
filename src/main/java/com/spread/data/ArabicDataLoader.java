package com.spread.data;

import org.springframework.stereotype.Component;

/**
 * 
 * @author Haytham Salhi
 *
 */
@Component("FILE-AR")
public class ArabicDataLoader extends FileDataLoader {

	@Override
	protected String getFileName() {
		return "arabic-queries-2.csv";
	}

}
