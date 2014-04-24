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

import java.util.*;

public class Joke {
	
	// the joke
	private String content;
	
	private ArrayList<CoreMap> sentences;
	
	// length of the joke
	private int length;

	
	// details related to the subject of the joke
	
	// if the subject is a person or not
	//private boolean isPerson;
	
	// 0 - male
	// 1 - female
	//private int sex;
	
	// 0 - white
	// 1 - black
	// 2 - yellow
	// ...
	// TODO: more to be considered
//	private int race;
	
	// TODO: add nationality
	
	// place the subject in time 
	// expected to be a year
//	private int timeStart;
//	private int timeEnd;
	
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
	
	// count words with sexual connotations, organized by POS
	// private double POSCategoriesWithMultiplePronunciations[];

	// if there is a dialogue in the joke 
//	private boolean isDialogue;
	
	// if we have some questions and answers inside the joke
	private boolean isQA;
	
	// histogram - part of speech
	// noun, verb, adjective, adverb ...
	// TODO: more to be considered
	private double histoPOS[];
	
	// TODO: add histogram (meanings)
	
	private WordNetWrapper wordNet;
	
	public Joke(String content) {
		this.content = content;
		
		this.wordNet = new WordNetWrapper();
		
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

		partOfSpeechAnalyze();
	}
	
	public void init() {
		this.histoPOS = new double[Constants.POSConsidered.length];
		for(int i=0; i<this.histoPOS.length; i++)
			histoPOS[i] = 0;
		
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
		boolean isQMark = false, isNonQMark = false;
		
		init();
		
		for(int i=0; i<Constants.nrPOSCategories; i++)
			totalPOS[i] = totalPOSMultipleMeanings[i] = totalPOSSexualConnotations[i] = 0;
		
		for(CoreMap sentence: sentences) {
			String lastWord = "";
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		       
		        String word = token.get(TextAnnotation.class);
		       
		        String pos = token.get(PartOfSpeechAnnotation.class);
		       
		        String ne = token.get(NamedEntityTagAnnotation.class);
		        
		        String lemma = token.get(LemmaAnnotation.class);
		        
		        int j=0; 
		        while (j<Constants.POSConsidered.length && !pos.equals(Constants.POSConsidered[j]))
		        	j++;
		        if (j<Constants.POSConsidered.length)
		        	this.histoPOS[j]++;	
		        
		       // System.out.println(word+" | "+pos + " | "+ne+" | "+lemma);
		        
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
		        }
		
		        if (profCom!=-1) {
		        	this.histoProfessionalCommunities[profCom]++;
		        	System.out.println(profCom+" "+lemma +" "+word);
		        }
			}
			
			
			if (lastWord.equals("?"))
				isQMark = true;
			else 
				isNonQMark = true;
			
		
		//	Tree tree = sentence.get(TreeAnnotation.class);
		//	processTree(tree);
		}
		
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
	
}
