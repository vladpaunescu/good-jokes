package classification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Instances;

public abstract class AbstractClassifier {
	
	protected Classifier classifier;
	protected Clusterer clusterer;
	
	public AbstractClassifier() {}
	
	/**
	 * Creates classifier by loading it from file.
	 * 
	 * @param path Path to file where the classifier is saved.
	 * 
	 * @throws Exception
	 */
	public AbstractClassifier(String path) throws Exception{
		
		classifier = loadClassifier(path);
	}
	
	/**
	 * Creates classifier of certain type by training it using train data.
	 * 
	 * @param train Train data.
	 * @param type Classifier type.
	 * 
	 * @throws Exception
	 */
	public AbstractClassifier(Instances train, ClassificationType type) throws Exception {		
		
		classifier = createClassifier(train, type);
		
	}
	
	/**
	 * Loads classifier from file.
	 * 
	 * @param fileName Path to file containing classifier.
	 * 
	 * @return loaded classifier
	 * 
	 * @throws Exception
	 */
	protected abstract Classifier loadClassifier(String fileName) throws Exception;
	
		
	
	/**
	 * Trains a new classifier of a certain type.
	 * 
	 * @param data Training data.
	 * @param type Classifier type.
	 * 
	 * @return Trained classifier.
	 * 
	 * @throws Exception
	 */
	protected abstract Classifier createClassifier(Instances data, ClassificationType type) throws Exception;
	
	/**
	 * 
	 * Classifies test data.
	 * 
	 * @param test Test data.
	 * @param filePath File path where to save classified data.
	 * 
	 * @throws Exception
	 * @throws IOException
	 */
	public abstract void classify(Instances test, String filePath) throws Exception, IOException;
	
	/**
	 * Saves classifier to file.
	 * 
	 * @param fileName Path where to save the classifier.
	 * 
	 * @throws Exception 
	 * 
	 */
	public abstract void saveClassifier(String fileName) throws Exception;
	
	/**
	 * Evaluates classifier using train and test data and prints statistics.
	 * 
	 * @param train Training data.
	 * @param test Test data.
	 * 
	 * @throws Exception
	 */
	public abstract void evaluateClassifier(Instances train, Instances test) throws Exception;
	
	/**
	 * 
	 * Transforms train data into unlabeled data and uses it for clustering.
	 * 
	 * @param train Trin data.
	 * 
	 * @throws Exception
	 */
	public abstract void createClusters(Instances train) throws Exception;
	
	/**
	 * Loads training/test data from file.
	 * 
	 * @param filePath Path to training/test data file.
	 * @return Training/test data.
	 * 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public static Instances loadInstances(String filePath) throws FileNotFoundException, IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		Instances data = new Instances(reader);
		reader.close();
		// setting class attribute
		data.setClassIndex(data.numAttributes() - 1);
		return data;
	}

}
