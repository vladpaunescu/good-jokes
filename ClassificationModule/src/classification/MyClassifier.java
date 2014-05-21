package classification;

import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;










import java.text.SimpleDateFormat;
import java.util.Date;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.EM;
import weka.core.Instances;
import weka.filters.Filter;
import weka.gui.explorer.ClustererPanel;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

public class MyClassifier extends AbstractClassifier {


	public MyClassifier() {}
	
	public MyClassifier(Instances train, ClassificationType type)
			throws Exception {
		super(train, type);
	}
	
	public MyClassifier(String path)
			throws Exception {
		super(path);
	}

	@Override
	protected Classifier loadClassifier(String fileName) throws Exception {
		
		return (Classifier) weka.core.SerializationHelper.read(fileName);
	}



	@Override
	protected Classifier createClassifier(Instances data,
			ClassificationType type) throws Exception {

		Classifier cModel = null;

		switch (type) {
		case C45:
			String[] options = new String[1];
			options[0] = "-U"; // unpruned tree
			J48 tree = new J48(); // new instance of tree
			tree.setOptions(options); // set the options
			tree.buildClassifier(data); // build classifier
			drawTree(tree);
			cModel = (Classifier) tree;
			break;
		case NaiveBayes:
			cModel = (Classifier) new NaiveBayes();
			cModel.buildClassifier(data);
			break;
		case NeuralNetwork:
			cModel = (Classifier) new MultilayerPerceptron();
			cModel.buildClassifier(data);
			break;
		case LogisticRegression:
			cModel = (Classifier) new Logistic();
			cModel.buildClassifier(data);
			break;
		default:
			break;

		}

		return cModel;
	}

	@Override
	public void classify(Instances test, String filePath)
			throws Exception, IOException {
		
		Instances labeled = new Instances(test);
		// label instances
		for (int i = 0; i < test.numInstances(); i++) {
			double clsLabel = classifier.classifyInstance(test.instance(i));
			labeled.instance(i).setClassValue(clsLabel);
		}
		// save labeled data
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		writer.write(labeled.toString());
		writer.newLine();
		writer.flush();
		writer.close();

	}

	@Override
	public void saveClassifier(String fileName) throws Exception {
		
		weka.core.SerializationHelper.write(fileName, classifier);

	}

	@Override
	public void evaluateClassifier(Instances train, Instances test) throws Exception {
		
		Evaluation eval = new Evaluation(train);
		eval.evaluateModel(classifier, test);
		System.out.println(eval.toSummaryString("\nResults\n======\n", false));

	}
	
	private static void drawTree(J48 tree) throws Exception {

		// display classifier
		final javax.swing.JFrame jf = new javax.swing.JFrame(
				"C4.5 Tree Visualizer");

		jf.setSize(1600, 1000);
		jf.getContentPane().setLayout(new BorderLayout());
		TreeVisualizer tv = new TreeVisualizer(null, tree.graph(),
				new PlaceNode2());
		jf.getContentPane().add(tv, BorderLayout.CENTER);
		jf.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				jf.dispose();
			}
		});

		jf.setVisible(true);
		tv.fitToScreen();
	}

	@Override
	public void createClusters(Instances train) throws Exception {

		weka.filters.unsupervised.attribute.Remove filter = new weka.filters.unsupervised.attribute.Remove();
		filter.setAttributeIndices("" + (train.classIndex() + 1));
		filter.setInputFormat(train);

		Instances dataClusterer = Filter.useFilter(train, filter);

		String[] options = new String[4];
		options[0] = "-I"; // max. iterations
		options[1] = "100";
		options[2] = "-N"; // number of clusters
		options[3] = "2";
		

		EM em = new EM();
		em.setOptions(options);
		em.buildClusterer(dataClusterer);

		clusterer = (Clusterer) em;
		
		visualizeCluster(train);

	}

	private void visualizeCluster(Instances train) throws Exception {
		ClusterEvaluation eval = new ClusterEvaluation();
	    eval.setClusterer(clusterer);
	    eval.evaluateClusterer(train);
	    
		// setup visualization
	    // taken from: ClustererPanel.startClusterer()
	    PlotData2D predData = ClustererPanel.setUpVisualizableInstances(train, eval);
	    String name = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
	    String cname = clusterer.getClass().getName();
	    if (cname.startsWith("weka.clusterers."))
	      name += cname.substring("weka.clusterers.".length());
	    else
	      name += cname;
	 
	    VisualizePanel vp = new VisualizePanel();
	    vp.setName(name + " (" + train.relationName() + ")");
	    predData.setPlotName(name + " (" + train.relationName() + ")");
	    vp.addPlot(predData);
	 
	    // display data
	    // taken from: ClustererPanel.visualizeClusterAssignments(VisualizePanel)
	    String plotName = vp.getName();
	    final javax.swing.JFrame jf = 
	      new javax.swing.JFrame("Visualize Clusters: " + plotName);
	    jf.setSize(1000,900);
	    jf.getContentPane().setLayout(new BorderLayout());
	    jf.getContentPane().add(vp, BorderLayout.CENTER);
	    jf.addWindowListener(new java.awt.event.WindowAdapter() {
	      public void windowClosing(java.awt.event.WindowEvent e) {
	        jf.dispose();
	      }
	    });
	    jf.setVisible(true);
	}

}
