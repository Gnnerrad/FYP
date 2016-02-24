package nnTest;

import gameModes.BasicModeLayout;
import gameModes.MisereGameMode;
import gameModes.TrumpGameMode;

import java.util.ArrayList;
import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;

import objects.Card;
import objects.Deck;

public class NN {
    // NeuralNetwork network = NeuralNetwork.load("lib/Just Card neurons.nnet");
    private NeuralNetwork network = NeuralNetwork.load("lib/NN.nnet");
    private Boolean[] CardsIHave = new Boolean[52];
    private Boolean[] CardsSeen = new Boolean[52];
    private int gameMode = -1;
    private int CardInPlay = -1;
    private int CardToPlay = -1;

    public NN() {
	Arrays.fill(CardsIHave, Boolean.FALSE);
	Arrays.fill(CardsSeen, Boolean.FALSE);
    }

    public void clearHand() {
	Arrays.fill(CardsIHave, Boolean.FALSE);
    }

    public void clearCardInPlay() {
	CardInPlay = -1;
    }

    public void clearCardToPlay() {
	CardToPlay = -1;
    }

    public void addCardIHave(Card c) {
	if (c.getValue().getCardValue() == 14) {
	    CardsIHave[(13 * c.getSuit().getCardSuit())] = true;// So that ace
								// is the first.
	} else {
	    CardsIHave[(13 * c.getSuit().getCardSuit())
		    + (c.getValue().getCardValue()) - 1] = true;// minus
	    // one as the array starts on 0 not 1 and 0 is taken by the ace
	}
    }

    public void removeCardIHave(Card c) {
	if (c.getValue().getCardValue() == 14) {
	    CardsIHave[(13 * c.getSuit().getCardSuit())] = false;// So that ace
	    // is the first.
	} else {
	    CardsIHave[(13 * c.getSuit().getCardSuit())
		    + (c.getValue().getCardValue()) - 1] = false;
	}
    }

    public void addCardSeen(Card c) {
	if (c.getValue().getCardValue() == 14) {
	    CardsSeen[(13 * c.getSuit().getCardSuit())] = true;// So that ace is
	    // the first.
	} else {
	    CardsSeen[(13 * c.getSuit().getCardSuit())
		    + (c.getValue().getCardValue()) - 1] = true;
	}
    }

    public void removeCardSeen(Card c) {
	if (c.getValue().getCardValue() == 14) {
	    CardsSeen[(13 * c.getSuit().getCardSuit())] = false;
	} else {
	    CardsSeen[(13 * c.getSuit().getCardSuit())
		    + (c.getValue().getCardValue()) - 1] = false;
	}
    }

    public void setCardsInPlay(Card c) {
	// input
	if (c.getValue().getCardValue() == 14) {
	    CardInPlay = (13 * c.getSuit().getCardSuit());
	} else {
	    CardInPlay = (13 * c.getSuit().getCardSuit())
		    + (c.getValue().getCardValue()) - 1;
	}
    }

    public void setCardsToPlay(Card c) {
	if (c.getValue().getCardValue() == 14) {
	    CardToPlay = (13 * c.getSuit().getCardSuit());
	} else {
	    CardToPlay = (13 * c.getSuit().getCardSuit())
		    + (c.getValue().getCardValue()) - 1;
	}
    }

    public double[] compute6Hand() {
	network.randomizeWeights();
	double[] results = new double[3];
	setInputs();
	network.calculate();
	int i = 0;
	for (double d : network.getOutput()) {
	    results[i] = d;
	    i++;
	}
	return results;
    }

    public void setGameMode(int gm) {
	gameMode = gm;
    }

    public double[] computeGameMode(int gameMode) {
	network.randomizeWeights();
	this.gameMode = gameMode;
	double[] results = new double[3];
	setInputs();
	network.calculate();
	int i = 0;
	for (double d : network.getOutput()) {
	    results[i] = d;
	    i++;
	}
	return results;
    }

    public double[] computeCurrentInputs() {
	network.randomizeWeights();
	double[] results = new double[3];
	setInputs();
	network.calculate();
	int i = 0;
	for (double d : network.getOutput()) {
	    results[i] = d;
	    i++;
	}

	return results;
    }

    private void setInputs() {
	double[] inputArray = new double[214];
	Arrays.fill(inputArray, 0);
	int i = 0;
	// 0-51 are inputs for cards I currently have.
	for (Boolean input : CardsIHave) {
	    if (input)
		inputArray[i] = 1;
	    i++;
	}
	// 52-103 are inputs for cards I have seen.
	for (Boolean input : CardsSeen) {
	    if (input)
		inputArray[i] = 1;
	    i++;
	}
	// 104-155 are inputs for cards in play.
	if (CardInPlay > 0)
	    inputArray[104 + CardInPlay] = 1;
	// 156-207 are inputs for cards I currently have.
	if (CardToPlay > 0)
	    inputArray[156 + CardToPlay] = 1;
	switch (gameMode) {
	case 0:
	case 14:
	    inputArray[208] = 1;
	    break;
	case 15:
	case 1:
	    inputArray[209] = 1;
	    break;
	case 2:
	case 11:
	    inputArray[210] = 1;
	    break;
	case 10:
	    inputArray[211] = 1;
	    break;
	case 12:
	    inputArray[212] = 1;
	    break;
	case 13:
	    inputArray[213] = 1;
	    break;
	default:
	    System.out
		    .println("\nThere has been an error is the nn input switch.");
	    System.out.println("GameMode = " + gameMode);
	    break;
	}
	// Game mode inputs are as follows: 0 = misere, 1 = no trump, 2 = spades
	// 11 = spades, 10 = clubs, 12 = hearts, 13 = diamonds
	// The choice game modes values come form the enum
	network.setInput(inputArray);
    }
    
    public double[] getOutput(){
	return network.getOutput();
    }
    
    public double[] getInput(){
	double[] inputs = new double[214];
	int i = 0;
	for(Neuron n : network.getInputNeurons()){
	    inputs[i] = n.getNetInput();
	    i++;
	}
	return inputs;
    }

    public void printNetworkInputLables() {
	Neuron[] rons = network.getInputNeurons();
	int i = 0;
	for (Neuron k : rons) {
	    System.out.print("(" + i + ", " + k.getNetInput() + "), ");
	    i++;
	}
	System.out.println();
    }
}