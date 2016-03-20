package NN;

import gameModes.BasicModeLayout;
import gameModes.MisereGameMode;
import gameModes.TrumpGameMode;

import java.util.ArrayList;
import java.util.Arrays;

import objects.Card;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;

public class NN {
	// NeuralNetwork network = NeuralNetwork.load("lib/Just Card neurons.nnet");
	private NeuralNetwork network;
	private Boolean[] CardsIHave = new Boolean[52];
	private Boolean[] CardsSeen = new Boolean[52];
	private int gameMode = -1, CardInPlay = -1, CardToPlay = -1;
	private boolean myTurn = false;

	public NN(String nn) {
		network = NeuralNetwork.load(nn);
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

	public void setTurn(boolean b) {
		myTurn = b;
	}

	public void addCardIHave(Card c) {
		if (c.getValue().getCardValue() == 14) {
			CardsIHave[(13 * c.getSuit().getCardSuit())] = true;// So that ace
			// is the first.
		} else {
			CardsIHave[(13 * c.getSuit().getCardSuit()) + (c.getValue().getCardValue()) - 1] = true;// minus
			// one as the array starts on 0 not 1 and 0 is taken by the ace
		}
	}

	public void removeCardIHave(Card c) {
		if (c.getValue().getCardValue() == 14) {
			CardsIHave[(13 * c.getSuit().getCardSuit())] = false;// So that ace
			// is the first.
		} else {
			CardsIHave[(13 * c.getSuit().getCardSuit()) + (c.getValue().getCardValue()) - 1] = false;
		}
	}

	public void addCardSeen(Card c) {
		if (c.getValue().getCardValue() == 14) {
			CardsSeen[(13 * c.getSuit().getCardSuit())] = true;// So that ace is
			// the first.
		} else {
			CardsSeen[(13 * c.getSuit().getCardSuit()) + (c.getValue().getCardValue()) - 1] = true;
		}
	}

	public void removeCardSeen(Card c) {
		if (c.getValue().getCardValue() == 14) {
			CardsSeen[(13 * c.getSuit().getCardSuit())] = false;
		} else {
			CardsSeen[(13 * c.getSuit().getCardSuit()) + (c.getValue().getCardValue()) - 1] = false;
		}
	}

	public void setCardsInPlay(Card c) {
		if (c.getValue().getCardValue() == 14) {
			CardInPlay = (13 * c.getSuit().getCardSuit());
		} else {
			CardInPlay = (13 * c.getSuit().getCardSuit()) + (c.getValue().getCardValue()) - 1;
		}
	}

	public void setCardsToPlay(Card c) {
		if (c.getValue().getCardValue() == 14) {
			CardToPlay = (13 * c.getSuit().getCardSuit());
		} else {
			CardToPlay = (13 * c.getSuit().getCardSuit()) + (c.getValue().getCardValue()) - 1;
		}
	}

	public void setGameMode(int gm) {
		gameMode = gm;
	}

	public double[] computeGameMode(int gameMode) {
		this.gameMode = gameMode;
		double[] results = new double[NNSettings.outputSize];
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
		double[] results = new double[NNSettings.outputSize];
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
		double[] inputArray = new double[NNSettings.inputSize];
		Arrays.fill(inputArray, 0);
		if (myTurn) {
			inputArray[0] = 1;
			inputArray[1] = 0;
		} else {
			inputArray[0] = 0;
			inputArray[1] = 1;
		}
		int i = 2;
		// 2-53 are inputs for cards I currently have.
		for (Boolean input : CardsIHave) {
			if (input)
				inputArray[i] = 1;
			i++;
		}
		// 54-105 are inputs for cards I have seen.
		for (Boolean input : CardsSeen) {
			if (input)
				inputArray[i] = 1;
			i++;
		}
		// 106-157 are inputs for cards in play.
		if (CardInPlay > 0) {
			inputArray[106 + CardInPlay] = 1;
		}
		// 158-209 are inputs for cards I play
		if (CardToPlay > 0) {
			inputArray[158 + CardToPlay] = 1;
		}
		switch (gameMode) {
		// 0 = misere
		// 1 = no trump
		// 2 = spades
		// 3 = choice => 10 = clubs, 11 = spades, 12 = hearts, 13 = diamonds, 14
		// = misere, 15 = no trump
		case 0:
		case 14:
			inputArray[210] = 1;
			break;
		case 1:
		case 15:
			inputArray[211] = 1;
			break;
		case 2:
		case 11:
			inputArray[212] = 1;
			break;
		case 10:
			inputArray[213] = 1;
			break;
		case 12:
			inputArray[214] = 1;
			break;
		case 13:
			inputArray[215] = 1;
			break;
		default:
			System.out.println("\nThere has been an error is the nn input switch.");
			System.out.println("GameMode = " + gameMode);
			break;
		}
		// Game mode inputs are as follows: 0 = misere, 1 = no trump, 2 = spades
		// 11 = spades, 10 = clubs, 12 = hearts, 13 = diamonds
		// The choice game modes values come form the enum
		network.setInput(inputArray);
	}

	public double[] getOutput() {
		double[] results = new double[NNSettings.outputSize];
		int i = 0;
		for (double d : network.getOutput()) {
			results[i] = d;
			i++;
		}

		return results;
	}

	public double[] getInput() {
		setInputs();
		double[] inputs = new double[NNSettings.inputSize];
		int i = 0;
		for (Neuron n : network.getInputNeurons()) {
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