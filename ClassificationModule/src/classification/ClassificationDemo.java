package classification;

import weka.core.Instances;

public class ClassificationDemo {

	public static void main(String args[]) throws Exception {
		
		
		ClassificationType type = ClassificationType.C45;
		Instances  train = AbstractClassifier.loadInstances("data/input/Thyroid.dat"),  test = AbstractClassifier.loadInstances("data/input/thyrtest.dat");
		MyClassifier classifier = new MyClassifier(train, type);
		
		classifier.classify(test, "data/output/Thyroid_out.dat");
		
		classifier.evaluateClassifier(train, test);
		
		classifier.saveClassifier("data/output/classifier"+type+".model");
		
		
		MyClassifier new_Classifier = new MyClassifier("data/output/classifier"+type+".model");
		new_Classifier.evaluateClassifier(train, test);
	
		classifier.createClusters(train);
		

	}
}
