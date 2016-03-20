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
import java.util.ArrayList;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;

import NN.NNSettings;
import td.temporalDifference;
import dataStructures.GameData;
import dataStructures.IOTuple;

public class GameDriver {
	private ArrayList<IOTuple> neuralNetworkData1/*
													 * = new
													 * ArrayList<IOTuple>()
													 */, neuralNetworkData2;// =
	// new
	// ArrayList<IOTuple>();
	private boolean player1GoesFirst;
	private Deck deck;
	private Player player1, player2;
	private int player1Rounds, player2Rounds, player1WinsB = 0, player1WinsS = 0, player2WinsS = 0, player2WinsB = 0,
			drawCount = 0;
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
		// lean(500);
		// test100();
		// createFile(true);
		// player1WinsB = 0;
		// player1WinsS = 0;
		// player2WinsS = 0;
		// player2WinsB = 0;
		// drawCount = 0;
		// createFile(false);

		// double start = System.currentTimeMillis(), t1, t2, average = 0;
		// GameData gd = new GameData();
		// for (int i = 0; i <= 10000; i++) {6
		// t1 = System.currentTimeMillis();
		// playGame(true, NNSettings.nn, NNSettings.nn, null, gd);
		// learnGame(gd, 0);
		// gd.clear();
		// t2 = System.currentTimeMillis();
		// average += (t2 - t1);
		// System.out.println(i + "\t" + (t2 - t1) + "\t:\t" + (average / (i +
		// 1)) + "/" + (t2 - start));
		// td.save();
		// if (i % 1000 == 0) {
		// td.save("lib/NNself " + i);
		// }
		// }

		// learn(9000);
		// test100();
		// System.out.println();
		// testGame(NNSettings.nn);

		testGame("lib/NN.nnet", 100);
		for (int i = 10000; i >= 0; i -= 1000) {
			testGame("lib/NNself " + i + ".nnet", 100);
		}
		testGame("lib/Whist(216i - 60h - 5o).nnet", 100);
		// pvp("lib/NNself 2000.nnet", "lib/NNself 1000.nnet", 100);
	}

	// private void test100() {
	// for (int nn = 0; nn <= 1000; nn += 50) {
	// testGame("lib/Whist(216i - 60h - 5o) Epoch " + nn + ".nnet");
	// }
	// // // testGame("lib/Whist(216i - 60h - 5o) Epoch 0.nnet");
	// // for(int i = 0; i<100;i++){
	// // playGame(true, "lib/Whist(216i - 60h - 5o) Epoch 1000.nnet",
	// // "lib/Whist(216i - 60h - 5o) Epoch 0.nnet", null, new GameData());
	// // }
	// }

	private void pvp(String NN1, String NN2, int games) {
		GameData gd = new GameData();
		for (int i = 0; i < games; i++)
			playGame(true, NN1, NN2, null, gd);
	}

	private void createFile(boolean trainFile) {
		GameData randomPlayGames = new GameData();
		if (trainFile) {
			for (int i = 0; i < 100; i++) {
				playGame(true, NNSettings.nn, NNSettings.nn, null, randomPlayGames);
			}
			randomPlayGames.writeFile(NNSettings.player1GameFile, NNSettings.player2GameFile);
		} else {
			for (int i = 0; i < 100; i++) {
				playGame(true, NNSettings.nn, NNSettings.nn, "data\100 deck", randomPlayGames);
			}
			randomPlayGames.writeFile(NNSettings.player1TestFile, NNSettings.player2TestFile);
		}
	}

	private void learn(int epochs) {
		double start = System.currentTimeMillis(), t1, t2, average = 0;
		GameData gd = new GameData();
		gd.readFile(NNSettings.player1GameFile, 1);
		gd.readFile(NNSettings.player2GameFile, 2);
		for (int epoch = 0; epoch <= epochs; epoch++) {
			t1 = System.currentTimeMillis();
			for (int i = 0; i < gd.size(); i++) {
				learnGame(gd, i);
			}
			if (epoch % 50 == 0) {
				td.save();
				td.save("lib/Whist(216i - 60h - 5o) Epoch " + (epoch + 1000) + ".nnet");
			}
			t2 = System.currentTimeMillis();
			average += (t2 - t1);
			System.out.println(epoch + "\t" + (t2 - t1) + "\t:\t" + (average / (epoch + 1)) + "/" + (t2 - start));
		}
	}

	private void learnGame(GameData gm, int gameIndex) {
		td.learn(gm.getPlayer1Game(gameIndex));
		td.learn(gm.getPlayer2Game(gameIndex));
	}

	private void testGame(String NN, int games) {
		GameData gd = new GameData();
		for (int i = 0; i < games; i++)
			playGame(true, NN, NN, null, gd);

		double[] tempOut, outcome1, outcome2;
		double gameModeRmse = 0, discard6Rmse = 0, acceptOrNextRmse = 0, playCardRmse = 0, temp = 0;
		int gameModeCount = 0, discard6Count = 0, acceptOrNextCount = 0, playCardCount = 0;
		for (int i = 0; i < gd.size(); i++) {
			ArrayList<IOTuple> game1 = gd.getPlayer1Game(i);
			outcome1 = game1.get(game1.size() - 1).getOutput();
			ArrayList<IOTuple> game2 = gd.getPlayer2Game(i);
			outcome2 = game2.get(game2.size() - 1).getOutput();
			for (IOTuple t : game1) {
				switch (t.getType()) {
				case 0:
					for (int j = 0; j < outcome1.length; j++) {
						gameModeRmse += Math.pow(outcome1[j] - t.getOutput()[j], 2);
						gameModeCount++;
					}
					break;
				case 1:
					for (int j = 0; j < outcome1.length; j++) {
						discard6Rmse += Math.pow(outcome1[j] - t.getOutput()[j], 2);
						discard6Count++;
					}
					break;
				case 2:
					for (int j = 0; j < outcome1.length; j++) {
						acceptOrNextRmse += Math.pow(outcome1[j] - t.getOutput()[j], 2);
						acceptOrNextCount++;
					}
					break;
				case 4:
					for (int j = 0; j < outcome1.length; j++) {
						playCardRmse += Math.pow(outcome1[j] - t.getOutput()[j], 2);
						playCardCount++;
					}
					break;
				default:
					break;
				}
			}
			for (IOTuple t : game2) {
				switch (t.getType()) {
				case 0:
					for (int j = 0; j < outcome2.length; j++) {
						gameModeRmse += Math.pow(outcome2[j] - t.getOutput()[j], 2);
						gameModeCount++;
					}
					break;
				case 1:
					for (int j = 0; j < outcome2.length; j++) {
						discard6Rmse += Math.pow(outcome2[j] - t.getOutput()[j], 2);
						discard6Count++;
					}
					break;
				case 2:
					for (int j = 0; j < outcome2.length; j++) {
						acceptOrNextRmse += Math.pow(outcome2[j] - t.getOutput()[j], 2);
						acceptOrNextCount++;
					}
					break;
				case 4:
					for (int j = 0; j < outcome2.length; j++) {
						playCardRmse += Math.pow(outcome2[j] - t.getOutput()[j], 2);
						playCardCount++;
					}
					break;
				default:
					break;
				}
			}
		}
		try (BufferedWriter out = new BufferedWriter(new FileWriter("data/rmses", true))) {
			out.write(Math.sqrt(gameModeRmse / gameModeCount) + "\t" + Math.sqrt(discard6Rmse / discard6Count) + "\t"
					+ Math.sqrt(acceptOrNextRmse / acceptOrNextCount) + "\t" + Math.sqrt(playCardRmse / playCardCount)
					+ "\n");
		} catch (Exception e) {

		}
	}

	private void playGame(boolean selfPlay, String player1NN, String player2NN, String DeckFile, GameData gm) {
		player1Rounds = 0;
		player2Rounds = 0;
		neuralNetworkData1 = new ArrayList<IOTuple>();
		neuralNetworkData2 = new ArrayList<IOTuple>();
		if (selfPlay) {
			player1 = new Player(1, player1NN, neuralNetworkData1);
			player2 = new Player(2, player2NN, neuralNetworkData2);
		} else {
			player1 = new RandomPlayer(1, player1NN, neuralNetworkData1);
			player2 = new RandomPlayer(2, player2NN, neuralNetworkData2);
		}
		populateGameModeArrays();
		player1GoesFirst = getRandomBoolean();

		for (int i = 0; i < 8; i++) {
			deck = new Deck();
			deck.shuffle();
			if (DeckFile != null) {
				deck.write(DeckFile, (i == 0) ? true : false);
			}
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
			// 13-0 12-1 11-2 10-3 9-4 8-5 7-6
			if (player1Rounds > 9) {
				player1WinsB++;
				neuralNetworkData1.add(new IOTuple(5, neuralNetworkData1.size(), null, new double[] { 1, 0, 0, 0, 0 }));
				neuralNetworkData2.add(new IOTuple(5, neuralNetworkData2.size(), null, new double[] { 0, 0, 0, 0, 1 }));
			} else {
				player1WinsS++;
				neuralNetworkData1.add(new IOTuple(5, neuralNetworkData1.size(), null, new double[] { 0, 1, 0, 0, 0 }));
				neuralNetworkData2.add(new IOTuple(5, neuralNetworkData2.size(), null, new double[] { 0, 0, 0, 1, 0 }));
			}

		} else if (player1Rounds < player2Rounds) {
			if (player2Rounds > 9) {
				player2WinsB++;
				neuralNetworkData2.add(new IOTuple(5, neuralNetworkData2.size(), null, new double[] { 1, 0, 0, 0, 0 }));
				neuralNetworkData1.add(new IOTuple(5, neuralNetworkData1.size(), null, new double[] { 0, 0, 0, 0, 1 }));
			} else {
				player2WinsS++;
				neuralNetworkData2.add(new IOTuple(5, neuralNetworkData2.size(), null, new double[] { 0, 1, 0, 0, 0 }));
				neuralNetworkData1.add(new IOTuple(5, neuralNetworkData1.size(), null, new double[] { 0, 0, 0, 1, 0 }));
			}
		} else {
			drawCount++;
			neuralNetworkData1.add(new IOTuple(5, neuralNetworkData1.size(), null, new double[] { 0, 0, 1, 0, 0 }));
			neuralNetworkData2.add(new IOTuple(5, neuralNetworkData2.size(), null, new double[] { 0, 0, 1, 0, 0 }));
		}
		System.out
				.println(player1WinsB + "-" + player1WinsS + ":" + drawCount + ":" + player2WinsS + "-" + player2WinsB);
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
