import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class RacistSlur {
	
	private ArrayList<String> racistSlutWords;
	
	public RacistSlur(String filename) {
		racistSlutWords = new ArrayList<String>();
		
		try {
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
			String line;
			while ((line=br.readLine())!=null) {
				line = line.replaceAll("\n", "");
				racistSlutWords.add(line);
			
			}
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isRacistSlurWord(String word) {
		return this.racistSlutWords.contains(word);
	}

}
