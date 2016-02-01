package nnTest;

import objects.Card;
import objects.Deck;

public class NNTester {
    private NN neuralNetwork = new NN();
    private Deck deck = new Deck();

    public static void main(String[] args) {
	NNTester test = new NNTester();
	test.deck.shuffle();
	Card c = test.deck.dealCard();
	System.out.println(c);
	test.setCardToPlay(c);

    }

    private void testCardsIHave() {
	for (Card c : deck.getDeck()) {
	    neuralNetwork.addCardIHave(c);
	}
	neuralNetwork.computeCurrentInputs();
	neuralNetwork.printNetworkInputLables();
    }

    private void testCardsSeen() {
	for (Card c : deck.getDeck()) {
	    neuralNetwork.addCardSeen(c);
	}
	neuralNetwork.computeCurrentInputs();
	neuralNetwork.printNetworkInputLables();
    }

    private void setCardInPlay(Card c) {
	neuralNetwork.setCardsInPlay(c);
	neuralNetwork.computeCurrentInputs();
	neuralNetwork.printNetworkInputLables();
    }

    private void setCardToPlay(Card c) {
	neuralNetwork.setCardsToPlay(c);
	neuralNetwork.computeCurrentInputs();
	neuralNetwork.printNetworkInputLables();
    }

}
