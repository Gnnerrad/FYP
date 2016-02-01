package objects;

import java.util.*;

import objects.Card.*;

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

    public void printDeck() {
	Iterator<Card> deckIT = deck.iterator();
	while (deckIT.hasNext()) {
	    Card card = deckIT.next();
	    System.out.println(card.getValue() + " of " + card.getSuit());
	}
    }
}