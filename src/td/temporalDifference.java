//package td;
//
//import java.util.ArrayList;
//
//import objects.IOTuple;
//
//import org.neuroph.core.NeuralNetwork;
//import org.neuroph.core.data.DataSet;
//import org.neuroph.nnet.MultiLayerPerceptron;
//import org.neuroph.nnet.learning.DynamicBackPropagation;
//
//public class temporalDifference {
//    private NeuralNetwork network = NeuralNetwork.createFromFile("lib/NN.nnet");
//    private MultiLayerPerceptron nn = MultiLayerPerceptron.createFromFile("lib/NN.nnet");
//    private ArrayList<IOTuple> neuralNetworkData;
//    private DynamicBackPropagation backprop = new DynamicBackPropagation();
//     private Writer writer = new Writer();
//    private int outcome;
//    private double learningRate = 0.00001, lambda = 0.01;
//    private DataSet learningData;
//
//     private boolean writeGame;
//
//    public temporalDifference() {
//	backprop.setNeuralNetwork(network);
//    }
//
//    public void setOutcome(int outcome) {
//	this.outcome = outcome;
//    }
//
//    public void setDataToLearn(ArrayList<IOTuple> data, boolean writeGame) {
//	this.neuralNetworkData = data;
//	learningData = new DataSet(neuralNetworkData.get(0).getInput().length, neuralNetworkData.get(0).getOutput().length);
//	 Paramaters describe sizes of input and output
//	for (IOTuple tuple : data) {
//	    learningData.addRow(tuple.getInput(), tuple.getOutput());
//	}
//	System.out.println(learningData.size());
//	learn();
//    }
//
//    private void learn() {
//	System.out.println("Test");
//	 Double[] ori = network.getWeights();
//	double[] output = new double[3];
//	backprop.setUseDynamicMomentum(false);
//	backprop.setMaxIterations(500);
//	backprop.setLearningRateChange(lambda);
//	backprop.setLearningRate(learningRate);
//	network.learn(learningData, backprop);
//	for(int i = 0; i<10; i++){
//	    backprop.doLearningEpoch(learningData);
//	    System.out.println(backprop.getLearningRate());
//	}
//    }
//}

package td;

import java.util.ArrayList;
import java.util.Arrays;

import objects.NNSettings;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.DynamicBackPropagation;

import dataStructures.IOTuple;

public class temporalDifference {
	private NeuralNetwork<BackPropagation> network = NeuralNetwork.load(NNSettings.nn);
	private BackPropagation backprop = new BackPropagation();
	// private Writer writer = new Writer();
	private DataSet learningData;

	public temporalDifference() {
		backprop.setNeuralNetwork(network);
	}

	public void learn(ArrayList<IOTuple> data) {
		learningData = new DataSet(NNSettings.inputSize, NNSettings.outputSize);
		int lambdaPower = 0;
		// Last move Case
		learningData.addRow(data.get(data.size() - 2).getInput(), data.get(data.size() - 1).getOutput());
		backprop.setLearningRate(NNSettings.learningRate * Math.pow(NNSettings.lambda, lambdaPower));
		backprop.learn(learningData, 1);
		// All other Cases
		for (int count = data.size() - 2; count > 0; count--) {
			learningData.clear();
			learningData.addRow(data.get(count - 1).getInput(), data.get(count).getOutput());
			backprop.setLearningRate(NNSettings.learningRate * Math.pow(NNSettings.lambda, lambdaPower));
			backprop.learn(learningData, 1);
		}
	}
	
	public void save(){
		network.save(NNSettings.nn);
	}
}
