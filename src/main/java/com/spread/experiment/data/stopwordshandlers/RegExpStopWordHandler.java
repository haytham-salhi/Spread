package com.spread.experiment.data.stopwordshandlers;

import java.util.regex.Pattern;

import weka.core.stopwords.AbstractStopwords;

/**
 * 
 * @author Haytham Salhi
 *
 */
public class RegExpStopWordHandler
  extends AbstractStopwords {

	private static final long serialVersionUID = 174647626968747213L;
	
	private String regex;
	private Pattern pattern;
	
	public RegExpStopWordHandler(String regex) {
		this.regex = regex;
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		pattern = Pattern.compile(regex);
	}

/**
   * Returns a string describing the stopwords scheme.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Stopwords list based on Regex: " + regex;
  }

  /**
   * Returns true if the given string is a stop word.
   *
   * @param word the word to test
   * 
   * @return true if the word is a stopword
   */
  @Override
  protected boolean is(String word) {
	  if (pattern.matcher(word.trim().toLowerCase()).matches()) {
		  if (m_Debug)
			  debug(pattern.pattern() + " --> true");
		  return true;
	  }
      else {
    	  if (m_Debug)
    		  debug(pattern.pattern() + " --> false");
    	  return false;
      }
  }
}