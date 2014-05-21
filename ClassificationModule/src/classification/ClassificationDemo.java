package classification;

import java.util.Random;

import weka.core.Instances;

public class ClassificationDemo {

	public static void main(String args[]) throws Exception {
		
		if(args.length != 6){
			System.out.println("Arguments: run_type num_training num_test folder_training folder_test classifier_test");
			System.exit(1);
		}
		
		String run_type = args[0];
		
		switch(run_type){
			case("cross-validation"):
			{
				int numTraining = Integer.parseInt(args[1]);
				String folder_training = args[3];
				
				System.out.println(folder_training + " : "+numTraining);
				test(folder_training, numTraining);
				break;
			}
			case("train"):
			{
				int numTraining = Integer.parseInt(args[1]);
				String folder_training = args[3];
				System.out.println(folder_training + " : "+numTraining);
				
				ClassificationType type = ClassificationType.C45;
				train(folder_training, numTraining, type);
				break;
			}
			case("cluster"):
			{
				int numTraining = Integer.parseInt(args[1]);
				String folder_training = args[3];
				System.out.println(folder_training + " : "+numTraining);
				
				cluster(folder_training, numTraining);
				break;
			}
			case("classify"):
			{
				
				int numTest = Integer.parseInt(args[2]);
				String folder_test = args[4], classifier_test = args[5];
				
				System.out.println(folder_test + " : "+numTest + " : "+ classifier_test);
				
				classify(folder_test, numTest, classifier_test);
				break;
			}
			default:
			{
				System.out.println("run_type can only be : cross-validation, train, cluster, classify");
				break;
			}
		}
		
		
		
		
		//classify("data/input/test/", 6, "data/output/classifier"+type+".model");
		
		
		

	}
	
	public static void train(String folderPath, int numFiles, ClassificationType type) throws Exception{
		
		Instances train  = ClassificationModule.parseJokes(folderPath, numFiles);
		MyClassifier classifier = new MyClassifier(train, type);
		
		classifier.saveClassifier("data/output/classifier"+type+".model");
		
	}
	
	public static void classify(String folderPath, int numFiles, String classifierPath) throws Exception{
		
		Instances test  = ClassificationModule.parseJokes(folderPath, numFiles);
		MyClassifier classifier = new MyClassifier(classifierPath);
		
		classifier.classify(test, "data/output/classification_out.dat");
		
	}
	
	public static void cluster(String folderPath, int numFiles) throws Exception{
		
		Instances train  = ClassificationModule.parseJokes(folderPath, numFiles);
		
		new MyClassifier().createClusters(train);
	}
	
	private static void test(String folderPath, int numFiles) throws Exception
	{
		for(ClassificationType type: ClassificationType.values()){
			
			System.out.println("TYPE: "+type);
			test_type(folderPath, numFiles, type);
		}
	}
	
	private static void test_type(String folderPath, int numFiles, ClassificationType type) throws Exception{
		
		MyClassifier classifier;
		Instances randData, data  = ClassificationModule.parseJokes(folderPath, numFiles);
		int seed = 10;          // the seed for randomizing the data
		int folds = 3;         // the number of folds to generate, >=2
		
		Random rand = new Random(seed);   // create seeded number generator
		randData = new Instances(data);   // create copy of original data
		randData.randomize(rand);         // randomize data with number generator
		 
		if (randData.classAttribute().isNominal())
		      randData.stratify(folds);
		
		for (int n = 0; n < folds; n++) {
			Instances train = randData.trainCV(folds, n);
			Instances test = randData.testCV(folds, n);
			
			classifier = new MyClassifier(train, type);
			classifier.evaluateClassifier(train, test);
		}
		
		
	}
}
