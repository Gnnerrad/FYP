/*TO-DO
	1:: Basic mode does not account for a draw, ask arthur what happens in that case.
 */

package objects;

import gameModes.BasicModeLayout;
import gameModes.ChoiceGameMode;
import gameModes.MisereGameMode;
import gameModes.TrumpGameMode;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GameDriver {

    private Scanner in;
    private boolean player1GoesFirst;
    private Deck deck;
    private Player player1;
    private Player player2;
    private ArrayList<Integer> player1Modes, player2Modes;
    private BasicModeLayout gameMode;

    public static void main(String[] args) {

	GameDriver driver = new GameDriver();
	driver.driver();
    }

    private void driver() {
	deck = new Deck();
	player1 = new Player();
	player2 = new Player();

	player1GoesFirst = getRandomBoolean();
	setUpGame();
	chooseGameMode();
	gameMode.playMode();
    }

    private void chooseGameMode() {
	int gm;
	if (player1GoesFirst) {
	    System.out.println("Player 1 chooses game mode.");
	    gm = player1.chooseGamemode(player1Modes);
	    if (gm >= 3)
		player1Modes.remove(3);
	    else
		player1Modes.remove(gm);

	} else {
	    System.out.println("Player 2 chooses game mode.");
	    gm = player2.chooseGamemode(player2Modes);
	    if (gm >= 3)
		player2Modes.remove(3);
	    else
		player2Modes.remove(gm);
	}

	switch (gm) {
	// trickle down case, will enter case 14 as there is no break statement
	case 0:
	case 14:
	    gameMode = new MisereGameMode(player1, player2, player1GoesFirst,
		    deck);
	    break;
	case 1:
	case 15:
	    gameMode = new BasicModeLayout(player1, player2, player1GoesFirst,
		    deck);
	    break;
	case 2:
	case 11:
	    gameMode = new TrumpGameMode(player1, player2, player1GoesFirst,
		    deck);
	    break;
	case 10:
	    gameMode = new ChoiceGameMode(player1, player2, player1GoesFirst,
		    deck, 0);
	    break;
	case 12:
	    gameMode = new ChoiceGameMode(player1, player2, player1GoesFirst,
		    deck, 2);
	    break;
	case 13:
	    gameMode = new ChoiceGameMode(player1, player2, player1GoesFirst,
		    deck, 3);
	    break;
	default:
	    System.out
		    .println("There has been an error is the game mode choice switch.");
	    break;
	}
    }

    private void populateGameModeArrays() {
	player1Modes = new ArrayList<>();
	player2Modes = new ArrayList<>();
	for (int i = 0; i < 4; i++) {
	    // 0 = misere
	    // 1 = no trump
	    // 2 = spades
	    // 3 = choice => 10 = clubs, 11 = spades, 12 = hearts, 13 = diamonds
	    // => 14 = misere, 15 = no trump
	    player1Modes.add(i);
	    player2Modes.add(i);
	}
    }

    private void setUpGame() {
	deck.shuffle();
	if (player1GoesFirst) {
	    for (int i = 0; i < 12; i++) {
		// 12 is the initial amount of cards dealt
		player1.addCardToHand(deck.dealCard());
		player2.addCardToHand(deck.dealCard());
	    }
	} else {
	    for (int i = 0; i < 12; i++) {
		player2.addCardToHand(deck.dealCard());
		player1.addCardToHand(deck.dealCard());
	    }
	}
	populateGameModeArrays();
    }

    public boolean getRandomBoolean() {
	Random random = new Random();
	return random.nextBoolean();
    }
}

// System.out.println(tempCard.getValue() + " of " + tempCard.getSuit());
