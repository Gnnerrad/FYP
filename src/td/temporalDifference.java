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

import objects.IOTuple;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.DynamicBackPropagation;

public class temporalDifference {
    private NeuralNetwork<BackPropagation> network = NeuralNetwork.load("lib/NN.nnet");
    private ArrayList<IOTuple> neuralNetworkData;
    private BackPropagation backprop = new BackPropagation();
    // private Writer writer = new Writer();
    private double learningRate = 0.00001, lambda = 0.5;
    private DataSet learningData;
    private boolean writeGame;

    public temporalDifference() {
	backprop.setNeuralNetwork(network);
    }

    // public void setDataToLearn(ArrayList<IOTuple> data, boolean writeGame) {
    // this.neuralNetworkData = data;
    // learningData = new DataSet(neuralNetworkData.get(0).getInput().length,
    // neuralNetworkData.get(0).getOutput().length);
    // // Paramaters describe sizes of input and output
    // for (IOTuple tuple : data) {
    // learningData.addRow(tuple.getInput(), tuple.getOutput());
    // }
    // System.out.println(learningData.size());
    // this.writeGame = writeGame;
    // if (writeGame) {
    // // writer.writeGameDecisions(gameNumber, data);
    // // network.save("data/" + gameNumber + ".nnet");
    // }
    // learn();
    // }

    public void learn(ArrayList<IOTuple> data) {
	learningData = new DataSet(data.get(0).getInput().length, data.get(1).getOutput().length);
	int lambdaPower = 0;
//	Double[] weights = network.getWeights();
//	for (int i = 0; i < 20; i++) {
//	    System.out.print(weights[i]+", ");
//	}
	for (int count = data.size() - 1; count > 0; count--) {
	    learningData.clear();
	    learningData.addRow(data.get(count - 1).getInput(), data.get(count).getOutput());
	    backprop.setLearningRate(learningRate * Math.pow(lambda, lambdaPower));
	    backprop.learn(learningData, 1000);
	}
	network.save("lib/NN.nnet");
	// weights = network.getWeights();
	// for (int i = 0; i < 20; i++) {
	// System.out.print(weights[i] + ", ");
	// }
    }
}
