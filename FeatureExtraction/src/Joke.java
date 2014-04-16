import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
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
	
	// nouns related
	private double nounsWithMultipleMeanings;
//	private double nounsWithMultiplePronunciations;
	private double nounsWithSexualConnotations;
	
	// verbs related
	private double verbsWithMultipleMeanings;
//	private double verbsWithMultiplePronunciations;
	private double verbsWithSexualConnotations;
	
	// adjectives related
	private double adjectivesWithMultipleMeanings;
//	private double adjectivesWithMultiplePronunciations;
	private double adjectivesWithSexualConnotations;
	
	// adverbs related
	private double adverbsWithMultipleMeanings;
//	private double adverbsWithMultiplePronunciations;
	private double adverbsWithSexualConnotations;
	
	// if there is a dialogue in the joke 
//	private boolean isDialogue;
	
	// if we have some questions and answers inside the joke
	private boolean isQA;
	
	// histogram - part of speech
	// noun, verb, adjective, adverb ...
	// TODO: more to be considered
	private int histoPOS[];
	
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
	
	private void partOfSpeechAnalyze() {
		int totalNouns = 0, totalNounsWithSexualConnotations = 0, totalNounsWithMultipleMeanings = 0;
		int totalVerbs = 0, totalVerbsWithSexualConnotations = 0, totalVerbsWithMultipleMeanings = 0;
		int totalAdverbs = 0, totalAdverbsWithSexualConnotations = 0, totalAdverbsWithMultipleMeanings = 0;
		int totalAdjectives = 0, totalAdjectivesWithSexualConnotations = 0, totalAdjectivesWithMultipleMeanings = 0;
		int profCom;
		
		this.histoPOS = new int[Constants.POSConsidered.length];
		for(int i=0; i<this.histoPOS.length; i++)
			histoPOS[i] = 0;
		
		this.histoProfessionalCommunities = new int[Constants.professionalCommunities.length];
		for(int i=0; i<this.histoProfessionalCommunities.length; i++)
			histoProfessionalCommunities[i] = 0;
		
		boolean isQMark = false, isNonQMark = false;
		
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
		        if (Constants.isNounPOS(pos)) {
		        	profCom = this.wordNet.detectProfessionalCommunityNoun(lemma);
		        	totalNouns++;
		        	if (this.wordNet.checkIfSexualConnotationNoun(lemma))
		        		totalNounsWithSexualConnotations++;
		        	if (this.wordNet.checkIfMultipleMeaningsNoun(lemma))
		        		totalNounsWithMultipleMeanings++;
		        }
		        else if (Constants.isVerbPOS(pos)) {
		        	profCom = this.wordNet.detectProfessionalCommunityVerb(lemma);
		        	totalVerbs++;
		        	if (this.wordNet.checkIfSexualConnotationVerb(lemma))
		        		totalVerbsWithSexualConnotations++;
		        	if (this.wordNet.checkIfMultipleMeaningsVerb(lemma))
		        		totalVerbsWithMultipleMeanings++;
		        }
		        else if (Constants.isAdjectivePOS(pos)) {
		        	profCom = this.wordNet.detectProfessionalCommunityAdjective(lemma);
		        	totalAdjectives++;
		        	if (this.wordNet.checkIfSexualConnotationAdjective(lemma))
		        		totalAdjectivesWithSexualConnotations++;
		        	if (this.wordNet.checkIfMultipleMeaningsAdjective(lemma))
		        		totalAdjectivesWithMultipleMeanings++;
		        }
		        else if (Constants.isAdverbPOS(pos)) {
		        	profCom = this.wordNet.detectProfessionalCommunityAdverb(lemma);
		        	totalAdverbs++;
		        	if (this.wordNet.checkIfSexualConnotationAdverb(lemma))
		        		totalAdverbsWithSexualConnotations++;
		        	if (this.wordNet.checkIfMultipleMeaningsAdverb(lemma))
		        		totalAdverbsWithMultipleMeanings++;
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
			
			
			
		}
		
		this.isQA = isQMark && isNonQMark;
		
		if (totalNouns!=0) {
			this.nounsWithSexualConnotations = 1.0 * totalNounsWithSexualConnotations / totalNouns;
			this.nounsWithMultipleMeanings = 1.0 * totalNounsWithMultipleMeanings / totalNouns;
		}
		else 
			this.nounsWithSexualConnotations = this.nounsWithMultipleMeanings = 0;
		
		if (totalVerbs!=0) {
			this.verbsWithSexualConnotations = 1.0 * totalVerbsWithSexualConnotations / totalVerbs;
			this.verbsWithMultipleMeanings = 1.0 * totalVerbsWithMultipleMeanings / totalVerbs;
		}
		else 
			this.verbsWithMultipleMeanings = this.verbsWithSexualConnotations = 0;
		
		if (totalAdjectives!=0) {
			this.adjectivesWithSexualConnotations = 1.0 * totalAdjectivesWithSexualConnotations / totalAdjectives;
			this.adjectivesWithMultipleMeanings = 1.0 * totalAdjectivesWithMultipleMeanings / totalAdjectives;
		}
		else 
			this.adjectivesWithMultipleMeanings = this.adjectivesWithSexualConnotations = 0;
		
		if (totalAdverbs!=0) {
			this.adverbsWithSexualConnotations = 1.0 * totalAdverbsWithSexualConnotations / totalAdverbs;
			this.adverbsWithMultipleMeanings = 1.0 * totalAdverbsWithMultipleMeanings / totalAdverbs;
		}
		else 
			this.adverbsWithMultipleMeanings = this.adverbsWithSexualConnotations = 0;
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
			str += this.histoPOS[i]+",";
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
	
	public String getNounsWithSexualConnotations() {
		return this.nounsWithSexualConnotations+"";
	}
	
	public String getVerbsWithSexualConnotations() {
		return this.verbsWithSexualConnotations+"";
	}
	
	public String getAdverbsWithSexualConnotations() {
		return this.adverbsWithSexualConnotations+"";
	}
	
	public String getAdjectivesWithSexualConnotations() {
		return this.adjectivesWithSexualConnotations+"";
	}
	
	public String getNounsWithMultipleMeanings() {
		return this.nounsWithMultipleMeanings+"";
	}
	
	public String getVerbsWithMultipleMeanings() {
		return this.verbsWithMultipleMeanings+"";
	}
	
	public String getAdverbsWithMultipleMeanings() {
		return this.adverbsWithMultipleMeanings+"";
	}
	
	public String getAdjectivesWithMultipleMeanings() {
		return this.adjectivesWithMultipleMeanings+"";
	}
}
