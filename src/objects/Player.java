//To-Do
/*
 * Tidy up code.
 * Add acc or next nnData logging.
 * 
 * 
 */

package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import javax.naming.spi.DirStateFactory.Result;

import nnTest.NN;
import objects.Card.CardValue;
import objects.Card.Suit;
import td.temporalDifference;

public class Player {
    private ArrayList<Card> hand;
    private NN neuralNetwork = new NN();
    private ArrayList<IOTuple> neuralNetworkData;
    protected int playerNumber;

    public Player(int playerNumber, ArrayList<IOTuple> neuralNetworkData) {
	hand = new ArrayList<>();
	this.playerNumber = playerNumber;
	this.neuralNetworkData = neuralNetworkData;
    }

    public int handSize() {
	return hand.size();
    }

    public void acceptGameMode(int gm) {
	neuralNetwork.setGameMode(gm);
    }

    public ArrayList<Card> getHand() {
	return hand;
    }

    public void addCardToHand(Card card) {
	hand.add(card);
	neuralNetwork.addCardIHave(card);
    }

    public void addCardSeen(Card c) {
	neuralNetwork.addCardSeen(c);
    }

    public Card playCard(ArrayList<Card> legalMoves, Card otherPlayersCard) {
	if (otherPlayersCard != null)
	    neuralNetwork.setCardsInPlay(otherPlayersCard);
	double currentResult, bestResult = -100;
	double[] currentOutput, bestOutput = new double[3], bestInput = new double[214];
	Card bestCard = null;
	for (Card handCard : legalMoves) {
	    neuralNetwork.setCardsToPlay(handCard);// sets the card about to be
						   // played. This is unset in
						   // the
	    neuralNetwork.removeCardIHave(handCard);
	    currentOutput = neuralNetwork.computeCurrentInputs();
	    currentResult = ((2 * currentOutput[0] + currentOutput[1]) / (2 * currentOutput[2] + 1)) - ((2 * currentOutput[2] + currentOutput[1]) / (2 * currentOutput[0] + 1));
	    if (currentResult > bestResult) {
		bestCard = handCard;
		bestResult = currentResult;
		bestOutput = currentOutput;
		bestInput = neuralNetwork.getInput();
	    }
	    neuralNetwork.addCardIHave(handCard);
	}
	// System.out.println("best card = " + bestCard);
	hand.remove(bestCard);
	neuralNetworkData.add(new IOTuple(playerNumber, bestInput, bestOutput));
	return bestCard;
    }

    public ArrayList<Card> discardSix() {
	// N choose K algorithm from:
	// http://hmkcode.com/calculate-find-all-possible-combinations-of-an-array-using-java/
	HashMap<ArrayList<Card>, double[]> handResults = new HashMap<ArrayList<Card>, double[]>();
	int K = 6;

	if (K > handSize()) {
	    System.out.println("Invalid input, K > N");
	    return null;
	}

	int combination[] = new int[K];
	int r = 0;
	int index = 0;
	ArrayList<Card> bestHand = new ArrayList<Card>();
	double bestResult = -100;
	double currentResult;
	double[] currentOutput, bestOutput = new double[3], bestInput = new double[214];

	while (r >= 0) {
	    if (index <= (handSize() + (r - K))) {
		combination[r] = index;
		if (r == K - 1) {
		    ArrayList<Card> hand = new ArrayList<Card>();
		    neuralNetwork.clearHand();
		    for (int g : combination) {
			neuralNetwork.addCardIHave(this.hand.get(g));
			hand.add(this.hand.get(g));
		    }
		    currentOutput = neuralNetwork.compute6Hand();
		    currentResult = ((2 * currentOutput[0] + currentOutput[1]) / (2 * currentOutput[2] + 1)) - ((2 * currentOutput[2] + currentOutput[1]) / (2 * currentOutput[0] + 1));
		    if (currentResult > bestResult) {
			bestHand = hand;
			bestResult = currentResult;
			bestOutput = currentOutput;
			bestInput = neuralNetwork.getInput();
		    }
		    index++;
		} else {
		    index = combination[r] + 1;
		    r++;
		}
	    } else {
		r--;
		if (r > 0)
		    index = combination[r] + 1;
		else
		    index = combination[0] + 1;
	    }
	}
	this.hand.removeAll(bestHand);
	for (Card c : this.hand)
	    addCardSeen(c);
	this.hand = bestHand;
	neuralNetworkData.add(new IOTuple(playerNumber, bestInput, bestOutput));
	return bestHand;
    }

    public boolean acceptCardChoice(Card c, boolean forcedToTake) {
	// Should I store nn results for forced card takes.
	if (forcedToTake) {
	    return true;
	} else {
	    neuralNetwork.addCardIHave(c);
	    double[] acceptInputs = neuralNetwork.getInput();
	    double[] acceptResults = neuralNetwork.computeCurrentInputs();
	    double[] acceptOutput = neuralNetwork.getOutput();
	    neuralNetwork.removeCardIHave(c);

	    neuralNetwork.addCardSeen(c);
	    double[] passInputs = neuralNetwork.getInput();
	    double[] passResults = neuralNetwork.computeCurrentInputs();
	    double[] passOutput = neuralNetwork.getOutput();
	    neuralNetwork.removeCardSeen(c);

	    double acceptResult = ((2 * acceptResults[0] + acceptResults[1]) / (2 * acceptResults[2] + 1)) - ((2 * acceptResults[2] + acceptResults[1]) / (2 * acceptResults[0] + 1));

	    double passResutlt = ((2 * passResults[0] + passResults[1]) / (2 * passResults[2] + 1)) - ((2 * passResults[2] + passResults[1]) / (2 * passResults[0] + 1));

	    if (acceptResult > passResutlt) {
		neuralNetworkData.add(new IOTuple(playerNumber, acceptInputs, acceptOutput));
		return true;
	    } else {
		neuralNetworkData.add(new IOTuple(playerNumber, passInputs, passOutput));
		neuralNetwork.addCardSeen(c);
		return false;
	    }
	}
    }

    public int chooseGamemode(ArrayList<Integer> gameModesAvailable) {
	int bestGameMode = -1;
	double[] results;
	double currentResult, bestResult = -100;
	double[] currentOutput, bestOutput = new double[3], bestInput = new double[214];
	for (int gm : gameModesAvailable) {
	    if (gm == 3) {// if choice check every game mode.
		for (int i = 0; i < 4; i++) {
		    results = neuralNetwork.computeGameMode(i + 10);
		    currentResult = ((2 * results[0] + results[1]) / (2 * results[2] + 1)) - ((2 * results[2] + results[1]) / (2 * results[0] + 1));
		    if (currentResult > bestResult) {
			bestGameMode = i + 10;
			bestResult = currentResult;
			bestOutput = results;
			bestInput = neuralNetwork.getInput();
		    }
		}
		results = neuralNetwork.computeGameMode(0);
		currentResult = ((2 * results[0] + results[1]) / (2 * results[2] + 1)) - ((2 * results[2] + results[1]) / (2 * results[0] + 1));
		if (currentResult > bestResult) {
		    bestGameMode = 0;
		    bestResult = currentResult;
		    bestOutput = results;
		    bestInput = neuralNetwork.getInput();
		}
		results = neuralNetwork.computeGameMode(1);
		currentResult = ((2 * results[0] + results[1]) / (2 * results[2] + 1)) - ((2 * results[2] + results[1]) / (2 * results[0] + 1));
		if (currentResult > bestResult) {
		    bestGameMode = 1;
		    bestResult = currentResult;
		    bestOutput = results;
		    bestInput = neuralNetwork.getInput();
		}
	    } else {
		results = neuralNetwork.computeGameMode(gm);
		currentResult = ((2 * results[0] + results[1]) / (2 * results[2] + 1)) - ((2 * results[2] + results[1]) / (2 * results[0] + 1));
		if (currentResult > bestResult) {
		    bestGameMode = gm;
		    bestResult = currentResult;
		    bestOutput = results;
		    bestInput = neuralNetwork.getInput();
		}
	    }
	}
	System.out.println("best mode = " + bestGameMode);
	neuralNetwork.setGameMode(bestGameMode);
	neuralNetworkData.add(new IOTuple(playerNumber, bestInput, bestOutput));
	return bestGameMode;
    }

    public void printData() {
	// td.printData();
    }

    public void printHand() {
	Iterator<Card> handIT = hand.iterator();
	while (handIT.hasNext()) {
	    Card card = handIT.next();
	    System.out.println(card.toString());
	}
    }

    // public void setGameOutcome(int activeOutput){
    // td.setGameOutcomeP1(activeOutput);
    // }
}
