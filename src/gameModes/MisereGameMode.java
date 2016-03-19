package gameModes;

import NN.NNSettings;
import objects.Card;
import objects.Deck;
import objects.Player;

public class MisereGameMode extends BasicModeLayout {

    public MisereGameMode(Player p1, Player p2, boolean player1turn, Deck deck) {
	super(p1, p2, player1turn, deck);
    }

    public boolean playMode() {
	setUpHands();
	if (player1turn) {
	    player1turn = player1.chooseWhoGoesFirst();
	    player1.setTurn(false);
	    player2.setTurn(false);
	} else {
	    player1turn = player2.chooseWhoGoesFirst();
	    player1.setTurn(false);
	    player2.setTurn(false);
	}
	Card card1, card2;
	while (player1.handSize() > 0 && player2.handSize() > 0) {
	    if (player1turn) {
		card1 = player1.playCard(lmg.legalMoves(player1.getHand(), nullCard, false, 0), nullCard);
		card2 = player2.playCard(lmg.legalMoves(player2.getHand(), card1, false, 0), card1);

		if (calculateWinner(card1, card2)) {
		    player1Tricks++;
		} else {
		    player2Tricks++;
		    player1turn = false;
		}
	    } else {
		card1 = player2.playCard(lmg.legalMoves(player2.getHand(), nullCard, false, 0), nullCard);
		card2 = player1.playCard(lmg.legalMoves(player1.getHand(), card1, false, 0), card1);

		if (calculateWinner(card1, card2)) {
		    player2Tricks++;
		} else {
		    player1Tricks++;
		    player1turn = true;
		}
	    }
	}
	// // return (player1Tricks > player1Tricks) ? true : false;
	// System.out.println("player"
	// + ((player1Tricks < player2Tricks) ? 1 : 2)
	// + " wins, "
	// + ((player1Tricks < player2Tricks) ? player1Tricks
	// : player2Tricks)
	// + " tricks to "
	// + ((player1Tricks > player2Tricks) ? player1Tricks
	// : player2Tricks) + " tricks.");
	return ((player1Tricks > player2Tricks) ? true : false);
    }

    private boolean calculateWinner(Card p1Card, Card p2Card) {
	return (p1Card.getValue().getCardValue() < p2Card.getValue().getCardValue());
    }
}
