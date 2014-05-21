import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.*;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.Label;


import java.util.*;

public class Joke {
	
	// the joke
	private String content;
	
	private ArrayList<CoreMap> sentences;
	
	// length of the joke
	private int length;
	
	// 0 - doctor / medical field
	// 1 - lawyer
	// 2 - teacher / educational system
	// ...
	//  TODO: more to be considered
	private int histoProfessionalCommunities[];
	
	// count words with multiple meanings, organized by POS
	private double POSCategoriesWithMultipleMeanings[];
	
	// count words with sexual connotations, organized by POS
	private double POSCategoriesWithSexualConnotations[];
	
	// if we have some questions and answers inside the joke
	private boolean isQA;
	
	// histogram - part of speech
	// noun, verb, adjective, adverb ...
	// TODO: more to be considered
	private double histoPOS[];
	
	// TODO: add histogram (meanings)
	
	private double simplifiedHistoPOS[];
	private double racistWordsPercent;
	private double sexismPercent;
	private double insultingPercent;
	private double quotePercent;
	
	private WordNetWrapper wordNet;
	private RacistSlur racistSlur;
	private Insulting insulting;
	
	public Joke(String content, String insultingWordsFilename, String racistWordsFilename) {
		this.content = content;
		
		this.wordNet = new WordNetWrapper();
		this.racistSlur = new RacistSlur(racistWordsFilename);
		this.insulting = new Insulting(insultingWordsFilename);
		
		computeSentences();
	}
	
	private void computeSentences() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		    	    
		Annotation document = new Annotation(this.content);
		    
		pipeline.annotate(document);
		    
		this.sentences = (ArrayList<CoreMap>) document.get(SentencesAnnotation.class);
	} 
	
	public void extractFeatures() {
		computeLength();
		
		computeQuotePercent();

		partOfSpeechAnalyze();
	}
	
	public void computeQuotePercent() {
		
		int totalLength = this.content.length();
		int quoteLength = 0;
		
		String quotationMarks[] = {"“", "\"", "\'"}; 
		String s = "“";
	
		int index1 = -1, index2 = -1;
		for(int i=0; i<quotationMarks.length && (index1==-1 && index2==-1); i++) {
			index1 = this.content.indexOf(quotationMarks[i]);
			if (index1!=-1)
				index2 = this.content.indexOf(quotationMarks[i], index1+1);
		}
		while (index1!=-1 && index2!=-1) {
	
			String q = this.content.substring(index1+1, index2);
			quoteLength += q.length();
						
			int last = index2;
			index1 = -1; index2 = -1;
			for(int i=0; i<quotationMarks.length && (index1==-1 && index2==-1); i++) {
				index1 = this.content.indexOf(quotationMarks[i], last+1);
				if (index1!=-1)
					index2 = this.content.indexOf(quotationMarks[i], index1+1);
			}
		}

		
		this.quotePercent = 1.0 * quoteLength / totalLength;
	}
	
	public void init() {
		this.histoPOS = new double[Constants.POSConsidered.length];
		for(int i=0; i<this.histoPOS.length; i++)
			histoPOS[i] = 0;
		
		this.simplifiedHistoPOS = new double[Constants.POScategories.length];
		for(int i=0; i<this.simplifiedHistoPOS.length; i++) 
			simplifiedHistoPOS[i] = 0;
		
		this.histoProfessionalCommunities = new int[Constants.professionalCommunities.length];
		for(int i=0; i<this.histoProfessionalCommunities.length; i++)
			histoProfessionalCommunities[i] = 0;
		
		this.POSCategoriesWithMultipleMeanings = new double[Constants.nrPOSCategories];
		this.POSCategoriesWithSexualConnotations = new double[Constants.nrPOSCategories];
		for(int i=0; i<Constants.nrPOSCategories; i++)
			this.POSCategoriesWithMultipleMeanings[i] = this.POSCategoriesWithSexualConnotations[i] = 0;
		
	}
	
	private void partOfSpeechAnalyze() {
		int totalPOS[] = new int[Constants.nrPOSCategories];
	
		int totalPOSMultipleMeanings[] = new int[Constants.nrPOSCategories];
		int totalPOSSexualConnotations[] = new int[Constants.nrPOSCategories];
		int profCom = 0, totalRelevantWords = 0;
		int totalRacistSlurWords = 0, totalSexisms = 0, totalInsulting = 0;;
		boolean isQMark = false, isNonQMark = false;
		
		init();
		
		for(int i=0; i<Constants.nrPOSCategories; i++)
			totalPOS[i] = totalPOSMultipleMeanings[i] = totalPOSSexualConnotations[i] = 0;
		
		for(CoreMap sentence: sentences) {
			String lastWord = "";
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		       
		        String word = token.get(TextAnnotation.class);       
		        String pos = token.get(PartOfSpeechAnnotation.class);
		        String lemma = token.get(LemmaAnnotation.class);
		        
		        
		        
		        int j=0; 
		        while (j<Constants.POSConsidered.length && !pos.equals(Constants.POSConsidered[j]))
		        	j++;
		        if (j<Constants.POSConsidered.length)
		        	this.histoPOS[j]++;	
		        
		        lastWord = word;
		        profCom = -1;
		        
		        int POSCategoryID = Constants.getPOSCategoryID(pos);
		        if (POSCategoryID!=-1) {
		        	totalRelevantWords++;
		        	totalPOS[POSCategoryID]++;
		        	if (this.wordNet.checkIfMultipleMeaningsByPOS(lemma, POSCategoryID))
		        		totalPOSMultipleMeanings[POSCategoryID]++;
		        	if (this.wordNet.checkIfSexualConnotationByPOS(lemma, POSCategoryID))
		        		totalPOSSexualConnotations[POSCategoryID]++;
		        	profCom = this.wordNet.detectProfessionalCommunityByPOS(lemma, POSCategoryID);
		        	
		        	this.simplifiedHistoPOS[POSCategoryID]++;
		        	
		        	if (this.racistSlur.isRacistSlurWord(word) || this.racistSlur.isRacistSlurWord(lemma))
			        	totalRacistSlurWords++;
		        	
		        	if (this.wordNet.checkIfSexism(lemma, POSCategoryID))
		        		totalSexisms++;
		        	
		        	if (this.insulting.isInsultingWord(lemma, this.wordNet.getSynonyms(lemma, POSCategoryID)) || this.insulting.isInsultingWord(word, null))
		        		totalInsulting++;
		        }
		
		        if (profCom!=-1) {
		        	this.histoProfessionalCommunities[profCom]++;
		        
		        }
			}
			
			
			if (lastWord.equals("?"))
				isQMark = true;
			else 
				isNonQMark = true;
			
		
		//	Tree tree = sentence.get(TreeAnnotation.class);
		//	processTree(tree);
		}
		
		this.sexismPercent = (1.0*totalSexisms)/(1.0*totalRelevantWords);
		this.insultingPercent = (1.0*totalInsulting)/(1.0*totalRelevantWords);
		
		this.racistWordsPercent = 1.0*totalRacistSlurWords/totalRelevantWords;
		
		this.isQA = isQMark && isNonQMark;
		
		for(int i=0; i<Constants.nrPOSCategories; i++) {
			if (totalPOS[i]!=0) {
				this.POSCategoriesWithMultipleMeanings[i] = 1.0 * totalPOSMultipleMeanings[i] / totalPOS[i];
				this.POSCategoriesWithSexualConnotations[i] = 1.0 * totalPOSSexualConnotations[i] / totalPOS[i];
			}
			else 
				this.POSCategoriesWithMultipleMeanings[i] = this.POSCategoriesWithSexualConnotations[i] = 0;
		}
		
		if (totalRelevantWords!=0) {
			for(int i=0; i<Constants.POSConsidered.length; i++)
				histoPOS[i] /= totalRelevantWords;
			for(int i=0; i<Constants.POScategories.length; i++)
				simplifiedHistoPOS[i] /= totalRelevantWords;
		}
	}
	
	
	private void computeLength() {
		this.length = this.content.length();
	}
	
	public int getLength() {
		return this.length;
	}
	
	public String getHistoPOS() {
		String str = "";
		for(int i=0; i<this.histoPOS.length; i++) {
			str += (double)Math.round(this.histoPOS[i] * 1000) / 1000+",";
		}
		return str;
	}
	
	public String getHistoProfessionalCommunities() {
		String str = "";
		for(int i=0; i<this.histoProfessionalCommunities.length; i++) {
			str += this.histoProfessionalCommunities[i]+",";
		}
		return str;
	}
	
	public String getIsQA() {
		if (this.isQA)
			return "1";
		else 
			return "0";
	}
	
	public String getPOSWithSexualConnotations(int POSCategoryID) {
		return (double)Math.round(this.POSCategoriesWithSexualConnotations[POSCategoryID] * 1000) / 1000 + "";
	}
	
	public String getPOSWithMultipleMeanings(int POSCategoryID) {
		return (double)Math.round(this.POSCategoriesWithMultipleMeanings[POSCategoryID] * 1000) / 1000 + "";
	}
	
	public String getRacistSlur() {
		return (double)Math.round(this.racistWordsPercent * 1000) / 1000+"";
	}
	
	public String getSexism() {
		return (double)Math.round(this.sexismPercent * 1000) / 1000+"";
	}
	
	public String getSimplifiedHistoPOS() {
		String str = "";
		for(int i=0; i<this.simplifiedHistoPOS.length; i++) {
			str += (double)Math.round(this.simplifiedHistoPOS[i] * 1000) / 1000+",";
		}
		return str;
	}
	
	public String getInsulting() {
		return (double)Math.round(this.insultingPercent * 1000) / 1000+"";
	}
	
	public String getQuotePercent() {
		return (double)Math.round(this.quotePercent * 1000) / 1000+"";
	}
	
}
