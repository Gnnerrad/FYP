package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import nnTest.NN;
import objects.Card.CardValue;
import objects.Card.Suit;

public class Player {
    private ArrayList<Card> hand;
    private NN neuralNetwork = new NN();
    private int TEST = 1;

    public Player() {
	hand = new ArrayList<>();
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
	HashMap<Card, double[]> playResult = new HashMap<Card, double[]>();
	for (Card handCard : legalMoves) {
	    neuralNetwork.setCardsToPlay(handCard);// sets the card about to be
						   // played. This is unset in
						   // the
	    neuralNetwork.removeCardIHave(handCard);
	    playResult.put(handCard, neuralNetwork.computeCurrentInputs());
	    neuralNetwork.addCardIHave(handCard);
	}
	double[] results;
	double currentResult, bestResult = -100;
	Card bestCard = null;
	for (Card cardResult : playResult.keySet()) {
	    results = playResult.get(cardResult);// 0 == win, 1 == draw, 2 ==
						 // loose
	    currentResult = ((2 * results[0] + results[1]) / (2 * results[2] + 1))
		    - ((2 * results[2] + results[1]) / (2 * results[0] + 1));
	    if (currentResult > bestResult) {
		bestCard = cardResult;
		bestResult = currentResult;
	    }
	}
	// System.out.println("best card = " + bestCard);
	hand.remove(bestCard);
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
		    handResults.put(hand, neuralNetwork.compute6Hand());
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

	ArrayList<Card> bestHand = new ArrayList<Card>();
	double bestResult = -100;
	double currentResult;
	for (ArrayList<Card> hand : handResults.keySet()) {
	    double[] result = handResults.get(hand);// 0 == win, 1 == draw, 2 ==
						    // loose
	    currentResult = ((2 * result[0] + result[1]) / (2 * result[2] + 1))
		    - ((2 * result[2] + result[1]) / (2 * result[0] + 1));
	    if (currentResult > bestResult) {
		bestHand = hand;
		bestResult = currentResult;
	    }
	}
	// current hand(12 cards) - best hand(6 cards) = Seen but discarding
	// cards
	this.hand.removeAll(bestHand);
	for (Card c : this.hand)
	    addCardSeen(c);
	this.hand = bestHand;
	return bestHand;
    }

    public boolean acceptCardChoice(Card c, boolean forcedToTake) {
	if (forcedToTake) {
	    return true;
	} else {
	    neuralNetwork.addCardIHave(c);
	    double[] acceptResults = neuralNetwork.computeCurrentInputs();
	    neuralNetwork.removeCardIHave(c);

	    neuralNetwork.addCardSeen(c);
	    double[] passResults = neuralNetwork.computeCurrentInputs();
	    neuralNetwork.removeCardSeen(c);

	    double acceptResult = ((2 * acceptResults[0] + acceptResults[1]) / (2 * acceptResults[2] + 1))
		    - ((2 * acceptResults[2] + acceptResults[1]) / (2 * acceptResults[0] + 1));

	    double passResutlt = ((2 * passResults[0] + passResults[1]) / (2 * passResults[2] + 1))
		    - ((2 * passResults[2] + passResults[1]) / (2 * passResults[0] + 1));

	    if (acceptResult > passResutlt) {
		return true;
	    } else {
		neuralNetwork.addCardSeen(c);
		return false;
	    }
	}
    }

    public int chooseGamemode(ArrayList<Integer> gameModesAvailable) {
	HashMap<Integer, double[]> gmResults = new HashMap<Integer, double[]>();
	int bestGameMode = -1;
	double bestResult = -1000;
	double currentResult;
	double[] results;
	for (int gm : gameModesAvailable) {
	    if (gm == 3) {
		for (int i = 0; i < 4; i++) {
		    gmResults
			    .put(i + 10, neuralNetwork.computeGameMode(i + 10));
		}
		gmResults.put(14, neuralNetwork.computeGameMode(0));
		gmResults.put(15, neuralNetwork.computeGameMode(1));
	    } else {
		gmResults.put(gm, neuralNetwork.computeGameMode(gm));
	    }
	}

	for (int gameMode : gmResults.keySet()) {
	    results = gmResults.get(gameMode);// 0 == win, 1 == draw, 2 == loose
	    currentResult = ((2 * results[0] + results[1]) / (2 * results[2] + 1))
		    - ((2 * results[2] + results[1]) / (2 * results[0] + 1));
	    if (currentResult > bestResult) {
		bestGameMode = gameMode;
		bestResult = currentResult;
	    }
	}
	System.out.println("best mode = " + bestGameMode);
	neuralNetwork.setGameMode(bestGameMode);
	return bestGameMode;
    }

    public void printHand() {
	Iterator<Card> handIT = hand.iterator();
	while (handIT.hasNext()) {
	    Card card = handIT.next();
	    System.out.println(card.toString());
	}
    }

}
