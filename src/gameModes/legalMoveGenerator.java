package gameModes;

import java.util.ArrayList;
import objects.Card;

public class legalMoveGenerator {
	public ArrayList<Card> legalMoves(ArrayList<Card> hand, Card cardAlreadyPlayed, boolean trumpableGameMode,
			int trumpSuit) {
		if (cardAlreadyPlayed == null) {
			return hand;
		}
		else
			if (trumpableGameMode) {
				return trumpable(hand, cardAlreadyPlayed, trumpSuit);
			}
			else {
				return nonTrumpable(hand, cardAlreadyPlayed);
			}
	}

	private ArrayList<Card> nonTrumpable(ArrayList<Card> hand, Card cardAlreadyPlayed) {
		ArrayList<Card> legalMoves = new ArrayList<Card>();
		for (Card inHand : hand) {
			if (inHand.getSuit().getCardSuit() == cardAlreadyPlayed.getSuit().getCardSuit()) {
				legalMoves.add(inHand);
			}
		}
		if (legalMoves.size() == 0) {
//			printList(hand);
			return hand;
		}
		else {
//			printList(legalMoves);
			return legalMoves;
		}
	}

	private ArrayList<Card> trumpable(ArrayList<Card> hand, Card cardAlreadyPlayed, int trumpSuit) {
		ArrayList<Card> legalMoves = new ArrayList<Card>();
		for (Card inHand : hand) {
			if (inHand.getSuit().getCardSuit() == cardAlreadyPlayed.getSuit().getCardSuit()
					|| inHand.getSuit().getCardSuit() == trumpSuit) {
				legalMoves.add(inHand);
			}
		}
		if (legalMoves.size() == 0) {
//			printList(hand);
			return hand;
		}
		else {
//			printList(legalMoves);
			return legalMoves;
		}
	}
	
	private void printList(ArrayList<Card> list){
		System.out.println("Legal Moves:");
		for(Card card : list){
			System.out.println(card.toString());
		}
	}
}
