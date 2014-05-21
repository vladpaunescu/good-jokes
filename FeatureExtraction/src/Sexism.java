import java.util.ArrayList;


public class Sexism {
	
	private String suggestAbuseWords[] = {"discriminatory", "abusive", "insult", "insulting", "abusing", "discriminaroty"};
	
	private String referenceToSexDifferences[] = {"opposite sex", "male", "female"};
	
	public Sexism() {
		
	}
	
	public boolean isSexism_simple(String definition) {
		boolean ok = false;
		for(int i=0; i<referenceToSexDifferences.length; i++) {
			if (definition.contains(referenceToSexDifferences[i]))
				ok = true;
		}
		if (!ok)
			return false;
		ok = false;
		for(int i=0; i<suggestAbuseWords.length; i++) {
			if (definition.contains(suggestAbuseWords[i]))
				ok = true;
		}
		return ok;
	}
	
	public boolean isSexism(String definition, ArrayList<String> synonymDefinitions) {
		if (isSexism_simple(definition))
			return true;
		if (synonymDefinitions!=null){
			for(int i=0; i<synonymDefinitions.size(); i++) {
				if (isSexism_simple(synonymDefinitions.get(i)))
					return true;
			}
		}
		return false;
	}

}
