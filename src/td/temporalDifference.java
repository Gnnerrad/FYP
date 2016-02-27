package td;

import java.util.ArrayList;

import objects.IOTuple;
import objects.Writer;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.learning.BackPropagation;

public class temporalDifference {
    private NeuralNetwork<BackPropagation> network = NeuralNetwork.load("lib/NN.nnet");
    private ArrayList<IOTuple> neuralNetworkData;
    private BackPropagation backprop = new BackPropagation();
    private Writer writer = new Writer();
    private int outcome, gameNumber;
    private double learningRate = 0.00001, lambda = 0.01;
    private boolean writeGame;

    public temporalDifference() {
	backprop.setNeuralNetwork(network);
    }

    public void setOutcome(int outcome) {
	this.outcome = outcome;
    }

    public void setDataToLearn(ArrayList<IOTuple> data, int gameNumber, boolean writeGame) {
	this.neuralNetworkData = data;
	this.gameNumber = gameNumber;
	this.writeGame = writeGame;
	if (writeGame)
	    writer.writeGameDecisions(gameNumber, data);
	learn();
    }

    private void learn() {
	// Double[] ori = network.getWeights();
	DataSet learningData = new DataSet(neuralNetworkData.get(0).getInput().length, neuralNetworkData.get(0).getOutput().length);
	double[] output = new double[3];
	backprop.setMaxIterations(1);

	// special case for the last step.
	IOTuple gameData = neuralNetworkData.get(neuralNetworkData.size() - 1);
	if ((outcome == 1 && gameData.getId() == 1) || (gameData.getId() == 2 && outcome == 2)) {
	    output[0] = 1;
	    output[1] = 0;
	    output[2] = 0;
	    if (writeGame)
		writer.writeGameOutcome(gameNumber, output, gameData.getId());
	} else if ((gameData.getId() == 2 && outcome == 1) || (gameData.getId() == 1 && outcome == 2)) {
	    output[0] = 0;
	    output[1] = 0;
	    output[2] = 1;
	    if (writeGame)
		writer.writeGameOutcome(gameNumber, output, gameData.getId());
	} else {
	    output[0] = 0;
	    output[1] = 1;
	    output[2] = 0;
	    if (writeGame)
		writer.writeGameOutcome(gameNumber, output, 0);
	}
	learningData.addRow(gameData.getInput(), output);
	backprop.setLearningRate(learningRate);
	network.learn(learningData, backprop);

	// Every step but the last step.
	for (int time = neuralNetworkData.size() - 2; time >= 0; time--) {
	    gameData = neuralNetworkData.get(neuralNetworkData.size() - 1);

	    network.setInput(gameData.getInput());
	    network.calculate();
	    output = network.getOutput();

	    learningData.addRow(gameData.getInput(), output);
	    backprop.setLearningRate(learningRate * Math.pow(lambda, (neuralNetworkData.size() - (time + 2))));
	    network.learn(learningData, backprop);
	}
	// Double[] fin = network.getWeights();
	// double biggestChange = 0, smallestChange = 100;
	// for (int i = 0; i < fin.length; i++) {
	// double delta = Math.abs(ori[i] - fin[i]);
	// if(delta > biggestChange) biggestChange = delta;
	// if(delta < smallestChange) smallestChange = delta;
	// }
	// System.out.println(biggestChange + "\t" + smallestChange);
	network.save("lib/NN.nnet");
    }
}