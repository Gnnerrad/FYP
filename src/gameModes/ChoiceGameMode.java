package gameModes;

import java.util.Scanner;

import objects.Card;
import objects.Deck;
import objects.Player;

public class ChoiceGameMode extends BasicModeLayout {
    public int trumpSuit; // CLUBS(0), SPADES(1), HEARTS(2), DIAMONDS(3)

    public ChoiceGameMode(Player p1, Player p2, boolean player1turn, Deck deck,
	    int trumpSuit) {
	super(p1, p2, player1turn, deck);
	this.trumpSuit = trumpSuit;
    }

    public boolean playMode() {
	setUpHands();

	Card card1, card2;
	while (player1.handSize() > 0 && player2.handSize() > 0) {
	    if (player1turn) {
		card1 = player1.playCard(
			lmg.legalMoves(player1.getHand(), nullCard, false, 0),
			nullCard);
		card2 = player2.playCard(
			lmg.legalMoves(player2.getHand(), card1, false, 0),
			card1);

		if (calculateWinner(card1, card2)) {
		    player1Tricks++;
		} else {
		    player2Tricks++;
		    player1turn = false;
		}
	    } else {
		card1 = player2.playCard(
			lmg.legalMoves(player2.getHand(), nullCard, false, 0),
			nullCard);
		card2 = player1.playCard(
			lmg.legalMoves(player1.getHand(), card1, false, 0),
			card1);

		if (calculateWinner(card1, card2)) {
		    player2Tricks++;
		} else {
		    player1Tricks++;
		    player1turn = true;
		}
	    }
	}
	System.out.println("player"
		+ ((player1Tricks > player2Tricks) ? 1 : 2)
		+ " wins, "
		+ ((player1Tricks > player2Tricks) ? player1Tricks
			: player2Tricks)
		+ " tricks to "
		+ ((player1Tricks < player2Tricks) ? player1Tricks
			: player2Tricks) + " tricks.");
	return ((player1Tricks > player2Tricks) ? true : false);
    }

    private boolean calculateWinner(Card card1, Card card2) {
	if (card1.getSuit().getCardSuit() == trumpSuit
		&& card2.getSuit().getCardSuit() != trumpSuit) {
	    return true; // player1 wins by trump
	} else if (card1.getSuit().getCardSuit() != trumpSuit
		&& card2.getSuit().getCardSuit() == trumpSuit) {
	    return false; // player2 wins by trump
	} else {
	    return (card1.getValue().getCardValue() > card2.getValue()
		    .getCardValue());
	}
    }
}
