/*TO-DO
	1:: Basic mode does not account for a draw, ask arthur what happens in that case.
 */

package objects;

import gameModes.BasicModeLayout;
import gameModes.ChoiceGameMode;
import gameModes.MisereGameMode;
import gameModes.TrumpGameMode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;

import td.temporalDifference;

public class GameDriver {

    private ArrayList<IOTuple> neuralNetworkData1 = new ArrayList<IOTuple>();
    private ArrayList<IOTuple> neuralNetworkData2 = new ArrayList<IOTuple>();
    private Scanner in;
    private boolean player1GoesFirst;
    private Deck deck;
    private Player player1, player2;
    private int player1Rounds, player2Rounds, player1Wins = 0, player2Wins = 0, drawCount = 0;
    private double totalTime = 0;
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
	double time, t1 = System.currentTimeMillis();
	for (int z = 0; z < 10; z++) {
	    System.out.println(z);
	    double start = System.currentTimeMillis();
	    if (z % 1000 == 0 || z == 0)
		writeGame = true;
	    else
		writeGame = false;
	    player1Rounds = 0;
	    player2Rounds = 0;
	    neuralNetworkData1.clear();
	    neuralNetworkData2.clear();
	    // neuralNetworkData.clear();
	    player1 = new Player(1, neuralNetworkData1);
	    player2 = new Player(2, neuralNetworkData2);
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
		player1Wins++;
		//there are 172 choices made by each player
		neuralNetworkData1.add(new IOTuple(173, null, new double[]{1,0,0}));
		neuralNetworkData2.add(new IOTuple(173, null, new double[]{0,0,1}));
		// td.setDataToLearn(neuralNetworkData, z, writeGame);
		
	    } else if (player1Rounds < player2Rounds) {
		player2Wins++;
		neuralNetworkData2.add(new IOTuple(173, null, new double[]{1,0,0}));
		neuralNetworkData1.add(new IOTuple(173, null, new double[]{0,0,1}));
		// td.setDataToLearn(neuralNetworkData, z, writeGame);
	    } else {
		drawCount++;
		neuralNetworkData1.add(new IOTuple(173, null, new double[]{0,1,0}));
		neuralNetworkData2.add(new IOTuple(173, null, new double[]{0,1,0}));
		// td.setDataToLearn(neuralNetworkData, z, writeGame);
	    }
	    td.learn(neuralNetworkData1);
	    td.learn(neuralNetworkData2);
	    time = System.currentTimeMillis() - start;
	    System.out.println("time = " + totalTime + "\tdata size " + neuralNetworkData1.size() + "," + neuralNetworkData2.size());
	    totalTime += time;
	}
//	System.out.println("time = " + totalTime + "\tdata size " + neuralNetworkData1.size() + "," + neuralNetworkData2.size());
//	td.setDataToLearn(neuralNetworkData, writeGame);

	time = System.currentTimeMillis() - t1;
	System.out.println("time = " + totalTime);
	// try (PrintWriter out = new PrintWriter(new BufferedWriter(new
	// FileWriter("data/0data.txt", true)))) {
	// out.println("Total Time Taken = " + totalTime + "\nPlayer 1 wins = "
	// + player1Wins + "\nPlayer 2 wins = " + player2Wins + "\nDraws = " +
	// drawCount);
	// } catch (IOException e) {
	//
	// }
    }

    private void chooseGameMode() {
	int gm;
	if (player1GoesFirst) {
	    gm = player1.chooseGamemode(player1Modes);
	    player2.acceptGameMode(gm);
	    if (gm >= 3)
		player1Modes.remove((Integer) 3);
	    else {
		player1Modes.remove((Integer) gm);
	    }
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
