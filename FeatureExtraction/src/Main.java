
public class Main {
	
	public static XMLInfo xmlInfo;
	 
	
	public static void main(String args[]) {
		
		System.setProperty("wordnet.database.dir", args[1]);
		
		xmlInfo = new XMLInfo(args[0]);
		xmlInfo.printOutputXMLFile();
		
	}

}
