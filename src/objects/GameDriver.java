/*TO-DO
	1:: Basic mode does not account for a draw, ask arthur what happens in that case.
 */

package objects;

import gameModes.BasicModeLayout;
import gameModes.ChoiceGameMode;
import gameModes.MisereGameMode;
import gameModes.TrumpGameMode;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;

import td.temporalDifference;

public class GameDriver {

    private ArrayList<IOTuple> neuralNetworkData = new ArrayList<IOTuple>();
    private Scanner in;
    private boolean player1GoesFirst;
    private Deck deck;
    private Player player1, player2;
    private int player1Rounds = 0, player2Rounds = 0;
    private ArrayList<Integer> player1Modes, player2Modes;
    private BasicModeLayout gameMode;
    private temporalDifference td = new temporalDifference();

    public static void main(String[] args) {

	GameDriver driver = new GameDriver();
	driver.driver();
	// driver.networkTest();
    }

    private void driver() {
	boolean writeGame = false;
	for (int z = 0; z < 100000; z++) {
	    if (z % 1000 == 0 || z == 0)
		writeGame = true;
	    else
		writeGame = false;
	    System.out.println(z);
	    player1 = new Player(1, neuralNetworkData);
	    player2 = new Player(2, neuralNetworkData);
	    populateGameModeArrays();
	    player1GoesFirst = getRandomBoolean();

	    for (int i = 0; i < 8; i++) {
		deck = new Deck();
		setUpGame();
		chooseGameMode();
		boolean b = gameMode.playMode();
		if (b)
		    player1Rounds++;
		else
		    player2Rounds++;
		player1GoesFirst = !player1GoesFirst;
	    }
	    if (player1Rounds > player2Rounds) {
		System.out.println("\nplayer1 wins " + player1Rounds + " to " + player2Rounds);
		td.setOutcome(1);
		td.setDataToLearn(neuralNetworkData, z, writeGame);
	    } else if (player1Rounds < player2Rounds) {
		System.out.println("\nplayer2 wins " + player2Rounds + " to " + player1Rounds);
		td.setOutcome(2);
		td.setDataToLearn(neuralNetworkData, z, writeGame);
	    } else {
		System.out.println("\nIt was a draw");
		td.setOutcome(0);
		td.setDataToLearn(neuralNetworkData, z, writeGame);
	    }
	}
    }

    private void chooseGameMode() {
	int gm;
	if (player1GoesFirst) {
	    gm = player1.chooseGamemode(player1Modes);
	    player2.acceptGameMode(gm);
	    if (gm >= 3)
		player1Modes.remove((Integer) 3);
	    else
		player1Modes.remove((Integer) gm);
	} else {
	    gm = player2.chooseGamemode(player2Modes);
	    player1.acceptGameMode(gm);
	    if (gm >= 3)
		player2Modes.remove((Integer) 3);
	    else
		player2Modes.remove((Integer) gm);
	}

	switch (gm) {
	// trickle down case, will enter case 14 as there is no break statement
	case 0:
	case 14:
	    gameMode = new MisereGameMode(player1, player2, player1GoesFirst, deck);
	    break;
	case 1:
	case 15:
	    gameMode = new BasicModeLayout(player1, player2, player1GoesFirst, deck);
	    break;
	case 2:
	case 11:
	    gameMode = new TrumpGameMode(player1, player2, player1GoesFirst, deck);
	    break;
	case 10:
	    gameMode = new ChoiceGameMode(player1, player2, player1GoesFirst, deck, 0);
	    break;
	case 12:
	    gameMode = new ChoiceGameMode(player1, player2, player1GoesFirst, deck, 2);
	    break;
	case 13:
	    gameMode = new ChoiceGameMode(player1, player2, player1GoesFirst, deck, 3);
	    break;
	default:
	    System.out.println("There has been an error is the game mode choice switch.");
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
    }

    public boolean getRandomBoolean() {
	Random random = new Random();
	return random.nextBoolean();
    }

    private void networkTest() {
	NeuralNetwork oldNetwork = NeuralNetwork.load("lib/origional NN.nnet");
	NeuralNetwork newNetwork = NeuralNetwork.load("lib/NN.nnet");
	for (int i = 0; i < oldNetwork.getWeights().length; i++) {
	    System.out.println(oldNetwork.getWeights()[i] + "\t" + newNetwork.getWeights()[i]);
	}
    }
}

// System.out.println(tempCard.getValue() + " of " + tempCard.getSuit());
