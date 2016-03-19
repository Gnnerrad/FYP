package dataStructures;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import NN.NNSettings;

public class GameData {

    private ArrayList<ArrayList<IOTuple>> player1Data, player2Data;

    public GameData() {
	player1Data = new ArrayList<ArrayList<IOTuple>>();
	player2Data = new ArrayList<ArrayList<IOTuple>>();
    }

    public void clear() {
	player1Data = new ArrayList<ArrayList<IOTuple>>();
	player2Data = new ArrayList<ArrayList<IOTuple>>();
    }

    public void addGame(ArrayList<IOTuple> player1Game, ArrayList<IOTuple> player2Game) {
	ArrayList<IOTuple> p1 = new ArrayList<IOTuple>(), p2 = new ArrayList<IOTuple>();
	for (IOTuple t : player1Game) {
	    p1.add(t);
	}
	for (IOTuple t : player2Game) {
	    p2.add(t);
	}
	player1Data.add(p1);
	player2Data.add(p2);
    }

    public void addPlayer1Game(ArrayList<IOTuple> player1Game) {
	ArrayList<IOTuple> p1 = new ArrayList<IOTuple>();
	for (IOTuple t : player1Game) {
	    p1.add(t);
	}
	player1Data.add(p1);
    }

    public void addPlayer2Game(ArrayList<IOTuple> player2Game) {
	ArrayList<IOTuple> p2 = new ArrayList<IOTuple>();
	for (IOTuple t : player2Game) {
	    p2.add(t);
	}
	player2Data.add(p2);
    }

    public ArrayList<IOTuple> getPlayer1Game(int index) {
	return player1Data.get(index);
    }

    public ArrayList<IOTuple> getPlayer2Game(int index) {
	return player2Data.get(index);
    }

    public int size() {
	if (player1Data.size() == player2Data.size())
	    return player1Data.size();
	else {
	    System.out.println(player1Data.size() + " : " + player2Data.size());
	    return -1;
	}
    }

    public void writeFile(String player1File, String player2File) {
	System.out.println(player1Data.size() + " = " + player2Data.size());
	try (BufferedWriter out = new BufferedWriter(new FileWriter(player1File, false))) {
	    for (int gameIndex = 0; gameIndex < player1Data.size(); gameIndex++) {
		ArrayList<IOTuple> player1Game = getPlayer1Game(gameIndex);
		for (IOTuple t : player1Game) {
		    out.write(t.getType() + "=" + t.getIndex() + "=");
		    if (t.getType() < 5) {
			for (double d : t.getInput()) {
			    out.write(d + ",");
			}
		    }
		    out.write("=");
		    for (double d : t.getOutput()) {
			out.write(d + ",");
		    }
		    out.write("\n");
		}
		out.write("\n");
	    }
	} catch (IOException e) {
	}
	try (BufferedWriter out = new BufferedWriter(new FileWriter(player2File, false))) {
	    for (int gameIndex = 0; gameIndex < player2Data.size(); gameIndex++) {
		ArrayList<IOTuple> player2Game = getPlayer2Game(gameIndex);
		for (IOTuple t : player2Game) {
		    out.write(t.getType() + "=" + t.getIndex() + "=");
		    if (t.getType() < 5) {
			for (double d : t.getInput()) {
			    out.write(d + ",");
			}
		    }
		    out.write("=");
		    for (double d : t.getOutput()) {
			out.write(d + ",");
		    }
		    out.write("\n");
		}
		out.write("\n");
	    }
	} catch (IOException e) {
	}
    }

    public void readFile(String filePath, int player) {
	try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
	    ArrayList<IOTuple> game = new ArrayList<IOTuple>();
	    String line;
	    String[] splitline, tempSplit;
	    int type, index;
	    double[] input = new double[NNSettings.inputSize], output = new double[NNSettings.outputSize];
	    while ((line = in.readLine()) != null) {
		if (line.equals("")) {
		    if (player == 1) {
			addPlayer1Game(game);
			game = new ArrayList<IOTuple>();
			input = new double[NNSettings.inputSize];
		    } else {
			addPlayer2Game(game);
			game = new ArrayList<IOTuple>();
			input = new double[NNSettings.inputSize];
		    }
		} else {
		    splitline = line.split("=");
		    type = Integer.parseInt(splitline[0]);
		    index = Integer.parseInt(splitline[1]);
		    if (type < 5) {
			tempSplit = splitline[2].split(",");
			for (int sIndex = 0; sIndex < tempSplit.length; sIndex++) {
			    input[sIndex] = Double.valueOf(tempSplit[sIndex]);
			}
		    } else {
			input = null;
		    }
		    tempSplit = splitline[3].split(",");
		    for (int sIndex = 0; sIndex < tempSplit.length; sIndex++) {
			output[sIndex] = Double.valueOf(tempSplit[sIndex]);
		    }
		    game.add(new IOTuple(type, index, input, output));
		}
	    }
	} catch (IOException e) {
	    System.out.println("Could not read the file: " + filePath);
	}
    }
}
