
public class Main {
	
	public static XMLInfo xmlInfo;
	 
	
	public static void main(String args[]) {
		
		System.setProperty("wordnet.database.dir", args[1]);
		
	/*	String directory = "D:\\Master - semestrul II\\Good jokes project\\Corpus";
		//for(int i=0; i<586; i++) {
		for(int i=0; i<1; i++) {
			String filename = directory + "\\joke-"+i+".xml";
			
			xmlInfo = new XMLInfo(filename);
			xmlInfo.printOutputXMLFile();
		}*/
		
		System.setProperty("wordnet.database.dir", args[1]);
		
		xmlInfo = new XMLInfo(args[0]);
		xmlInfo.printOutputXMLFile();
		
	}

}
