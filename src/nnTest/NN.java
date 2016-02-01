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
    NeuralNetwork network = NeuralNetwork.load("lib/NN.nnet");
    Boolean[] CardsIHave = new Boolean[52];
    Boolean[] CardsSeen = new Boolean[52];
    int gameMode;
    int CardInPlay;
    int CardToPlay;
    Deck deck = new Deck();

    public NN() {
	Arrays.fill(CardsIHave, Boolean.FALSE);
	Arrays.fill(CardsSeen, Boolean.FALSE);
    }

    // public static void main(String[] args) {
    // NN t = new NN();
    // int i = 0;
    // for (Card c : t.deck.getDeck()) {
    // // int val =
    // (13*c.getSuit().getCardSuit())+(c.getValue().getCardValue());
    // //// System.out.println(i + "\t" + val + "\t" + c.toString());
    // // System.out.println(i + "\t" + t.CardsIHave[i]);
    // if (c.getValue().getCardValue() == 14) {
    // System.out.println(i + "\t" + (13 * c.getSuit().getCardSuit()));
    // }
    // else {
    // System.out.println(i + "\t" + (13 * c.getSuit().getCardSuit() +
    // c.getValue().getCardValue() - 1));
    // }
    // i++;
    // }
    // // t.organiseLables();
    // // t.printNetworkInputLables();
    // // t.network.save("lib/TestSavingNetwork");
    // }

    public void clearHand() {
	Arrays.fill(CardsIHave, Boolean.FALSE);
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

    public void addCardsInPlay(Card c) {
	// input
	if (c.getValue().getCardValue() == 14) {
	    CardInPlay = (13 * c.getSuit().getCardSuit());
	} else {
	    CardInPlay = (13 * c.getSuit().getCardSuit())
		    + (c.getValue().getCardValue()) - 1;
	}
    }

    public void addCardsToPlay(Card c) {
	if (c.getValue().getCardValue() == 14) {
	    CardToPlay = (13 * c.getSuit().getCardSuit());
	} else {
	    CardToPlay = (13 * c.getSuit().getCardSuit())
		    + (c.getValue().getCardValue()) - 1;
	}
    }

    public double[] compute6Hand() {
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
	// network.randomizeWeights();
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
	int i = 0;
	for (Boolean input : CardsIHave) {
	    if (input) {
		inputArray[i] = 1;
	    } else {
		inputArray[i] = 0;
	    }
	    i++;
	}
	// i should = 52 - 1
	for (Boolean input : CardsSeen) {
	    if (input) {
		inputArray[i] = 1;
	    } else {
		inputArray[i] = 0;
	    }
	    i++;
	}
	// i should = 104 - 1
	for (i = i; i < i + CardInPlay; i++) {
	    inputArray[i] = 0;
	}
	inputArray[i + 1] = 1;
	for (i = i + 2; i < 156; i++) {
	    inputArray[i] = 0;
	}
	// i should = 156 - 1
	System.out.println("a\t"+ i +"\t" + CardToPlay);
	for (i = i; i < i + CardToPlay; i++) {
	    inputArray[i] = 0;
	}
	System.out.println("b\t"+ i +"\t" + CardToPlay);
	inputArray[i + 1] = 1;
	for (i = i + 2; i < 213; i++) {
	    inputArray[i] = 0;
	}
	switch (gameMode) {
	case 0:
	    inputArray[208] = 1;
	    break;
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

    private void calculate() {

    }

    public void printNetworkInputLables() {
	setInputs();
	network.calculate();
	Neuron[] rons = network.getInputNeurons();
	for (Neuron k : rons) {
	    System.out.print(k.getNetInput() + ", ");
	}
	System.out.println();
    }
    // private void go()
    // {
    // calculate(1,0,0);
    // calculate(0,1,0);
    // calculate(0,0,1);
    // }

    // private void calculate(double... input)
    // {
    // network.setInput(input);
    // network.calculate();
    // double[] output = network.getOutput();
    // Double answer = output[0];
    // System.out.println(answer);
    // }
}