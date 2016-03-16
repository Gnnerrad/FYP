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
import java.util.concurrent.ThreadLocalRandom;

import dataStructures.IOTuple;

public class Player {
    private ArrayList<Card> hand;
    private NN neuralNetwork;
    public ArrayList<IOTuple> neuralNetworkData;
    protected int playerNumber;

    public Player(int playerNumber, String nn, ArrayList<IOTuple> neuralNetworkData) {
	hand = new ArrayList<>();
	this.playerNumber = playerNumber;
	neuralNetwork = new NN(nn);
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
    
    public void setTurn(boolean b){
	neuralNetwork.setTurn(b);
    }

    public boolean chooseWhoGoesFirst() {
	neuralNetwork.setTurn(true);
	double[] output = neuralNetwork.computeCurrentInputs();
	double trueResult = calculateResult(output);
	neuralNetwork.setTurn(false);
	output = neuralNetwork.computeCurrentInputs();
	double falseResult = calculateResult(output);
	if (trueResult > falseResult)
	    return true;
	else
	    return false;

    }

    public Card playCard(ArrayList<Card> legalMoves, Card otherPlayersCard) {
	if (otherPlayersCard != null)
	    neuralNetwork.setCardsInPlay(otherPlayersCard);
	double currentResult, bestResult = -100;
	double[] currentOutput, bestOutput = new double[NNSettings.outputSize], bestInput = new double[NNSettings.inputSize];
	Card bestCard = null;
	for (Card handCard : legalMoves) {
	    neuralNetwork.setCardsToPlay(handCard);// sets the card about to be
	    // played. This is unset in
	    // the
	    neuralNetwork.removeCardIHave(handCard);
	    currentOutput = neuralNetwork.computeCurrentInputs();
	    currentResult = calculateResult(currentOutput);
	    if (currentResult > bestResult) {
		bestCard = handCard;
		bestResult = currentResult;
		bestOutput = currentOutput;
		bestInput = neuralNetwork.getInput();
	    }
	    neuralNetwork.addCardIHave(handCard);
	}
	hand.remove(bestCard);
	neuralNetworkData.add(new IOTuple(bestInput, bestOutput));
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
	double[] currentOutput, bestOutput = new double[NNSettings.outputSize], bestInput = new double[NNSettings.inputSize];

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
		    currentOutput = neuralNetwork.computeCurrentInputs();
		    currentResult = calculateResult(currentOutput);
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
	neuralNetworkData.add(new IOTuple(bestInput, bestOutput));
	return bestHand;
    }

    public boolean acceptCardChoice(Card c, boolean forcedToTake) {
	// Should I store nn results for forced card takes.
	if (forcedToTake) {
	    addCardToHand(c);
	    return true;
	} else {
	    neuralNetwork.addCardIHave(c);
	    double[] acceptInputs = neuralNetwork.getInput();
	    double[] acceptResults = neuralNetwork.computeCurrentInputs();
	    neuralNetwork.removeCardIHave(c);

	    neuralNetwork.addCardSeen(c);
	    double[] passInputs = neuralNetwork.getInput();
	    double[] passResults = neuralNetwork.computeCurrentInputs();
	    neuralNetwork.removeCardSeen(c);

	    double acceptResult = calculateResult(acceptResults);
	    double passResutlt = calculateResult(passResults);

	    if (acceptResult > passResutlt) {
		addCardToHand(c);
		neuralNetworkData.add(new IOTuple(acceptInputs, acceptResults));
		return true;
	    } else {
		addCardSeen(c);
		neuralNetworkData.add(new IOTuple(passInputs, passResults));
		return false;
	    }
	}
    }

    public int chooseGamemode(ArrayList<Integer> gameModesAvailable) {
	int bestGameMode = -1;
	double currentResult, bestResult = -100;
	double[] results = new double[NNSettings.outputSize], bestOutput = new double[NNSettings.outputSize], bestInput = new double[216];
	for (int gm : gameModesAvailable) {
	    if (gm == 3) {// if choice check every game mode.
		for (int i = 0; i < 4; i++) {
		    results = neuralNetwork.computeGameMode(i + 10);
		    currentResult = calculateResult(results);
		    if (currentResult > bestResult) {
			bestGameMode = i + 10;
			bestResult = currentResult;
			bestOutput = results;
			bestInput = neuralNetwork.getInput();
		    }
		}
		results = neuralNetwork.computeGameMode(0);
		currentResult = calculateResult(results);
	    } else {
		results = neuralNetwork.computeGameMode(gm);
		currentResult = calculateResult(results);
		if (currentResult > bestResult) {
		    bestGameMode = gm;
		    bestResult = currentResult;
		    bestOutput = results;
		    bestInput = neuralNetwork.getInput();
		}
	    }
	}
	neuralNetwork.setGameMode(bestGameMode);
	neuralNetworkData.add(new IOTuple(bestInput, bestOutput));
	return bestGameMode;
    }

    private double calculateResult(double[] output) {
	// 0=big win, 1=small win, 2=draw, 3=small loss, 4=big loss
	return (((2 * output[0] + output[2]) / (2 * output[4] + 1)) + ((2 * output[1] + output[2]) / (2 * output[3] + 1))) - (((2 * output[3] + output[2]) / (2 * output[1] + 1)) + ((2 * output[4] + output[2]) / (2 * output[0] + 1)));
    }

    public void printHand() {
	Iterator<Card> handIT = hand.iterator();
	while (handIT.hasNext()) {
	    Card card = handIT.next();
	    System.out.println(card.toString());
	}
    }
}
