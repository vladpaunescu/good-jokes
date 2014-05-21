package classification;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.Arrays;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class ClassificationModule {
	
	private static final int NUM_JOKE_FEATURES = 15;
	private static final FastVector featureVector = createFeatureVector();
	
	private static double minRating, avgRating, maxRating, minVotes, avgVotes,  maxVotes, avgScore;
	private static int count = 0;
	
	public static Instances parseJokes(String folderPath, int number){
		
			
		Instances instances = new Instances(folderPath, featureVector, number);
		XMLInfo xmlInfo;
		
		
			
		minRating= Double.MAX_VALUE;
		avgRating = maxRating = 0;
		minVotes = Double.MAX_VALUE;
		avgVotes =  maxVotes = 0;
		avgScore = 0;
		count = 0;
		
		for(int i=0;i<number;i++){
			xmlInfo = new XMLInfo(folderPath + "joke-" + i + ".xml");
			computeLimits(xmlInfo);
		}
		
		for(int i=0;i<number;i++){
			xmlInfo = new XMLInfo(folderPath + "joke-" + i + ".xml");
			computeAverageScore(xmlInfo, minVotes, maxVotes,minRating,  maxRating );
		}
		
		avgScore /= number;
		
//		System.out.println(number);
//		System.out.println("Votes: min = "+minVotes +" avg = "+avgVotes/number+" max = "+maxVotes);
//		System.out.println("Ratings: min = "+minRating +" avg = "+avgRating/number+" max = "+maxRating);
//		System.out.println("Avg proc = "+ avgScore);
		
		
		instances.setClassIndex(NUM_JOKE_FEATURES);
		
		for(int i=0;i<number;i++){
			xmlInfo = new XMLInfo(folderPath + "joke-" + i + ".xml");
			instances.add(createJokeExample(xmlInfo));
		}
					
		
		System.out.println("Positives = " + 100*count/number+"%");
		return instances;
	}

	private static Instance createJokeExample(XMLInfo xmlInfo) {
		
		Instance instance = new Instance(NUM_JOKE_FEATURES+1);
		String aux;
		double values[];
		
		//values = parseComaSeparated(xmlInfo.getContentForTagName("professional_communities_histogram"),3);
		//values = parseComaSeparated(xmlInfo.getContentForTagName("POS_histogram"),16);
		
		
		instance.setValue((Attribute)featureVector.elementAt(0), Double.parseDouble(xmlInfo.getContentForTagName("length")));
		
		
		instance.setValue((Attribute)featureVector.elementAt(1), Double.parseDouble(xmlInfo.getContentForTagName("QA")));
		instance.setValue((Attribute)featureVector.elementAt(2), Double.parseDouble(xmlInfo.getContentForTagName("nouns_with_sexual_connotations")));
		instance.setValue((Attribute)featureVector.elementAt(3), Double.parseDouble(xmlInfo.getContentForTagName("nouns_with_multiple_meanings")));
		instance.setValue((Attribute)featureVector.elementAt(4), Double.parseDouble(xmlInfo.getContentForTagName("verbs_with_sexual_connotations")));
		instance.setValue((Attribute)featureVector.elementAt(5), Double.parseDouble(xmlInfo.getContentForTagName("verbs_with_multiple_meanings")));
		instance.setValue((Attribute)featureVector.elementAt(6), Double.parseDouble(xmlInfo.getContentForTagName("adjectives_with_sexual_connotations")));
		instance.setValue((Attribute)featureVector.elementAt(7), Double.parseDouble(xmlInfo.getContentForTagName("adjectives_with_multiple_meanings")));
		instance.setValue((Attribute)featureVector.elementAt(8), Double.parseDouble(xmlInfo.getContentForTagName("adverbs_with_sexual_connotations")));
		instance.setValue((Attribute)featureVector.elementAt(9), Double.parseDouble(xmlInfo.getContentForTagName("adverbs_with_multiple_meanings")));
		
		

		
		
		instance.setValue((Attribute)featureVector.elementAt(10), Double.parseDouble(xmlInfo.getContentForTagName("racist_slur")));
		instance.setValue((Attribute)featureVector.elementAt(11), Double.parseDouble(xmlInfo.getContentForTagName("sexism")));
		instance.setValue((Attribute)featureVector.elementAt(12), Double.parseDouble(xmlInfo.getContentForTagName("insulting")));
		instance.setValue((Attribute)featureVector.elementAt(13), Double.parseDouble(xmlInfo.getContentForTagName("quote")));
		instance.setValue((Attribute)featureVector.elementAt(14), Double.parseDouble(xmlInfo.getContentForTagName("comments_count")));


		
		instance.setValue((Attribute)featureVector.elementAt(15), checkIfGoodJoke(xmlInfo) ? "GOOD_JOKE" :  "BAD_JOKE");
		
		return instance;
	}

	private static boolean checkIfGoodJoke(XMLInfo xmlInfo) {
		
		double rating =  Double.parseDouble(xmlInfo.getContentForTagName("rating"));
		double user_votes = Double.parseDouble(xmlInfo.getContentForTagName("votes"));
		double proc1;
		double proc2;
		proc1 = (user_votes - minVotes)/(maxVotes - minVotes);
		proc2 = (rating- minRating)/(maxRating - minRating);
		
		//System.out.println("Score: "+ proc1*proc2 + "(avg = "+avgScore+")");
		if(proc1*proc2 >= avgScore) count++;
		return  proc1*proc2 > avgScore;
	}

	private static void computeLimits(XMLInfo xmlInfo) {
		
		double rating =  Double.parseDouble(xmlInfo.getContentForTagName("rating"));
		double user_votes = Double.parseDouble(xmlInfo.getContentForTagName("votes"));

		
		avgRating += rating;
		if(rating < minRating)
			minRating = rating;
		if(rating > maxRating)
			maxRating = rating;
		
		avgVotes += user_votes;
		if(user_votes < minVotes)
			minVotes = user_votes;
		if(user_votes > maxVotes)
			maxVotes = user_votes;
			
	}

	private static void computeAverageScore(XMLInfo xmlInfo, double minVotes, double maxVotes, double minRating, double maxRating ) {
		
		double rating =  Double.parseDouble(xmlInfo.getContentForTagName("rating"));
		double user_votes = Double.parseDouble(xmlInfo.getContentForTagName("votes"));
		double proc1;
		double proc2;
		proc1 = (user_votes - minVotes)/(maxVotes - minVotes);
		proc2 = (rating- minRating)/(maxRating - minRating);
		
		avgScore += proc1*proc2;
	}

	private static double[] parseComaSeparated(String contentForTagName, int num) {
		
		String elems[] = contentForTagName.split(",");
		double result[] = new double[elems.length];
		
		if(num != elems.length) throw new IllegalArgumentException();
		
		for(int i=0;i<elems.length;i++){
			result[i] = Double.parseDouble(elems[i]);
		}
		
		return result;
	}

	private static FastVector createFeatureVector() {
		
		FastVector attInfo = new FastVector(NUM_JOKE_FEATURES + 1);
		attInfo.addElement(new Attribute("length"));
		attInfo.addElement(new Attribute("QA"));
		attInfo.addElement(new Attribute("n_sexual_con"));
		attInfo.addElement(new Attribute("n_multiple_me"));
		attInfo.addElement(new Attribute("v_sexual_con"));
		attInfo.addElement(new Attribute("v_multiple_me"));
		attInfo.addElement(new Attribute("adj_sexual_con"));
		attInfo.addElement(new Attribute("adj_multiple_me"));
		attInfo.addElement(new Attribute("adv_sexual_con"));
		attInfo.addElement(new Attribute("adv_multiple_me"));
		attInfo.addElement(new Attribute("racist_slur"));
		attInfo.addElement(new Attribute("sexism"));
		attInfo.addElement(new Attribute("insulting"));
		attInfo.addElement(new Attribute("quote"));
		attInfo.addElement(new Attribute("comments"));
		
		FastVector fvClassVal = new FastVector(2);
		 fvClassVal.addElement("GOOD_JOKE");
		 fvClassVal.addElement("BAD_JOKE");
		 
		attInfo.addElement(new Attribute("class", fvClassVal));
		return attInfo;
	}

}
