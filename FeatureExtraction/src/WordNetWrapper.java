import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;


public class WordNetWrapper {
	
	WordNetDatabase database;
	
	public WordNetWrapper() {
		this.database = WordNetDatabase.getFileInstance();
	}
	

	private boolean checkIfSexualConnotation(String definition) {
		for(int j=0; j<Constants.sexualConnotationWords.length; j++) {
			if (definition.contains(Constants.sexualConnotationWords[j]))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexualConnotationNoun(String word) {
		NounSynset nounSynset;
		
		Synset[] synsets = this.database.getSynsets(word, SynsetType.NOUN);
		for(int i=0; i<synsets.length; i++) {
			nounSynset = (NounSynset)(synsets[i]);
			if (checkIfSexualConnotation(nounSynset.getDefinition()))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexualConnotationVerb(String word) {
		VerbSynset verbSynset;
		
		Synset[] synsets = this.database.getSynsets(word, SynsetType.VERB);
		for(int i=0; i<synsets.length; i++) {
			verbSynset = (VerbSynset)(synsets[i]);
			if (checkIfSexualConnotation(verbSynset.getDefinition()))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexualConnotationAdverb(String word) {
		AdverbSynset adverbSynset;
		
		Synset[] synsets = this.database.getSynsets(word, SynsetType.ADVERB);
		for(int i=0; i<synsets.length; i++) {
			adverbSynset = (AdverbSynset)(synsets[i]);
			if (checkIfSexualConnotation(adverbSynset.getDefinition()))
				return true;
		}
		return false;
	}
	
	public boolean checkIfSexualConnotationAdjective(String word) {
		AdjectiveSynset adjectiveSynset;
		
		Synset[] synsets = this.database.getSynsets(word, SynsetType.ADJECTIVE);
		for(int i=0; i<synsets.length; i++) {
			adjectiveSynset = (AdjectiveSynset)(synsets[i]);
			if (checkIfSexualConnotation(adjectiveSynset.getDefinition()))
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
					System.out.println(definition);
					return i;
				}
			}
		}
		return -1;
	}
	
	public int detectProfessionalCommunityNoun(String word) {
		int r;
		NounSynset nounSynset;
		
		r = detectProfessionalCommunity(word);
		if (r!=-1)
			return r;
		
		Synset[] synsets = this.database.getSynsets(word, SynsetType.NOUN);
		for(int i=0; i<synsets.length; i++) {
			nounSynset = (NounSynset)(synsets[i]);
			
			r = detectProfessionalCommunity(nounSynset.getDefinition());
			if (r!=-1)
				return r;
		}
		return -1;
	}
	
	public int detectProfessionalCommunityVerb(String word) {
		int r;
		VerbSynset verbSynset;
		
		r = detectProfessionalCommunity(word);
		if (r!=-1)
			return r;
		
		Synset[] synsets = this.database.getSynsets(word, SynsetType.VERB);
		for(int i=0; i<synsets.length; i++) {
			verbSynset = (VerbSynset)(synsets[i]);
			
			r = detectProfessionalCommunity(verbSynset.getDefinition());
			if (r!=-1)
				return r;
		}
		return -1;
	}
	
	
	public int detectProfessionalCommunityAdverb(String word) {
		int r;
		AdverbSynset adverbSynset;
		
		r = detectProfessionalCommunity(word);
		if (r!=-1)
			return r;
		
		Synset[] synsets = this.database.getSynsets(word, SynsetType.ADVERB);
		for(int i=0; i<synsets.length; i++) {
			adverbSynset = (AdverbSynset)(synsets[i]);
			
			r = detectProfessionalCommunity(adverbSynset.getDefinition());
			if (r!=-1)
				return r;
		}
		return -1;
	}
	
	public int detectProfessionalCommunityAdjective(String word) {
		int r;
		AdjectiveSynset adjectiveSynset;
		
		r = detectProfessionalCommunity(word);
		if (r!=-1)
			return r;
		
		Synset[] synsets = this.database.getSynsets(word, SynsetType.ADJECTIVE);
		for(int i=0; i<synsets.length; i++) {
			adjectiveSynset = (AdjectiveSynset)(synsets[i]);
			
			r = detectProfessionalCommunity(adjectiveSynset.getDefinition());
			if (r!=-1)
				return r;
		}
		return -1;
	}
}
