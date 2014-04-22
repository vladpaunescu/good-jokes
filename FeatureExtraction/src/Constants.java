
public class Constants {
	public static final String[] POSConsidered = {"VB", "VBD", "VBG", "VBN", "VBP", "VBZ",
													"RB", "RBR", "RBS", 
													"JJ", "JJR", "JJS",
													"NN", "NNS", "NNP", "NNPS"};
	
	public static final String[] consideredNounPOS = {"NN", "NNS", "NNP", "NNPS"};
	public static final String[] consideredVerbPOS = {"VB", "VBD", "VBG", "VBN", "VBP", "VBZ"};
	public static final String[] consideredAdverbPOS = {"RB", "RBR", "RBS"};
	public static final String[] consideredAdjectivePOS = {"JJ", "JJR", "JJS"};
	
	public static final String[][] POScategories = {consideredNounPOS, consideredVerbPOS, consideredAdverbPOS, consideredAdjectivePOS};
	public static final int nrPOSCategories = 4;
	
	public static final int nounCategoryID = 0;
	public static final int verbCategoryID = 1;
	public static final int adverbCategoryID = 2;
	public static final int adjectiveCategoryID = 3;
  	
	public static final String[] sexualConnotationWords = {"sex", "sexual", "sexuality"};
	
	public static final String[][] professionalCommunities = {{"doctor", "medical", "dentist", "nurse", "medicine"},
																{"justice", "magistrate", "jurist", "judge", "lawyer", "law"},
																{"school", "university", "teacher", "student", "class"}};
	
	public static boolean isNounPOS(String pos) {
		for(int i=0; i<Constants.consideredNounPOS.length; i++) 
			if (pos.equals(Constants.consideredNounPOS[i]))
				return true;
		return false;
	}
	
	public static boolean isVerbPOS(String pos) {
		for(int i=0; i<Constants.consideredVerbPOS.length; i++) 
			if (pos.equals(Constants.consideredVerbPOS[i]))
				return true;
		return false;
	}
	
	public static boolean isAdjectivePOS(String pos) {
		for(int i=0; i<Constants.consideredAdjectivePOS.length; i++) 
			if (pos.equals(Constants.consideredAdjectivePOS[i]))
				return true;
		return false;
	}
	
	public static boolean isAdverbPOS(String pos) {
		for(int i=0; i<Constants.consideredAdverbPOS.length; i++) 
			if (pos.equals(Constants.consideredAdverbPOS[i]))
				return true;
		return false;
	}
	
	public static int getPOSCategoryID(String pos) {
		for(int i=0; i<Constants.nrPOSCategories; i++) {
			for(int j=0; j<Constants.POScategories[i].length; j++)
				if (pos.equals(Constants.POScategories[i][j]))
					return i;
		}
		return -1;
	}
	

}
