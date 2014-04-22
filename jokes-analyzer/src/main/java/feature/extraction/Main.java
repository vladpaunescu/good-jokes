package feature.extraction;

public class Main {
	
	public static XMLInfo xmlInfo;
	 
	
	public static void main(String args[]) {
		
		System.setProperty("wordnet.database.dir", "D:\\Personal\\WordNet-3.0\\dict");
		
		xmlInfo = new XMLInfo("D:\\Personal\\good-jokes\\FeatureExtraction\\data\\joke-0.xml");
		xmlInfo.printOutputXMLFile();
		
	}

}
