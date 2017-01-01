package com.spread.util;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.ar.ArabicNormalizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import weka.core.stemmers.Stemmer;

import com.google.common.base.Optional;
import com.spread.config.RootConfig;
import com.spread.experiment.data.stemmers.ArabicStemmerKhoja;
import com.spread.persistence.rds.model.Meaning;
import com.spread.persistence.rds.model.Query;
import com.spread.persistence.rds.model.SearchEngine;
import com.spread.persistence.rds.model.SearchResult;
import com.spread.persistence.rds.model.enums.Language;
import com.spread.persistence.rds.model.enums.Location;
import com.spread.persistence.rds.model.enums.SearchEngineCode;
import com.spread.persistence.rds.model.enums.SearchEngineLanguage;
import com.spread.persistence.rds.repository.MeaningRepository;
import com.spread.persistence.rds.repository.QueryRepository;
import com.spread.persistence.rds.repository.SearchEngineRepository;
import com.spread.util.nlp.arabic.SpreadArabicPreprocessor;
import com.spread.util.nlp.arabic.stemmers.LightStemmer10;
import com.spread.util.nlp.arabic.thirdparty.maha.AraNormalizer;
import com.spread.util.nlp.arabic.thirdparty.maha.DiacriticsRemover;

import java.text.Normalizer;
import java.text.Normalizer.Form;
public class UtilTest {
	
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testname()  {
		  Pattern p = Pattern.compile("\\p{Punct}");

	        Matcher m = p.matcher("One day! # when I was walking. I found your pants? just kidding...");
	        int count = 0;
	        while (m.find()) {
	            count++;
	            System.out.println("\nMatch number: " + count);
	            System.out.println("start() : " + m.start());
	            System.out.println("end()   : " + m.end());
	            System.out.println("group() : " + m.group());
	        }
	}
	
	@Test
	public void arabicTest()  {
		  Pattern p = Pattern.compile("[\\p{Punct}،؛,]");

	        Matcher m = p.matcher("   حيك يا حبيبي , ولكن  م؛ ماذا تقول~السلام عليكم !وحياكم الله ، واستودعكم الله");
	        int count = 0;
	        while (m.find()) {
	            count++;
	            System.out.println("\nMatch number: " + count);
	            System.out.println("start() : " + m.start());
	            System.out.println("end()   : " + m.end());
	            System.out.println("group() : " + m.group());
	        }
	}
	
	@Test
	public void testSplit() throws Exception {
		String[] values = "Incorrectly clustered instances :	11.0	 68.75   %".split("[ \r\n\t]");
		
		for (String string : values) {
			System.out.println(string + " >>" + string.length());
			
		}
		
		System.out.println(values.length);
	}
	
	@Test
	public void testGrouping() throws Exception {
		double[] assignments = {1.0, 2.0, 1.0, 3.0, 3.0, 3.0};
		
		Map<Double, Long> counts = Arrays.stream(assignments).boxed().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		System.out.println(counts);
	}
	
	@Test
	public void testLanguageDtector() throws Exception {
		//load all languages:
//		List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
//		
//		//build language detector:
//		LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard()).withProfiles(languageProfiles).build();
//		
//		//create a text object factory
//		TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText();
//		
//		TextObject textObject = textObjectFactory.forText("my");
//		Optional<LdLocale> lang = languageDetector.detect(textObject);
//		
//		System.out.println(lang);
		
		
	}
	
	@Test
	public void englishWords() throws Exception {
		  Pattern englishWords = Pattern.compile("^[a-zA-Z_\\-]+\\d*$"); //""[[]+[0-9]*]
		  
		  Pattern numbers = Pattern.compile("\\d+");
		  
		  Pattern dates = Pattern.compile("^\\d+[/\\-]\\d+[/\\-]\\d+$");
		  
		  
		  
		  System.out.println(englishWords.matcher("05/2/2016").matches());
		  System.out.println(numbers.matcher("05-2-2016").matches());
		  
		  System.out.println(dates.matcher("05-2-2016").matches());

	}
	
	@Test
	public void testReplacing() throws Exception {
		String s = "سلطنة عمان واحدة من الدول العربية وعمان تعني ";
		
		;
		System.out.println(s.replace("عمان", ""));
	}
	
	@Test
	public void testRemovingAmbiguityWord() throws Exception {
		String s = "سلطنة عمان واحدة من الدول العربية وعمان تعني ";
		
		System.out.println(s.replaceAll("\\sعمان\\s", " "));
	}
	
	@Test
	public void workingWithTashkeel() throws Exception {
		//String test = "كَلَّا لَا تُطِعْهُ وَاسْجُدْ وَاقْتَرِبْ ۩";
		
		String test = "الله أكبر ولله الحمد يا الله يا كريـــــــم";
		System.out.println(test);
		
		System.out.println(Normalizer.normalize(test, Form.NFKD).replaceAll("\\p{M}", ""));
		System.out.println(test.replaceAll("\\p{M}", ""));
	}
	
	@Test
	public void workingWithTashkeelLucene() throws Exception {
		String test1 = "كَلَّا لَا تُطِعْهُ وَاسْجُدْ وَاقْتَرِبْ ۩";
		String test = "الله أكبر ولله الحمد يا الله يا كريـــــــم du";
		
		String test2 = "منى انا حبيبي";


		ArabicNormalizer normalizer = new ArabicNormalizer();
		
		char[] testArr = test.toCharArray();
		
		System.out.println(String.valueOf(testArr));
		
		normalizer.normalize(testArr, test.length());
		
		System.out.println(String.valueOf(testArr));
		
		
		Stemmer stemmer = new ArabicStemmerKhoja();
		
		System.out.println(stemmer.stem("وَاقْتَرِبْ يا شجاع"));
		
		AraNormalizer norm = new AraNormalizer();
		
		System.out.println(norm.normalize(test2));
		
		DiacriticsRemover diacriticsRemover = new DiacriticsRemover();
		
		System.out.println(diacriticsRemover.removeDiacritics(norm.normalize(test1)));
		
	}
	
	@Test
	public void nonArabicWords() throws Exception {
		  Pattern arabic = Pattern.compile("\\p{InArabic}+"); //""[[]+[0-9]*]
		  
		  System.out.println(arabic.matcher("۵").matches());

	}
	
	@Test
	public void numbersTest() throws Exception {
		  Pattern arabic = Pattern.compile("[٠-٩]+"); //""[[]+[0-9]*]
		  
		  System.out.println(arabic.matcher("۹").matches());

	}
	
	
	@Test
	public void fullPreproccessingPipelineTest() throws Exception {
		SpreadArabicPreprocessor preprocessor = new SpreadArabicPreprocessor();
		
		//String test = "كَلَّا لَا تُطِعْهُ وَاسْجُدْ وَاقْتَرِبْ  شركة أبو أحمد للتوزيت وآلتجارة۩";
		
		String test = "كَلَّا لَا تُطِعْهُ وَاسْجُدْ وَاقْتَرِبْ ۩ haytham is the best ; / () ۵ ، 23 324 434 2012/12/44 ١";
		
		
		System.out.println("Initial:");
		System.out.println(test);
		System.out.println("==================");

		test = preprocessor.normalize(test);
		
		System.out.println("After removing tatweel, working on alef wal yaa wal haa:");
		System.out.println(test);
		System.out.println("==================");
		
		test = preprocessor.removeDiacritics(test);
		
		System.out.println("After removing diacs:");
		System.out.println(test);
		System.out.println("==================");
		
		test = preprocessor.removePunctuations(test);
		
		System.out.println("After removing puncuations:");
		System.out.println(test);
		System.out.println("==================");
		
		test = preprocessor.removeNonArabicWords(test);
		
		System.out.println("After removing non arabic words:");
		System.out.println(test);
		System.out.println("==================");
		
		test = preprocessor.removeNumbers(test);
		
		System.out.println("After removing numbers:");
		System.out.println(test);
		System.out.println("==================");
		
		test = preprocessor.removeNonAlphabeticWords(test);
		
		System.out.println("After removing AlphabeticWords:");
		System.out.println(test);
		System.out.println("==================");
		
		test = preprocessor.removeStopWords(test);
		
		System.out.println("After removing stopWords:");
		System.out.println(test);
		System.out.println("==================");
		
		ArrayList<String> wordsToRemove = new ArrayList<String>();
		wordsToRemove.add("واسجد");
		wordsToRemove.add("تطعه");
		
		test = preprocessor.removeSpecificWords(test, wordsToRemove);
		
		System.out.println("After removing specific words:");
		System.out.println(test);
		System.out.println("==================");

		test = preprocessor.stemText(test, new ArabicStemmerKhoja());
		
		System.out.println("After stemming:");
		System.out.println(test);
		System.out.println("==================");
		
		
	}
	
	@Test
	public void testName() throws Exception {
		String test = "كَلَّا لَا تُطِعْهُ وَاسْجُدْ وَاقْتَرِبْ ۩ haytham is the best ; / () ، 23 324 434 2012/12/44";

		for (int i = 0; i < test.length(); i++) {
			System.out.println(test.charAt(i) + " --> " + Character.isLetter(test.charAt(i)) + " ---> " + Character.isAlphabetic(test.charAt(i)));
			
		}
	}
	
	@Test
	public void dummy() throws Exception {
		String s = "";
		
		System.out.println(s.charAt(0));
	}
	
	@Test
	public void normTest() throws Exception {
		SpreadArabicPreprocessor preprocessor = new SpreadArabicPreprocessor();
		
		String text = "الله أكبر ولله الحمد، القدس في فلسطين آنيي إحمد منة ومنى وعبسية وحميديه";
		
		text = preprocessor.normalizeAlif(text);
		text = preprocessor.normalizeTaa(text);
		text = preprocessor.normalizeAlifMaqsoura(text);
		
		System.out.println(text);
	}
	
	@Test
	public void stemmerTest() throws Exception {

		//LightStemmer10 lightStemmer = new LightStemmer10();
		
		String token = "البلد";
		
		//System.out.println(lightStemmer.findStem(token));
		
		ArabicStemmerKhoja stemmer = new ArabicStemmerKhoja();
		System.out.println(stemmer.stem(token));
	}
	
	
		
}
