package com.spread.srg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.ar.ArabicNormalizer;
import org.junit.Before;
import org.junit.Test;
import weka.core.stemmers.Stemmer;

import com.spread.experiment.data.stemmers.ArabicStemmerKhoja;
import java.text.Normalizer;
import java.text.Normalizer.Form;
public class NonSpringTest {
	
	
	@Before
	public void setUp() throws Exception {
	}
	
	
	@Test
	public void testName1() throws Exception {
		
		Path path = Paths.get("Results/someFile/ss.txt");
		
		new File("Results/someFile/mam/sas").mkdirs();
		
		// This creates the file each time is executed
//		try (BufferedWriter writer = Files.newWriter(path.toFile(), Charset.forName("utf-8"))) {
//			writer.append("Hello mamss\n");
//			
//			writer.append("Hello mam");
//			FileWriter writer2 = new FileWriter(fileName, append);
//			
//		} catch (Exception e) {
//			System.out.println(e);
//		}
		
		try (BufferedWriter out = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream(path.toFile(), true),"UTF-8"))){
			
			out.write("\n7ayyallah");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
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
		
//		AraNormalizer norm = new AraNormalizer();
		
//		System.out.println(norm.normalize(test2));
		
//		DiacriticsRemover diacriticsRemover = new DiacriticsRemover();
		
//		System.out.println(diacriticsRemover.removeDiacritics(norm.normalize(test1)));
		
	}
	
	@Test
	public void nonArabicWords() throws Exception {
		  Pattern arabic = Pattern.compile("[^\\p{InArabic}]+"); //""[[]+[0-9]*]
		  
		  System.out.println(arabic.matcher("dsd").matches());

	}
		
}
