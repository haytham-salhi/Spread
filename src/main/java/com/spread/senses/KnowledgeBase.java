package com.spread.senses;

import java.util.List;


public interface KnowledgeBase {
	List<String> getSenses(String word, String language);
}
