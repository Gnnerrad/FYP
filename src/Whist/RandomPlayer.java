package Whist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import NN.NN;
import NN.NNSettings;
import dataStructures.IOTuple;

public class RandomPlayer extends Player {
    private ArrayList<Card> hand;
    private NN neuralNetwork;
    private ArrayList<IOTuple> neuralNetworkData;
    protected int playerNumber;

    public RandomPlayer(int playerNumber, String nn, ArrayList<IOTuple> neuralNetworkData) {
	super(playerNumber, nn, neuralNetworkData);
	hand = new ArrayList<>();
	neuralNetwork = new NN(nn);
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

    public boolean chooseWhoGoesFirst() {
//	NNSettings.testCount++;
	boolean b = new Random().nextBoolean();
	neuralNetwork.setTurn(b);
	neuralNetworkData.add(new IOTuple(3, neuralNetworkData.size(), neuralNetwork.getInput(), neuralNetwork.computeCurrentInputs()));
	return b;
    }

    public Card playCard(ArrayList<Card> legalMoves, Card otherPlayersCard) {
	if (otherPlayersCard != null)
	    neuralNetwork.setCardsInPlay(otherPlayersCard);
	Card bestCard = hand.get(ThreadLocalRandom.current().nextInt(0, hand.size()));
	hand.remove(bestCard);
	neuralNetwork.setCardsToPlay(bestCard);
	neuralNetwork.removeCardIHave(bestCard);
	neuralNetworkData.add(new IOTuple(4, neuralNetworkData.size(), neuralNetwork.getInput(), neuralNetwork.computeCurrentInputs()));
	neuralNetwork.clearCardInPlay();
	return bestCard;
    }

    public ArrayList<Card> discardSix() {
	ArrayList<Card> bestHand = new ArrayList<Card>();
	HashSet<Card> set = new HashSet<Card>();
	while (set.size() < 6) {
	    set.add(hand.get(ThreadLocalRandom.current().nextInt(0, hand.size())));
	}
	neuralNetwork.clearHand();
	for (Card c : hand) {
	    if (set.contains(c)) {
		neuralNetwork.addCardIHave(c);
		bestHand.add(c);
	    } else {
		addCardSeen(c);
	    }
	}
	neuralNetworkData.add(new IOTuple(1, neuralNetworkData.size(), neuralNetwork.getInput(), neuralNetwork.computeCurrentInputs()));
	hand = bestHand;
	return bestHand;
    }

    public boolean acceptOrNext(Card c, boolean forcedToTake) {
	if (forcedToTake) {
	    addCardToHand(c);
	    return true;
	} else {
	    int i = ThreadLocalRandom.current().nextInt(0, 1 + 1);
	    if (i == 0) {
		addCardSeen(c);
		neuralNetworkData.add(new IOTuple(2, neuralNetworkData.size(), neuralNetwork.getInput(), neuralNetwork.computeCurrentInputs()));
		return false;
	    } else {
		addCardToHand(c);
		neuralNetworkData.add(new IOTuple(2, neuralNetworkData.size(), neuralNetwork.getInput(), neuralNetwork.computeCurrentInputs()));
		return true;
	    }
	}
    }

    public int chooseGamemode(ArrayList<Integer> gameModesAvailable) {
	int bestGameMode = -1,gm, i = ThreadLocalRandom.current().nextInt(0, gameModesAvailable.size());
	gm = gameModesAvailable.get(i);
	switch (gm) {
	case 0:
	    bestGameMode = 0;
	    break;
	case 1:
	    bestGameMode = 1;
	    break;
	case 2:
	    bestGameMode = 2;
	    break;
	case 3:
	    bestGameMode = 10;
	    break;
	case 4:
	    bestGameMode = 11;
	    break;
	case 5:
	    bestGameMode = 12;
	    break;
	case 6:
	    bestGameMode = 13;
	    break;
	case 7:
	    bestGameMode = 14;
	    break;
	case 8:
	    bestGameMode = 15;
	    break;
	default:
	    while (true) {
		System.out.println("Random is not working................." + i);
	    }
	}
	neuralNetwork.setGameMode(bestGameMode);
	neuralNetworkData.add(new IOTuple(0, neuralNetworkData.size(), neuralNetwork.getInput(), neuralNetwork.computeCurrentInputs()));
	return bestGameMode;
    }
}
