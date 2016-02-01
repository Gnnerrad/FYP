package objects;

public class Card {

    public enum CardValue {
	/*
	 * if enum values need to change fix these: CardValue @ Player[134]
	 */
	TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(
		10), JACK(11), QUEEN(12), KING(13), ACE(14);

	private int cardValue;

	private CardValue(int value) {
	    this.cardValue = value;
	}

	public int getCardValue() {
	    return cardValue;
	}
    }

    public enum Suit {
	CLUBS(0), SPADES(1), HEARTS(2), DIAMONDS(3);

	private int cardSuit;

	private Suit(int suit) {
	    this.cardSuit = suit;
	}

	public int getCardSuit() {
	    return cardSuit;
	}
    }

    private Suit suit;
    private CardValue value;

    public Card(Suit suit, CardValue value) {
	this.setSuit(suit);
	this.setValue(value);
    }

    public Suit getSuit() {
	return suit;
    }

    public void setSuit(Suit suit) {
	this.suit = suit;
    }

    public CardValue getValue() {
	return value;
    }

    public void setValue(CardValue value) {
	this.value = value;
    }

    public String toString() {
	return getValue() + "(" + getValue().cardValue + ")" + " of "
		+ getSuit() + "(" + getSuit().cardSuit + ")";
    }
}
