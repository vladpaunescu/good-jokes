
			What makes a good joke? - Classification of jokes

 
							Haller Emanuela
							Păunescu Vlad
							Scarlat Georgiana
            
            			Artificial Intelligence Master

The complete source code can be found at:

https://github.com/vladpaunescu/good-jokes



Instructions to run the demo applications:
=========================================

	- Functionality:
		- train classifier (the classifier will be saved in data/output/classifiername.model)
		- test classifier using cross validation (the result will be printed in the console)
		- classify jokes (for which the features are already extracted) (the classification results are saved in data/output folder)
		- show clusters (clusteres are shown in GUI)

	- Arguments:
		1) run_type - one of cross-validation, train, cluster, classify
		2) num_training - number of training examples
		3) num_test - number of test examples
		4) folder_training - folder where training examples are found (numbered from 0 beginning with joke-0.xml)
		5) folder_test - folder where test examples are found (numbered from 0 beginning with joke-0.xml)
		6) classifier_test - file where the already trained classifier is

	- How to run:
		- import project in eclipse (or any other IDE), add arguments as specified above and run

	For this demo there already exists a corpus used for training in data/input/corpus and an already trained 
clasifier in data/output/classifierC45.model.


