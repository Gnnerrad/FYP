package Whist.gameModes;

import java.util.Scanner;

import Whist.Card;
import Whist.Deck;
import Whist.Player;

public class TrumpGameMode extends BasicModeLayout {
    private Scanner in;
    public int trumpSuit; // CLUBS(0), SPADES(1), HEARTS(2), DIAMONDS(3)

    public TrumpGameMode(Player p1, Player p2, boolean player1turn, Deck deck) {
	super(p1, p2, player1turn, deck);
	chooseTrumpSuit("spades");

    }

    public boolean playMode() {
	setUpHands();
	Card card1, card2;
	while (player1.handSize() > 0 && player2.handSize() > 0) {
	    if (player1turn) {
		card1 = player1.playCard(
			lmg.legalMoves(player1.getHand(), nullCard, false, 1),
			nullCard);
		card2 = player2.playCard(
			lmg.legalMoves(player2.getHand(), card1, false, 1),
			card1);

		if (calculateWinner(card1, card2)) {
		    // System.out.println(card1.toString()+" beats "+card2.toString());
		    player1Tricks++;
		} else {
		    // System.out.println(card2.toString()+" beats "+card1.toString());
		    player2Tricks++;
		    player1turn = false;
		}
	    } else {
		card1 = player2.playCard(
			lmg.legalMoves(player2.getHand(), nullCard, false, 1),
			nullCard);
		card2 = player1.playCard(
			lmg.legalMoves(player1.getHand(), card1, false, 1),
			card1);

		if (calculateWinner(card1, card2)) {
		    // System.out.println(card1.toString()+" beats "+card2.toString());
		    player2Tricks++;
		} else {
		    // System.out.println(card2.toString()+" beats "+card1.toString());
		    player1Tricks++;
		    player1turn = true;
		}
	    }
	}
//	System.out.println("player"
//		+ ((player1Tricks > player2Tricks) ? 1 : 2)
//		+ " wins, "
//		+ ((player1Tricks > player2Tricks) ? player1Tricks
//			: player2Tricks)
//		+ " tricks to "
//		+ ((player1Tricks < player2Tricks) ? player1Tricks
//			: player2Tricks) + " tricks.");
	return ((player1Tricks > player2Tricks) ? true : false);
    }

    public void chooseTrumpSuit(String suit) {
	switch (suit) {
	case "clubs":
	    trumpSuit = 0;
	    break;
	case "spades":
	    trumpSuit = 1;
	    break;
	case "hearths":
	    trumpSuit = 2;
	    break;
	case "diamonds":
	    trumpSuit = 3;
	    break;
	default:
	    System.out
		    .println("The suit '"
			    + suit
			    + "' is not recognised.\nPlease choose one of: clubs, spades, hearths, diamonds");
	    Scanner in = new Scanner(System.in);
	    String temp = in.nextLine();
	    chooseTrumpSuit(temp.toLowerCase());
	    break;
	}
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
