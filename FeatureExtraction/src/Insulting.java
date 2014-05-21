import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Insulting {
	
	private ArrayList<String> insultingWords;
	
	public Insulting(String filename) {
		insultingWords = new ArrayList<String>();
		
		try {
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line; 
			while ((line=br.readLine())!=null) {
				line = line.replaceAll("\n", "");
				insultingWords.add(line);
				
			}
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isInsultingWord(String word, ArrayList<String> synonyms) {
		if (this.insultingWords.contains(word))
			return true;
		if (synonyms!=null) {
			for(int i=0; i<synonyms.size(); i++) {
				if (this.insultingWords.contains(synonyms.get(i)))
					return true;
			}
		}
		return false;
	}

}
