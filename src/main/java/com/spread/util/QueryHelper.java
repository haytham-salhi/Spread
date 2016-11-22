package com.spread.util;

import com.spread.model.Meaning;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.QueryFormulationStartegy;

public class QueryHelper {
	
	public static String formulateQuery(
			String ambiguousQuery, Meaning meaning, Language lang) {
		String query;
		if(meaning.getFormulationStartegy() == QueryFormulationStartegy.APPEND) {
			if(lang == Language.AR) {
				query = meaning.getName() + " " + ambiguousQuery; // == APPEND_RIGHT (done this because it is arabic)
			} else if (lang == Language.EN) {
				query = ambiguousQuery + " " + meaning.getName(); // == APPEND_RIGHT (done this because it is english)
			} else {
				query = ambiguousQuery + " " + meaning.getName();
			}
		//} else if(meaning.getFormulationStartegy() == QueryFormulationStartegy.APPEND_RIGHT) {
			//query = meaning.getName() + " " + ambiguousQuery;
		//} else if(meaning.getFormulationStartegy() == QueryFormulationStartegy.APPEND_LEFT) {
			//query = ambiguousQuery + " " + meaning.getName();
		} else if(meaning.getFormulationStartegy() == QueryFormulationStartegy.NO_APPEND) {
			query = meaning.getName();
		} else {
			query = meaning.getName();
		}
		
		return query;
	}

}
