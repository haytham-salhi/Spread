package com.spread.experiment.preparation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.WordsFromFile;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

import com.spread.experiment.RawSearchResult;

public class WClusteringPreprocessor {
	
	// These two variables are input to this class
	// Meanings in rawSearchResults and meanings lists should be consistent!!
	private List<RawSearchResult> rawSearchResults;
	private List<String> meanings; // The classes in terms of clustering/classification
	
	// These two variables are modified through this class, and thus they are the output
	private Instances trainingDataset;
	// For evaluation
	private Instances trainingDatasetWithClassAtrr;
	
	
	public WClusteringPreprocessor(List<RawSearchResult> rawSearchResults, List<String> meanings) {
		this.rawSearchResults = rawSearchResults;
		this.meanings = meanings;
	}
	
	/**
	 * This API should be called first to prepare the instances [ARFF]
	 * 
	 * @param featureSelectionModes
	 * @throws Exception
	 */
	public void prepare(
			FeatureSelectionModes featureSelectionModes) throws Exception {
		
		if(rawSearchResults == null) {
			throw new Exception("Raw search results is null");
		}
		
		if(meanings == null) {
			throw new Exception("Meanings is null");
		}
		
		// 1. Declare the attributes (features) with the class atrribute
		// Declare the feature vector
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		// Possible attributes
		Attribute titleAttribute = null;
		Attribute snippetAttribute = null;
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
		default:
			break;
		}
		
		// Declare the class attribute along with its values
		Attribute classAttr = new Attribute("meaning", meanings);
		attributes.add(classAttr);

		//2. Create the instances (traimning data set (ARFF data set))
		trainingDataset = new Instances("SpreadRelation", attributes, rawSearchResults.size()); // Creates an empty set of instances. Uses the given attribute information. Sets the capacity of the set of instances to 0 if its negative. Given attribute information must not be changed after this constructor has been used
		
		for (RawSearchResult rawSearchResult : rawSearchResults) {
			Instance instance = new DenseInstance(attributes.size());
			
			String title = rawSearchResult.getTitle();
			String snippet = rawSearchResult.getSnippet();
			// Add others as needed
			// Here we might do some special processing before adding them to the instance for example to parse HTML or smth
			//innerPage = doSpecialProcess(innerPage);
			
			if(titleAttribute != null) {
				instance.setValue(titleAttribute, title);
			}
			
			if(snippetAttribute != null) {
				instance.setValue(snippetAttribute, snippet);
			}
			
			// Add here other attributes as needed 
			
			// Do these always
			instance.setValue(classAttr, rawSearchResult.getMeaning());
			instance.setDataset(trainingDataset);
			
			// Add the instance
			trainingDataset.add(instance);
		}
	}
	
	/**
	 * This shoud be called after prepare
	 * 
	 * @param stemmer The stemmer to be used. Null means no stemming!
	 * @param countWords If set to true, a count matrix will be used. Otherwise, incidence matrix will be used.
	 * @param wordsToKeep // Per class if there is a class
	 * @param TF
	 * @param IDF
	 * @throws Exception
	 */
	public void preprocessTrainingDataset(Stemmer stemmer,
			boolean countWords,
			int wordsToKeep,
			boolean TF,
			boolean IDF) throws Exception {
		
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
		filter.setStemmer(stemmer);
		filter.setOutputWordCounts(countWords);
		filter.setWordsToKeep(wordsToKeep);
		filter.setTFTransform(TF);
		filter.setIDFTransform(IDF);
		filter.setDoNotOperateOnPerClassBasis(true); // I think it should be like that because we do clustering
		filter.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));
		
		WordsFromFile wordsFromFile = new WordsFromFile();
		File file = new File(getClass().getClassLoader().getResource("stopwords.txt").getFile());
		wordsFromFile.setStopwords(file);
		wordsFromFile.setDebug(true);
		filter.setStopwordsHandler(wordsFromFile);
		
		trainingDataset = Filter.useFilter(trainingDataset, filter);
		// Now it is represented in document vector space
		
		// We can shuffle if needed 
		//trainingdataSetFiltered.randomize(new Random()); // For shuffling the instances :)))))
		
		// 4. 
		// Make a copy for evaluation
		trainingDatasetWithClassAtrr = new Instances(trainingDataset);

		// Let's remove the class attribute as it is no longer needed for clustering
		String[] opts= {"-R", "1"};
		Remove remove = new Remove(); // There are many types of filters!!
		remove.setOptions(opts);
		remove.setInputFormat(trainingDataset);
		// Apply the filter
		trainingDataset = Filter.useFilter(trainingDataset, remove);
	}
	
	// The output after calling prepare, preprocess
	public Instances getTrainingDataSet() {
		return trainingDataset;
	}
	
	// The output after calling prepare, preprocess
	public Instances getTrainingDataSetWithClassAttr() {
		return trainingDatasetWithClassAtrr;
	}
}
