
public class Main {
	
	public static XMLInfo xmlInfo;
	 
	
	public static void main(String args[]) {
		
		
		int countTotalInsulting = 0;
		int countTotalSexism = 0;
		int countTotalRacist = 0;
		
		System.setProperty("wordnet.database.dir", args[1]);
		
		
		String directory = args[0];
		
		for(int i=0; i<555; i++) {
			String filename = directory + "\\joke-"+i+".xml";
			
			xmlInfo = new XMLInfo(filename, args[2], args[3]);
			xmlInfo.printOutputXMLFile();
			countTotalInsulting += xmlInfo.countInsulting;
			countTotalSexism += xmlInfo.countSexism;
			countTotalRacist += xmlInfo.countRacist;		
		}
		
		System.out.println("INSULTING "+ countTotalInsulting);
		System.out.println("SEXSIM "+countTotalSexism);
		System.out.println("RACIST "+countTotalRacist);
		
	/*	System.setProperty("wordnet.database.dir", args[1]);
		
		xmlInfo = new XMLInfo(args[0]);
		xmlInfo.printOutputXMLFile();*/
		
	}

}
