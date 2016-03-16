package gameModes;

import objects.Card;
import objects.Deck;
import objects.Player;

public class BasicModeLayout {
    protected int player1Tricks, player2Tricks;
    protected Player player1;
    protected Player player2;
    protected boolean player1turn;
    private Deck deck;
    protected legalMoveGenerator lmg;
    protected Card nullCard = null;

    public BasicModeLayout(Player p1, Player p2, boolean player1turn, Deck deck) {
	player1Tricks = 0;
	player2Tricks = 0;
	this.player1 = p1;
	this.player2 = p2;
	this.deck = deck;
	this.player1turn = player1turn;
	lmg = new legalMoveGenerator();
    }

    public boolean playMode() {
	setUpHands();
	Card card1, card2;
	while (player1.handSize() > 0 && player2.handSize() > 0) {
	    if (player1turn) {
		card1 = player1.playCard(lmg.legalMoves(player1.getHand(), nullCard, false, 0), nullCard);
		// System.out.println("Player1 plays "+card1.toString());
		card2 = player2.playCard(lmg.legalMoves(player2.getHand(), card1, false, 0), card1);

		// System.out.println("Player2 plays "+card2.toString());
		if (isbiggerNoSuit(card1, card2)) {
		    // System.out.println("Player1 wins.\n");
		    player1Tricks++;
		} else {
		    // System.out.println("Player2 wins.\n");
		    player2Tricks++;
		    player1turn = false;
		}
	    } else {
		card1 = player2.playCard(lmg.legalMoves(player2.getHand(), nullCard, false, 0), nullCard);
		// System.out.println("Player2 plays "+card1.toString());
		card2 = player1.playCard(lmg.legalMoves(player1.getHand(), card1, false, 0), card1);
		// System.out.println("Player1 plays "+card2.toString());
		if (isbiggerNoSuit(card1, card2)) {
		    // System.out.println("Player2 wins.\n");
		    player2Tricks++;
		} else {
		    // System.out.println("Player1 wins.\n");
		    player1Tricks++;
		    player1turn = true;
		}
	    }
	}
	// System.out.println("player"
	// + ((player1Tricks > player2Tricks) ? 1 : 2)
	// + " wins, "
	// + ((player1Tricks > player2Tricks) ? player1Tricks
	// : player2Tricks)
	// + " tricks to "
	// + ((player1Tricks < player2Tricks) ? player1Tricks
	// : player2Tricks) + " tricks.");
	return ((player1Tricks > player2Tricks) ? true : false);
    }

    protected void setUpHands() {
	player1.discardSix();
	player2.discardSix();

	if (player1turn) {
	    while (deck.size() >= 2) {
		acceptOrNext(player1);
		acceptOrNext(player2);
	    }
	} else {
	    while (deck.size() >= 2) {
		acceptOrNext(player2);
		acceptOrNext(player1);
	    }
	}
    }

    private void acceptOrNext(Player player) {
	Card card1 = deck.dealCard(), card2 = deck.dealCard();
	if (player.acceptCardChoice(card1, false)) {
	    player.addCardSeen(card2);
	} else {
	    player.acceptCardChoice(card2, true);
	}
    }

    private boolean isbiggerNoSuit(Card card1, Card card2) {
	if (card1.getSuit() == card2.getSuit() && card1.getValue().getCardValue() > card2.getValue().getCardValue()) {
	    return true; // player1 wins by trump
	} else if (card1.getSuit() == card2.getSuit() && card1.getValue().getCardValue() < card2.getValue().getCardValue()) {
	    return false; // player2 wins by trump
	} else {
	    return true;
	}
    }
}
