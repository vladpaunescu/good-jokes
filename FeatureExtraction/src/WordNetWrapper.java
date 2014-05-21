import java.util.ArrayList;

import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;


public class WordNetWrapper {
	
	WordNetDatabase database;
	
	private Sexism sexism;
	
	public WordNetWrapper() {
		this.database = WordNetDatabase.getFileInstance();
		this.sexism = new Sexism();
	}
	
	
	private ArrayList<String> getSynonymsNoun(String word) {
		ArrayList<String> synonyms = new ArrayList<String>();
		NounSynset nounSynset ;
		Synset[] synsets = database.getSynsets(word, SynsetType.NOUN);
		for(int i=0; i<synsets.length; i++) {
			nounSynset = (NounSynset)synsets[i];
			String[] wordForms = nounSynset.getWordForms();
			for(int j=0; j<wordForms.length; j++)
				synonyms.add(wordForms[j]);
		}
		return synonyms;
	}
	
	private ArrayList<String> getSynonymsVerb(String word) {
		ArrayList<String> synonyms = new ArrayList<String>();
		VerbSynset verbSynset ;
		Synset[] synsets = database.getSynsets(word, SynsetType.VERB);
		for(int i=0; i<synsets.length; i++) {
			verbSynset = (VerbSynset)synsets[i];
			String[] wordForms = verbSynset.getWordForms();
			for(int j=0; j<wordForms.length; j++)
				synonyms.add(wordForms[j]);
		}
		return synonyms;
	}
	
	private ArrayList<String> getSynonymsAdverb(String word) {
		ArrayList<String> synonyms = new ArrayList<String>();
		AdverbSynset adverbSynset ;
		Synset[] synsets = database.getSynsets(word, SynsetType.ADVERB);
		for(int i=0; i<synsets.length; i++) {
			adverbSynset = (AdverbSynset)synsets[i];
			String[] wordForms = adverbSynset.getWordForms();
			for(int j=0; j<wordForms.length; j++)
				synonyms.add(wordForms[j]);
		}
		return synonyms;
	}
	
	private ArrayList<String> getSynonymsAdjective(String word) {
		ArrayList<String> synonyms = new ArrayList<String>();
		AdjectiveSynset adjectiveSynset ;
		Synset[] synsets = database.getSynsets(word, SynsetType.ADJECTIVE);
		for(int i=0; i<synsets.length; i++) {
			adjectiveSynset = (AdjectiveSynset)synsets[i];
			String[] wordForms = adjectiveSynset.getWordForms();
			for(int j=0; j<wordForms.length; j++)
				synonyms.add(wordForms[j]);
		}
		return synonyms;
	}
	
	public ArrayList<String> getSynonyms(String word, int POSCategoryID) {
		switch (POSCategoryID) {
		case Constants.nounCategoryID: 
			return this.getSynonymsNoun(word);
		case Constants.verbCategoryID: 
			return this.getSynonymsVerb(word);
		case Constants.adverbCategoryID: 
			return this.getSynonymsAdverb(word);
		case Constants.adjectiveCategoryID: 
			return this.getSynonymsAdjective(word);
		default: 
			return null;
	}
	}
	
	private ArrayList<String> getDefinitionsForWordNoun(String word) {
		ArrayList<String> result = new ArrayList<String>();
		NounSynset nounSynset ;
		Synset[] hyponyms;
		Synset[] hypernyms;
	
		
		Synset[] synsets = database.getSynsets(word, SynsetType.NOUN);
		
		//database.getSynonyms(word);
		for(int i=0; i<synsets.length; i++) {
			nounSynset = (NounSynset)synsets[i];
			result.add(nounSynset.getDefinition());
			if (Constants.useSemanticTree) {
				hyponyms = nounSynset.getHyponyms();
				hypernyms = nounSynset.getHypernyms();
				for(int j=0; j<hyponyms.length; j++)
					result.add(hyponyms[j].getDefinition());
				for(int j=0; j<hypernyms.length; j++)
					result.add(hypernyms[j].getDefinition());
			}	
		}
		return result;
	}
	
	private ArrayList<String> getDefinitionsForWordVerb(String word) {
		ArrayList<String> result = new ArrayList<String>();
		VerbSynset verbSynset ;
		Synset[] troponyms;
		Synset[] hypernyms;
		
		Synset[] synsets = database.getSynsets(word, SynsetType.VERB);
		for(int i=0; i<synsets.length; i++) {
			verbSynset = (VerbSynset)synsets[i];
			result.add(verbSynset.getDefinition());
			if (Constants.useSemanticTree) {
				hypernyms = verbSynset.getHypernyms();
				troponyms = verbSynset.getTroponyms();
				for(int j=0; j<troponyms.length; j++)
					result.add(troponyms[j].getDefinition());
				for(int j=0; j<hypernyms.length; j++)
					result.add(hypernyms[j].getDefinition());
			}	
		}
		return result;
	}

	private ArrayList<String> getDefinitionsForWordAdverb(String word) {
		ArrayList<String> result = new ArrayList<String>();
		AdverbSynset adverbSynset;
		
		Synset[] synsets = database.getSynsets(word, SynsetType.ADVERB);
		for(int i=0; i<synsets.length; i++) {
			adverbSynset = (AdverbSynset)synsets[i];
			result.add(adverbSynset.getDefinition());
		}
		
		return result;
	}
	
	private ArrayList<String> getDefinitionsForWordAdjective(String word) {
		ArrayList<String> result = new ArrayList<String>();
		AdjectiveSynset adjectiveSynset;
		
		Synset[] synsets = database.getSynsets(word, SynsetType.ADJECTIVE);
		for(int i=0; i<synsets.length; i++) {
			adjectiveSynset = (AdjectiveSynset)synsets[i];
			result.add(adjectiveSynset.getDefinition());
		}
		
		return result;
	}

	private boolean checkIfSexualConnotation(String definition) {
		for(int j=0; j<Constants.sexualConnotationWords.length; j++) {
			if (definition.contains(Constants.sexualConnotationWords[j]))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexualConnotationNoun(String word) {
		ArrayList<String> definitions = getDefinitionsForWordNoun(word);
		for(int i=0; i<definitions.size(); i++) {
			if (checkIfSexualConnotation(definitions.get(i)))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexualConnotationVerb(String word) {
		ArrayList<String> definitions = getDefinitionsForWordVerb(word);
		for(int i=0; i<definitions.size(); i++) {
			if (checkIfSexualConnotation(definitions.get(i)))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexualConnotationAdverb(String word) {
		ArrayList<String> definitions = getDefinitionsForWordAdverb(word);
		for(int i=0; i<definitions.size(); i++) {
			if (checkIfSexualConnotation(definitions.get(i)))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexualConnotationAdjective(String word) {
		ArrayList<String> definitions = getDefinitionsForWordAdjective(word);
		for(int i=0; i<definitions.size(); i++) {
			if (checkIfSexualConnotation(definitions.get(i)))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexismNoun(String word) {
		ArrayList<String> definitions = getDefinitionsForWordNoun(word);
		
		//ArrayList<String> synonymDefinitions = getSynonymDefinitionsNoun(word);
		
		for(int i=0; i<definitions.size(); i++) {
			
			if (sexism.isSexism(definitions.get(i), null))
				return true;
			
		}
		return false;
	}
	
	public boolean checkIfSexismVerb(String word) {
		ArrayList<String> definitions = getDefinitionsForWordNoun(word);
		for(int i=0; i<definitions.size(); i++) {
			if (sexism.isSexism(definitions.get(i), null))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexismAdjective(String word) {
		ArrayList<String> definitions = getDefinitionsForWordAdjective(word);
		for(int i=0; i<definitions.size(); i++) {
			if (sexism.isSexism(definitions.get(i), null))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexismAdverb(String word) {
		ArrayList<String> definitions = getDefinitionsForWordAdverb(word);
		for(int i=0; i<definitions.size(); i++) {
			if (sexism.isSexism(definitions.get(i), null))
				return true;
		}
		return false;
	}
		
	public boolean checkIfMultipleMeaningsNoun(String word) {
		Synset[] synsets = this.database.getSynsets(word, SynsetType.NOUN);
		if (synsets.length>1)
			return true;
		return false;
	}
	
	public boolean checkIfMultipleMeaningsVerb(String word) {
		Synset[] synsets = this.database.getSynsets(word, SynsetType.VERB);
		if (synsets.length>1)
			return true;
		return false;
	}
	
	public boolean checkIfMultipleMeaningsAdverb(String word) {
		Synset[] synsets = this.database.getSynsets(word, SynsetType.ADVERB);
		if (synsets.length>1)
			return true;
		return false;
	}
	
	public boolean checkIfMultipleMeaningsAdjective(String word) {
		Synset[] synsets = this.database.getSynsets(word, SynsetType.ADJECTIVE);
		if (synsets.length>1)
			return true;
		return false;
	}

	private int detectProfessionalCommunity(String definition) {
		for(int i=0; i<Constants.professionalCommunities.length; i++) {
			for(int j=0; j<Constants.professionalCommunities[i].length; j++) {
				if (definition.contains(Constants.professionalCommunities[i][j])) {
				
					return i;
				}
			}
		}
		return -1;
	}
	
	public int detectProfessionalCommunityNoun(String word) {
		int r;
		r = detectProfessionalCommunity(word);
		if (r!=-1)
			return r;
		
		ArrayList<String> definitions = getDefinitionsForWordNoun(word);
		for(int i=0; i<definitions.size(); i++) {
			r = detectProfessionalCommunity(definitions.get(i));
			if (r!=-1)
				return r;
		}
		
		return -1;
	}
	
	public int detectProfessionalCommunityVerb(String word) {
		int r;
		r = detectProfessionalCommunity(word);
		if (r!=-1)
			return r;
		
		ArrayList<String> definitions = getDefinitionsForWordVerb(word);
		for(int i=0; i<definitions.size(); i++) {
			r = detectProfessionalCommunity(definitions.get(i));
			if (r!=-1)
				return r;
		}
		
		return -1;
	}
	
	public int detectProfessionalCommunityAdverb(String word) {
		int r;
		r = detectProfessionalCommunity(word);
		if (r!=-1)
			return r;
		
		ArrayList<String> definitions = getDefinitionsForWordAdverb(word);
		for(int i=0; i<definitions.size(); i++) {
			r = detectProfessionalCommunity(definitions.get(i));
			if (r!=-1)
				return r;
		}
		
		return -1;
	}
	
	public int detectProfessionalCommunityAdjective(String word) {
		int r;
		r = detectProfessionalCommunity(word);
		if (r!=-1)
			return r;
		
		ArrayList<String> definitions = getDefinitionsForWordAdjective(word);
		for(int i=0; i<definitions.size(); i++) {
			r = detectProfessionalCommunity(definitions.get(i));
			if (r!=-1)
				return r;
		}
		
		return -1;
	}
	
	public int detectProfessionalCommunityByPOS(String word, int POSCategoryID) {
		switch (POSCategoryID) {
			case Constants.nounCategoryID: 
				return detectProfessionalCommunityNoun(word);
			case Constants.verbCategoryID: 
				return detectProfessionalCommunityVerb(word);
			case Constants.adverbCategoryID: 
				return detectProfessionalCommunityAdverb(word);
			case Constants.adjectiveCategoryID: 
				return detectProfessionalCommunityAdjective(word);
			default: 
				return -1;
		}
	}

	public boolean checkIfMultipleMeaningsByPOS(String word, int POSCategoryID) {
		switch (POSCategoryID) {
			case Constants.nounCategoryID: 
				return checkIfMultipleMeaningsNoun(word);
			case Constants.verbCategoryID: 
				return checkIfMultipleMeaningsVerb(word);
			case Constants.adverbCategoryID: 
				return checkIfMultipleMeaningsAdverb(word);
			case Constants.adjectiveCategoryID: 
				return checkIfMultipleMeaningsAdjective(word);
			default: 
				return false;
		}
	}
	
	public boolean checkIfSexualConnotationByPOS(String word, int POSCategoryID) {
		switch (POSCategoryID) {
		case Constants.nounCategoryID: 
			return checkIfSexualConnotationNoun(word);
		case Constants.verbCategoryID: 
			return checkIfSexualConnotationVerb(word);
		case Constants.adverbCategoryID: 
			return checkIfSexualConnotationAdverb(word);
		case Constants.adjectiveCategoryID: 
			return checkIfSexualConnotationAdjective(word);
		default: 
			return false;
	}
	}
	
	public boolean checkIfSexism(String word, int POSCategoryID) {
		switch (POSCategoryID) {
		case Constants.nounCategoryID: 
			return this.checkIfSexismNoun(word);
		case Constants.verbCategoryID: 
			return this.checkIfSexismVerb(word);
		case Constants.adverbCategoryID: 
			return this.checkIfSexismAdverb(word);
		case Constants.adjectiveCategoryID: 
			return this.checkIfSexismAdjective(word);
		default: 
			return false;
	}
	}
}
