package com.spread.util.nlp.arabic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import weka.core.stemmers.Stemmer;

import com.spread.util.nlp.arabic.thirdparty.maha.Constants;

/**
 * 
 * @author Haytham Salhi
 *
 */
public class SpreadArabicPreprocessor {
	
	private static final Logger LOGGER = LogManager.getLogger("spreadArabicPreprocessorLogger");
	
	/**
	 * It normalizes the query (Letter normalization with stemmer only!)
	 * @param query
	 * @return
	 */
	public String processAmbiguousQuery(String query, boolean letterNormalization, Stemmer stemmer) {
		LOGGER.info("Initial query:");
		LOGGER.info(query);
		LOGGER.info("==================");
		
		if(letterNormalization) {
			query = normalize(query);
			
			LOGGER.info("After removing tatweel, working on alef wal yaa wal haa:");
			LOGGER.info(query);
			LOGGER.info("==================");
		}
		
		// No need for other preprocessors because it is a query made by us, so it will not contain punct, diac, etc 
		
		if(stemmer != null) {
			query = stemText(query, stemmer);
			
			LOGGER.info("After stemming:");
			LOGGER.info(query);
			LOGGER.info("==================");
		}
		
		LOGGER.info("After removing tatweel, working on alef wal yaa wal haa:");
		LOGGER.info(query);
		LOGGER.info("==================");
		
		return query;
	}
	
	/**
	 * It does the following: 
	 * <br>
	 * 1- Normalization of ALEF, HAA, YAA, and Tatweel<br>
	 * 2- Diacritics removal<br>
	 * 3- Punct removal <br>
	 * 4- Non arabic words removal: Note that this will remove anything non arabic (e.g. English numbers)<br>
	 * 5- Arabic Numbers removal<br>
	 * 6- Non alphapatic words removal<br>
	 * 7- Stop words removal<br>
	 * 8- Specific words removal<br>
	 * 9- Stemming<br>
	 * 
	 * @param text
	 * @param stemmer
	 * @param wordsToRemove
	 * @return
	 */
	public String process(String text, 
			Stemmer stemmer,
			boolean letterNormalization,
			boolean diacriticsRemoval,
			boolean puncutationRemoval,
			boolean nonArabicWordsRemoval,
			boolean arabicNumbersRemoval,
			boolean nonAlphabeticWordsRemoval,
			boolean stopWordsRemoval,
			List<String> wordsToRemove) {
		LOGGER.info("Initial:");
		LOGGER.info(text);
		LOGGER.info("==================");
		
		if(letterNormalization) {
			text = normalize(text);
			
			LOGGER.info("After removing tatweel, working on alef wal yaa wal haa:");
			LOGGER.info(text);
			LOGGER.info("==================");
		}
		
		if(diacriticsRemoval) {
			text = removeDiacritics(text);
			
			LOGGER.info("After removing diacs:");
			LOGGER.info(text);
			LOGGER.info("==================");
		}
		
		if(puncutationRemoval) {
			text = removePunctuations(text);
			
			LOGGER.info("After removing puncuations:");
			LOGGER.info(text);
			LOGGER.info("==================");
		}
		
		if(nonArabicWordsRemoval) {
			text = removeNonArabicWords(text);
			
			LOGGER.info("After removing non arabic words:");
			LOGGER.info(text);
			LOGGER.info("==================");
		}

		if(arabicNumbersRemoval) {
			text = removeNumbers(text);
			
			LOGGER.info("After removing numbers:");
			LOGGER.info(text);
			LOGGER.info("==================");
		}
		
		if(nonAlphabeticWordsRemoval) {
			text = removeNonAlphabeticWords(text);
			
			LOGGER.info("After removing AlphabeticWords:");
			LOGGER.info(text);
			LOGGER.info("==================");
		}

		if(stopWordsRemoval) {
			text = removeStopWords(text);
			
			LOGGER.info("After removing stopWords:");
			LOGGER.info(text);
			LOGGER.info("==================");
		}
		
		if(stemmer != null) {
			text = stemText(text, stemmer);
			
			LOGGER.info("After stemming:");
			LOGGER.info(text);
			LOGGER.info("==================");
		}
		
		if(wordsToRemove != null && wordsToRemove.size() > 0) {
			text = removeSpecificWords(text, wordsToRemove);
			
			LOGGER.info("After removing specific words:");
			LOGGER.info(text);
			LOGGER.info("==================");
		}

		
		LOGGER.info("==================");
		LOGGER.info("==================");
		
		return text;
	}
	
	public String normalize(String text) {
		// Removes excessive spaces
		// Letter normalization (ALIF, TAA, AND YAA)
		// Tatweel remover
		
		ArrayList<String> tokens = new ArrayList<String>();
    	StringBuffer modifiedWord = new StringBuffer("");
	    ArrayList<String> modifiedText = tokenizeBySpaceAndRemoveExcessiveSpaces(text);
	    
	    // For each token in the text
	    for (int i=0;i<modifiedText.size();i++) {
	    	 modifiedWord.setLength(0);
	    	 String ctoken=modifiedText.get(i);
	    	 // normalization of hamzated alif to bare alif 
	    	 ctoken=ctoken.replace(Constants.ALIF_HAMZA_ABOVE, Constants.ALIF);
	    	 ctoken=ctoken.replace(Constants.ALIF_HAMZA_BELOW, Constants.ALIF);
	    	 ctoken=ctoken.replace(Constants.ALIF_HAMZA_ABOVE, Constants.ALIF);
	    	 ctoken=ctoken.replace(Constants.ALIF_MADDA, Constants.ALIF);
	    	 
	    	 // Normalization of taa  marbutah to haa   
	    	 ctoken=ctoken.replace(Constants.TAA_MARBUTA, Constants.HAA);
	    	 
	    	 // Normalization of dotless yaa to yaa 
	    	 ctoken=ctoken.replace(Constants.DOTLESS_YAA, Constants.YAA);

	    	 for (int j=0; j<ctoken.length(); j++) {
	    		 if ( !(Constants.TATWEEL.contains(ctoken.substring(j,j+1))) )
	                 modifiedWord.append(ctoken.substring(j,j+1));
	             else {
	                 
	             }
	    	}
	    	 tokens.add(modifiedWord.toString());
	    }  
	    
	    String result = "";
	
	    for (String t: tokens) {
		    result=result+t+" ";
	    }
	    
	    if(!result.isEmpty()) {
	    	return result.substring(0, result.length()-1);
	    } else {
	      	return result;
	    }
	}
	
	
	public String normalizeTatweel(String text) {
		// Tatweel remover
		
		ArrayList<String> tokens = new ArrayList<String>();
    	StringBuffer modifiedWord = new StringBuffer("");
	    ArrayList<String> modifiedText = tokenizeBySpaceAndRemoveExcessiveSpaces(text);
	    
	    // For each token in the text
	    for (int i=0;i<modifiedText.size();i++) {
	    	 modifiedWord.setLength(0);
	    	 String ctoken=modifiedText.get(i);

	    	 for (int j=0; j<ctoken.length(); j++) {
	    		 if ( !(Constants.TATWEEL.contains(ctoken.substring(j,j+1))) )
	                 modifiedWord.append(ctoken.substring(j,j+1));
	             else {
	                 
	             }
	    	}
	    	 tokens.add(modifiedWord.toString());
	    }
	    
	    String result = "";
	
	    for (String t: tokens) {
		    result=result+t+" ";
	    }
	    
	    if(!result.isEmpty()) {
	    	return result.substring(0, result.length()-1);
	    } else {
	      	return result;
	    }
	}
	
	public String normalizeAlif(String text) {
		// Removes excessive spaces
		// Letter normalization (ALIF, TAA, AND YAA)
		// Tatweel remover
		
		ArrayList<String> tokens = new ArrayList<String>();
    	StringBuffer modifiedWord = new StringBuffer("");
	    ArrayList<String> modifiedText = tokenizeBySpaceAndRemoveExcessiveSpaces(text);
	    
	    // For each token in the text
	    for (int i=0;i<modifiedText.size();i++) {
	    	 modifiedWord.setLength(0);
	    	 String ctoken=modifiedText.get(i);
	    	 // normalization of hamzated alif to bare alif 
	    	 ctoken=ctoken.replace(Constants.ALIF_HAMZA_ABOVE, Constants.ALIF);
	    	 ctoken=ctoken.replace(Constants.ALIF_HAMZA_BELOW, Constants.ALIF);
	    	 ctoken=ctoken.replace(Constants.ALIF_HAMZA_ABOVE, Constants.ALIF);
	    	 ctoken=ctoken.replace(Constants.ALIF_MADDA, Constants.ALIF);
	    	 
	    	 for (int j=0; j<ctoken.length(); j++) {
	    		 if ( !(Constants.TATWEEL.contains(ctoken.substring(j,j+1))) )
	                 modifiedWord.append(ctoken.substring(j,j+1));
	             else {
	                 
	             }
	    	}
	    	 tokens.add(modifiedWord.toString());
	    }  
	    
	    String result = "";
	
	    for (String t: tokens) {
		    result=result+t+" ";
	    }
	    
	    if(!result.isEmpty()) {
	    	return result.substring(0, result.length()-1);
	    } else {
	      	return result;
	    }
	}
	
	/**
	 * Normlaizes taa marbouta
	 * @param text
	 * @return
	 */
	public String normalizeTaa(String text) {
		// Removes excessive spaces
		// Letter normalization (ALIF, TAA, AND YAA)
		// Tatweel remover
		
		ArrayList<String> tokens = new ArrayList<String>();
    	StringBuffer modifiedWord = new StringBuffer("");
	    ArrayList<String> modifiedText = tokenizeBySpaceAndRemoveExcessiveSpaces(text);
	    
	    // For each token in the text
	    for (int i=0;i<modifiedText.size();i++) {
	    	 modifiedWord.setLength(0);
	    	 String ctoken=modifiedText.get(i);
	    	 // Normalization of taa  marbutah to haa   
	    	 ctoken=ctoken.replace(Constants.TAA_MARBUTA, Constants.HAA);
	    	 
	    	 for (int j=0; j<ctoken.length(); j++) {
	    		 if ( !(Constants.TATWEEL.contains(ctoken.substring(j,j+1))) )
	                 modifiedWord.append(ctoken.substring(j,j+1));
	             else {
	                 
	             }
	    	}
	    	 tokens.add(modifiedWord.toString());
	    }  
	    
	    String result = "";
	
	    for (String t: tokens) {
		    result=result+t+" ";
	    }
	    
	    if(!result.isEmpty()) {
	    	return result.substring(0, result.length()-1);
	    } else {
	      	return result;
	    }
	}
	
	/**
	 * Normalizes alif maqsora to yaa
	 * @param text
	 * @return
	 */
	public String normalizeAlifMaqsoura(String text) {
		// Removes excessive spaces
		// Letter normalization (ALIF, TAA, AND YAA)
		// Tatweel remover
		
		ArrayList<String> tokens = new ArrayList<String>();
    	StringBuffer modifiedWord = new StringBuffer("");
	    ArrayList<String> modifiedText = tokenizeBySpaceAndRemoveExcessiveSpaces(text);
	    
	    // For each token in the text
	    for (int i=0;i<modifiedText.size();i++) {
	    	 modifiedWord.setLength(0);
	    	 String ctoken=modifiedText.get(i);
	    	 
	    	// Normalization of dotless yaa to yaa 
	    	 ctoken=ctoken.replace(Constants.DOTLESS_YAA, Constants.YAA);

	    	 for (int j=0; j<ctoken.length(); j++) {
	    		 if ( !(Constants.TATWEEL.contains(ctoken.substring(j,j+1))) )
	                 modifiedWord.append(ctoken.substring(j,j+1));
	             else {
	                 
	             }
	    	}
	    	 tokens.add(modifiedWord.toString());
	    }  
	    
	    String result = "";
	
	    for (String t: tokens) {
		    result=result+t+" ";
	    }
	    
	    if(!result.isEmpty()) {
	    	return result.substring(0, result.length()-1);
	    } else {
	      	return result;
	    }
	}
	
	/**
	 * Removes extra spaces and tokenizes the text by white space
	 * @param currentText
	 * @return
	 */
	private ArrayList<String> tokenizeBySpaceAndRemoveExcessiveSpaces(String currentText) {
		ArrayList<String> tt=new ArrayList<String>();
		  StringBuffer word = new StringBuffer ( );
		  currentText=currentText+" ";
		  for ( int i=0;i<currentText.length();i++)
              {
			  
            // If the character is not a space, add it to a word
            if(( ! Character.isWhitespace(currentText.charAt(i))))
                 {
                word.append(currentText.charAt(i));
                 }
            else
               {
                if (word.length() != 0)
                  {
                    tt.add(word.toString());
                    word.setLength ( 0 );
                  }
                }
              }
		return tt;
	  }
	
	public String removeDiacritics(String text) {
		// Diarctic remover
		return Normalizer.normalize(text, Form.NFKD).replaceAll("\\p{M}", "");
	}
	
	public String removePunctuations(String text) {
		ArrayList<String> tokens=new ArrayList<String>();
	 	StringBuffer modifiedWord=new StringBuffer("");
	 	
	 	 Pattern pattern = Pattern.compile("\\p{Punct}");
	 	
		ArrayList<String> modifiedText=tokenizeBySpaceAndRemoveExcessiveSpaces(text);
		for (int i=0;i<modifiedText.size();i++) {
			 modifiedWord.setLength(0);
			 String ctoken=modifiedText.get(i);
			 for (int j=0; j<ctoken.length(); j++)
			{
				 if ( ! (Constants.punctuations.contains(ctoken.substring(j,j+1))) && !pattern.matcher(ctoken.substring(j,j+1)).matches())
		             modifiedWord.append(ctoken.substring(j,j+1));
		         else
		         {
		             
		         }
			}
			 tokens.add(modifiedWord.toString());
		}  // for each token in the text
		
		String result = "";
		for (String t: tokens)
		{
			result=result+t+" ";
			
		}

	    if(!result.isEmpty()) {
	    	return result.substring(0, result.length()-1);
	    } else {
	      	return result;
	    }
    }
	
	/**
	 * Removes non arabic words and numbers and anything not arabic
	 * 
	 * @param text
	 * @return
	 */
	public String removeNonArabicWords(String text) {
		ArrayList<String> tokens=new ArrayList<String>();
	 	
		Pattern pattern = Pattern.compile("\\p{InArabic}+");
	 	
		ArrayList<String> modifiedText = tokenizeBySpaceAndRemoveExcessiveSpaces(text);
		for (int i=0 ; i< modifiedText.size() ; i++) {
			 String ctoken = modifiedText.get(i);
			 
			 // If this token is Arabic then add it
			 if(pattern.matcher(ctoken).matches()) {
				 tokens.add(ctoken);
			 }
		}  // for each token in the text
		
		String result = "";
		for (String t: tokens) {
			result=result + t + " ";
			
		}
		
	    if(!result.isEmpty()) {
	    	return result.substring(0,result.length()-1);
	    } else {
	      	return result;
	    }
    }
	
	/**
	 * Removes arabic numbers
	 * 
	 * @param text
	 * @return
	 */
	public String removeNumbers(String text) {
		ArrayList<String> tokens=new ArrayList<String>();
	 	
		Pattern arabic = Pattern.compile("[٠-٩]+");
		Pattern persoArabic = Pattern.compile("[۰-۹]+");
	 	
		ArrayList<String> modifiedText = tokenizeBySpaceAndRemoveExcessiveSpaces(text);
		for (int i=0 ; i< modifiedText.size() ; i++) {
			 String ctoken = modifiedText.get(i);
			 
			 // If this token is not number, then add it
			 if(!arabic.matcher(ctoken).matches() && !persoArabic.matcher(ctoken).matches()) {
				 tokens.add(ctoken);
			 }
		}  // for each token in the text
		
		String result = "";
		for (String t: tokens) {
			result=result + t + " ";
			
		}
		
	    if(!result.isEmpty()) {
	    	return result.substring(0,result.length()-1);
	    } else {
	      	return result;
	    }
    }
	
	public String removeNonAlphabeticWords(String text) {
		ArrayList<String> tokens=new ArrayList<String>();
	 	StringBuffer modifiedWord=new StringBuffer("");
	 	
		ArrayList<String> modifiedText=tokenizeBySpaceAndRemoveExcessiveSpaces(text);
		for (int i=0;i<modifiedText.size();i++) {
			 modifiedWord.setLength(0);
			 String ctoken=modifiedText.get(i);
			 for (int j=0; j<ctoken.length(); j++) {
				 if ( Character.isAlphabetic(ctoken.charAt(j)))
		             modifiedWord.append(ctoken.charAt(j));
		         else
		         {
		             
		         }
			}
			tokens.add(modifiedWord.toString());
		}  // for each token in the text
		
		String result = "";
		for (String t: tokens)
		{
			result=result+t+" ";
			
		}
		
	    if(!result.isEmpty()) {
	    	return result.substring(0,result.length()-1);
	    } else {
	      	return result;
	    }
    }
	
	/**
	 * Removes the arabic stop words
	 * @param text
	 * @return
	 */
	public String removeStopWords(String text) {
		File file = new File(getClass().getClassLoader().getResource("stopwords.txt").getFile());

		HashSet<String> stopWords = new HashSet<String>();
		List<String> words   = read(file);
		
	    for (String word: words) {
	      // comment?
	      if (!word.startsWith("#"))
	    	  stopWords.add(word);
	    }
	    
	    ArrayList<String> tokens = new ArrayList<String>();
	 	
		ArrayList<String> modifiedText = tokenizeBySpaceAndRemoveExcessiveSpaces(text);
		for (int i=0 ; i< modifiedText.size() ; i++) {
			 String ctoken = modifiedText.get(i);
			 
			 // If this token is not stop words then add it
			 if(!stopWords.contains(ctoken.trim().toLowerCase())) {
				 tokens.add(ctoken);
			 }
		}  // for each token in the text
		
		String result = "";
		for (String t: tokens) {
			result=result + t + " ";
			
		}
		
	    if(!result.isEmpty()) {
	    	return result.substring(0,result.length()-1);
	    } else {
	      	return result;
	    }
	}
	
	private List<String> read(File file) {
	    List<String>	result;
	    String		line;
	    BufferedReader	reader;

	    result = new ArrayList<String>();

	    if (file.exists() && !file.isDirectory()) {
	      reader = null;
	      try {
		reader = new BufferedReader(new FileReader(file));
		while ((line = reader.readLine()) != null)
		  result.add(line.trim());
	      }
	      catch (Exception e) {
		System.err.println("Failed to read stopwords file '" + file + "'!");
		e.printStackTrace();
	      }
	      finally {
		if (reader != null) {
		  try {
		    reader.close();
		  }
		  catch (Exception ex) {
		    // ignored
		  }
		}
	      }
	    }

	    return result;
	}
	
	public String removeSpecificWords(String text, List<String> toRemoveWords) {
		if(toRemoveWords == null || toRemoveWords.size() < 1) {
			return text;
		}
		
		ArrayList<String> tokens=new ArrayList<String>();
		
		ArrayList<String> modifiedText = tokenizeBySpaceAndRemoveExcessiveSpaces(text);
		for (int i=0 ; i< modifiedText.size() ; i++) {
			 String ctoken = modifiedText.get(i);
			 
			 // If this token is not in toRemove list, then add it
			 if(!toRemoveWords.contains(ctoken)) {
				 tokens.add(ctoken);
			 }
		}  // for each token in the text
		
		String result = "";
		for (String t:tokens) {
			result=result + t + " ";
			
		}
		
	    if(!result.isEmpty()) {
	    	return result.substring(0,result.length()-1);
	    } else {
	      	return result;
	    }
	}
	
	public String stemText(String text, Stemmer stemmer) {
		if(stemmer == null) {
			return text;
		}
		
		// Tokenize by space
		// TODO We might use trained tokenizer here!
		ArrayList<String> tokens = tokenizeBySpaceAndRemoveExcessiveSpaces(text);
		
		String newText = "";
		
		for (String token : tokens) {
			 String stem = stemmer.stem(token);
			 newText = newText + stem + " ";
		}
		
	    if(!newText.isEmpty()) {
	    	return newText.substring(0, newText.length()-1);
	    } else {
	      	return newText;
	    }
	}
}
