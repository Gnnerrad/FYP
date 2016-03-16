package objects;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import NN.NNSettings;
import dataStructures.IOTuple;
import objects.Card.CardValue;
import objects.Card.Suit;

public class Deck {

    private ArrayList<Card> deck;

    public Deck() {
	this.deck = new ArrayList<Card>();
	for (int i = 0; i < 4; i++) {
	    for (int j = 0; j < 13; j++) {
		CardValue value = CardValue.values()[j];
		Card card = new Card(Suit.values()[i], value);
		this.deck.add(card);
	    }
	}
    }

    public ArrayList<Card> getDeck() {
	return deck;
    }

    public void shuffle() {
	Collections.shuffle(deck);
    }

    public int size() {
	return deck.size();
    }

    public Card dealCard() {
	return deck.remove(0);
    }

    public void write(String file, boolean newGame) {
	try (BufferedWriter out = new BufferedWriter(new FileWriter(file, true))) {
	    if (newGame)
		out.write(Boolean.toString(newGame));
		out.write("\n");
	    Iterator<Card> deckIT = deck.iterator();
	    while (deckIT.hasNext()) {
		Card card = deckIT.next();
		out.write(card.getValue().getCardValue() + " " + card.getSuit().getCardSuit() + ",");
	    }
	    out.write("\n");
	    out.close();
	} catch (IOException e) {
	}
    }

    public void printDeck() {
	Iterator<Card> deckIT = deck.iterator();
	while (deckIT.hasNext()) {
	    Card card = deckIT.next();
	    System.out.println(card.getValue() + " of " + card.getSuit());
	}
    }
}