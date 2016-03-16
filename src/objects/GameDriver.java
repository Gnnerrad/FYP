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

import org.neuroph.core.NeuralNetwork;

import td.temporalDifference;
import dataStructures.GameStructure;
import dataStructures.IOTuple;

public class GameDriver {
    private ArrayList<IOTuple> neuralNetworkData1/* = new ArrayList<IOTuple>() */, neuralNetworkData2;// =
												      // new
												      // ArrayList<IOTuple>();
    private boolean player1GoesFirst;
    private Deck deck;
    private Player player1, player2;
    private int player1Rounds, player2Rounds, player1Wins = 0, player2Wins = 0, drawCount = 0;
    private double totalTime = 0;
    private ArrayList<Integer> player1Modes, player2Modes;
    private BasicModeLayout gameMode;
    private temporalDifference td = new temporalDifference();

    // private GameStructure savedGames = new GameStructure();

    public static void main(String[] args) {

	GameDriver driver = new GameDriver();
	driver.driver();
	// driver.play();
    }

    private void driver() {
	GameStructure randomPlayGames = new GameStructure();
	// double t1 = System.currentTimeMillis(), t2;
	// for (int i = 0; i < 100; i++) {
	// playAndSaveGame(false, randomPlayGames);
	// }
	// randomPlayGames.writeFile();
	// randomPlayGames.clear();
	randomPlayGames.readFile(NNSettings.p1GameFile, 1);
	randomPlayGames.readFile(NNSettings.p2GameFile, 2);
	for (int epoch = 0; epoch < 100; epoch++) {
	    double t1 = System.currentTimeMillis();
	    for (int i = 0; i < randomPlayGames.size(); i++) {
		learnGame(randomPlayGames, i);
	    }
	    double t2 = System.currentTimeMillis();
	    System.out.println("\t" + (t2 - t1));
	}
	// for (int i = 0; i < 100; i++) {
	// for (int j = 0; j < 100; j++) {
	// learnGame(selfPlayGames, j);
	// }
	// }
	// t2 = System.currentTimeMillis();
	// System.out.println("\t" + (t2 - t1));
    }

    private void learnGame(GameStructure gm, int gameIndex) {
	td.learn(gm.getPlayer1Game(gameIndex));
	td.learn(gm.getPlayer2Game(gameIndex));
    }

    private void playAndSaveGame(boolean selfPlay, GameStructure gm) {
	player1Rounds = 0;
	player2Rounds = 0;
	neuralNetworkData1 = new ArrayList<IOTuple>();
	neuralNetworkData2 = new ArrayList<IOTuple>();
	if (selfPlay) {
	    player1 = new Player(1, neuralNetworkData1);
	    player2 = new Player(2, neuralNetworkData2);
	} else {
	    player1 = new RandomPlayer(1, neuralNetworkData1);
	    player2 = new RandomPlayer(1, neuralNetworkData2);
	}
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
	    // 13-0 12-1 11-2 10-3 9-4 8-5 7-6
	    if (player1Rounds > 9) {
		neuralNetworkData1.add(new IOTuple(null, new double[] { 1, 0, 0, 0, 0 }));
		neuralNetworkData2.add(new IOTuple(null, new double[] { 0, 0, 0, 0, 1 }));
	    } else {
		neuralNetworkData1.add(new IOTuple(null, new double[] { 0, 1, 0, 0, 0 }));
		neuralNetworkData2.add(new IOTuple(null, new double[] { 0, 0, 0, 1, 0 }));
	    }

	} else if (player1Rounds < player2Rounds) {
	    player2Wins++;
	    if (player2Rounds > 9) {
		neuralNetworkData2.add(new IOTuple(null, new double[] { 1, 0, 0, 0, 0 }));
		neuralNetworkData1.add(new IOTuple(null, new double[] { 0, 0, 0, 0, 1 }));
	    } else {
		neuralNetworkData2.add(new IOTuple(null, new double[] { 0, 1, 0, 0, 0 }));
		neuralNetworkData1.add(new IOTuple(null, new double[] { 0, 0, 0, 1, 0 }));
	    }
	} else {
	    drawCount++;
	    neuralNetworkData1.add(new IOTuple(null, new double[] { 0, 0, 1, 0, 0 }));
	    neuralNetworkData2.add(new IOTuple(null, new double[] { 0, 0, 1, 0, 0 }));
	}
	System.out.println(player1Wins + ":" + player2Wins + ":" + drawCount);
	gm.addGame(neuralNetworkData1, neuralNetworkData2);
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
