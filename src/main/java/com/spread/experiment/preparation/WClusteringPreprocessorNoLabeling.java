package com.spread.experiment.preparation;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.stemmers.Stemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import com.spread.experiment.RawSearchResult;
import com.spread.experiment.preparation.specialpreprocessing.InnerPagePreprocessor;
import com.spread.experiment.preparation.specialpreprocessing.TextPreprocesser;
import com.spread.util.nlp.arabic.SpreadArabicPreprocessor;

/**
 * This class is specific for Clustering preprocessing with no labeling
 * 
 * We just removed the meanings and trainingDatasetWithClassAtrr members and their related code.
 * 
 * TODO: This can be enhanced by generalizing the clustering processor 
 * 
 * @author Haytham Salhi
 *
 */
public class WClusteringPreprocessorNoLabeling {
	
	// These two variables are input to this class
	private List<RawSearchResult> rawSearchResults;
	private String ambiguousQuery;
	
	// These two variables are modified through this class, and thus they are the output
	private Instances trainingDataset;
	
	private static final Logger LOGGER = LogManager.getLogger("experimentNoLabaled");
	
	public WClusteringPreprocessorNoLabeling(List<RawSearchResult> rawSearchResults, String ambiguousQuery) {
		this.rawSearchResults = rawSearchResults;
		this.ambiguousQuery = ambiguousQuery;
	}
	
	/**
	 * This API should be called first to prepare the instances [ARFF]
	 * 
	 * @param featureSelectionModes
	 * @throws Exception
	 */
	public void prepare(
			FeatureSelectionModes featureSelectionModes, 
			Stemmer stemmer,
			boolean letterNormalization,
			boolean diacriticsRemoval,
			boolean puncutationRemoval,
			boolean nonArabicWordsRemoval,
			boolean arabicNumbersRemoval,
			boolean nonAlphabeticWordsRemoval,
			boolean stopWordsRemoval) throws Exception {
		
		if(rawSearchResults == null) {
			throw new Exception("Raw search results is null");
		}
		
		// Decleare our own preprocessor
		SpreadArabicPreprocessor spreadArabicPreprocessor = new SpreadArabicPreprocessor();
		
		// 1. Declare the attributes (features) with the class atrribute
		// Declare the feature vector
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		// Possible attributes
		Attribute titleAttribute = null;
		Attribute snippetAttribute = null;
		Attribute innerPageAttribute = null;
		// Add here other attributes as needed
		
		
		switch (featureSelectionModes) {
		case TITLE_ONLY:
			// Declare the text attr
			titleAttribute = new Attribute("title", (List<String>)null); // The way you define a string attribute See constructor documentation
			attributes.add(titleAttribute);
			
			break;
		case SNIPPET_ONLY:
			// Declare the text attr
			snippetAttribute = new Attribute("snippet", (List<String>)null); // The way you define a string attribute See constructor documentation
			attributes.add(snippetAttribute);

			break;
		case TITLE_WITH_SNIPPET:
			// Declare the text attr
			titleAttribute = new Attribute("title", (List<String>)null); // The way you define a string attribute See constructor documentation
			attributes.add(titleAttribute);
			
			// Declare the text attr
			snippetAttribute = new Attribute("snippet", (List<String>)null); // The way you define a string attribute See constructor documentation
			attributes.add(snippetAttribute);

			break;
		case INNER_PAGE:
			// Declare the text attr
			innerPageAttribute = new Attribute("innerPage", (List<String>)null); // The way you define a string attribute See constructor documentation
			attributes.add(innerPageAttribute);
			
			break;
		default:
			break;
		}
		
		//2. Create the instances (traimning data set (ARFF data set))
		trainingDataset = new Instances("SpreadRelation", attributes, rawSearchResults.size()); // Creates an empty set of instances. Uses the given attribute information. Sets the capacity of the set of instances to 0 if its negative. Given attribute information must not be changed after this constructor has been used
		
		for (RawSearchResult rawSearchResult : rawSearchResults) {
			Instance instance = new DenseInstance(attributes.size());
			
			String title = rawSearchResult.getTitle();
			String snippet = rawSearchResult.getSnippet();
			String innerPage = rawSearchResult.getInnerPage();
			// Add others as needed
			// Here we might do some special processing before adding them to the instance for example to parse HTML or smth
			//innerPage = doSpecialProcess(innerPage);
			
			if(titleAttribute != null) {
				if(title == null) {
					title = "";
				}
				
				// Preprocessing, pass it if is not empty
				// ==============
				if(!title.isEmpty()) {
					title = spreadArabicPreprocessor.process(title, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval, 
							nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQuery);
					
					if(title.isEmpty()) {
						LOGGER.warn("The title becomes empty after preprocessing for A.Q: " + ambiguousQuery + ", searchResultId=" + rawSearchResult.getSearchResultId());
					}
				}
				// =============
				
				instance.setValue(titleAttribute, title);
			}
			
			if(snippetAttribute != null) {
				if(snippet == null) {
					snippet = "";
				}
				
				// Preprocessing, pass it if is not empty
				// ==============
				if(!snippet.isEmpty()) {
					snippet = spreadArabicPreprocessor.process(snippet, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval, 
							nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQuery);
					
					if(snippet.isEmpty()) {
						LOGGER.warn("The snippet becomes empty after preprocessing for A.Q: " + ambiguousQuery + ", searchResultId=" + rawSearchResult.getSearchResultId());
					}
				}
				// =============
				
				instance.setValue(snippetAttribute, snippet);
			}
			
			if(innerPageAttribute != null) {
				if(innerPage == null) {
					innerPage = "";
				}
				
				// Here you might need to do some preprocessing before adding it to ARFF as an instance. For example using JSOUP
				// Preprocessing
				//1.
				TextPreprocesser innerPagePreprocessor = new InnerPagePreprocessor();
				innerPage = innerPagePreprocessor.process(innerPage);
				//2. Preprocessing, pass it if is not empty
				// ==============
				if(!innerPage.isEmpty()) {
					innerPage = spreadArabicPreprocessor.process(innerPage, stemmer, letterNormalization, diacriticsRemoval, puncutationRemoval, nonArabicWordsRemoval, arabicNumbersRemoval, 
							nonAlphabeticWordsRemoval, stopWordsRemoval, ambiguousQuery);
					
					if(innerPage.isEmpty()) {
						LOGGER.warn("The innerPage becomes empty after preprocessing for A.Q: " + ambiguousQuery + ", searchResultId=" + rawSearchResult.getSearchResultId());
					}
				}
				// =============
				
				instance.setValue(innerPageAttribute, innerPage);
			}
			
			// Add here other attributes as needed 
			
			instance.setDataset(trainingDataset);
			
			// Add the instance
			trainingDataset.add(instance);
		}
	}
	
	/**
	 * This shoud be called after prepare
	 * 
	 * @param countWords If set to true, a count matrix will be used. Otherwise, incidence matrix will be used.
	 * @param wordsToKeep // Per class if there is a class
	 * @param TF
	 * @param IDF
	 * @param nGramMaxSize the max of n
	 * @param nGramMinSize the min of n
	 * @throws Exception
	 */
	public void buildVectorSpaceDataset(
			boolean countWords,
			int wordsToKeep,
			int wordsToKeepInCaseOfInnerPage,
			boolean TF,
			boolean IDF,
			int nGramMinSize,
			int nGramMaxSize,
			int minTermFreqToKeep) throws Exception {
		
		if(trainingDataset == null) {
			throw new Exception("Training data set is null. Make sure that you call prepare before this!");
		}
//		filter.setInputFormat(trainingSet);
//		filter.setStemmer(new TestStemmer());
//		filter.setOutputWordCounts(false); // to make it count matrix instead set it to true. False indicates to incidence matrix ;)
//		filter.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));
		//filter.setTFTransform(false);
		//filter.setIDFTransform(true);
		//filter.setAttributeIndices("first-3"); Default is all string attributes will bo converted to words vectors
		//filter.setMinTermFreq(newMinTermFreq); // Default is 1, the idea is to prune the dictionary of low frequency terms
		//filter.setWordsToKeep(newWordsToKeep);
		//filter.setDoNotOperateOnPerClassBasis(false); Default false If this is set, the maximum number of words and the minimum term frequency is not enforced on a per-class basis but based on the documents in all the classes (even if a class attribute is set).
		//filter.setInvertSelection(invert); Inout selection mode, default is false
		//filter.setLowerCaseTokens(false); no care for arabic
		//filter.setTokenizer(value); Tokenizer algorithm to use
		//filter.setStopwordsHandler();
		
		// 3. I want to do preprocessing (StringToVector Filter ;))
		StringToWordVector filter = new StringToWordVector(); // It converts it to SparseData Repesentation :)))
		
		filter.setInputFormat(trainingDataset);
		filter.setStemmer(null);
		filter.setOutputWordCounts(countWords);
		
		if(trainingDataset.attribute("innerPage") != null) {
			filter.setWordsToKeep(wordsToKeepInCaseOfInnerPage);
		} else {
			filter.setWordsToKeep(wordsToKeep);
		}
		
		filter.setTFTransform(TF);
		filter.setIDFTransform(IDF);
		filter.setDoNotOperateOnPerClassBasis(true); // I think it should be like that because we do clustering
		filter.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));
		filter.setLowerCaseTokens(true);
 		//filter.setTokenizer(value); // default is worktokenizaer (we can use ngrams here)
		NGramTokenizer nGramTokenizer = new NGramTokenizer();
		nGramTokenizer.setNGramMinSize(nGramMinSize);
		nGramTokenizer.setNGramMaxSize(nGramMaxSize);
		filter.setTokenizer(nGramTokenizer);
		
		filter.setMinTermFreq(minTermFreqToKeep); // Default is 1, the idea is to prune the dictionary of low frequency terms

		
		trainingDataset = Filter.useFilter(trainingDataset, filter);
		// Now it is represented in document vector space
		
		// We can shuffle if needed 
		//trainingdataSetFiltered.randomize(new Random()); // For shuffling the instances :)))))
		
	}
	
	// The output after calling prepare, buildVectorSpaceDataset
	public Instances getTrainingDataSet() {
		return trainingDataset;
	}
}
