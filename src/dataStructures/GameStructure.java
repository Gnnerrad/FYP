package dataStructures;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import objects.NNSettings;

public class GameStructure {

    private ArrayList<ArrayList<IOTuple>> player1, player2;

    public GameStructure() {
	player1 = new ArrayList<ArrayList<IOTuple>>();
	player2 = new ArrayList<ArrayList<IOTuple>>();
    }

    public void clear() {
	player1 = new ArrayList<ArrayList<IOTuple>>();
	player2 = new ArrayList<ArrayList<IOTuple>>();
    }

    public void addGame(ArrayList<IOTuple> player1Game, ArrayList<IOTuple> player2Game) {
	ArrayList<IOTuple> p1 = new ArrayList<IOTuple>(), p2 = new ArrayList<IOTuple>();
	for (IOTuple t : player1Game) {
	    p1.add(t);
	}
	for (IOTuple t : player2Game) {
	    p2.add(t);
	}
	player1.add(p1);
	player2.add(p2);
    }

    public void addPlayer1Game(ArrayList<IOTuple> player1Game) {
	ArrayList<IOTuple> p1 = new ArrayList<IOTuple>();
	for (IOTuple t : player1Game) {
	    p1.add(t);
	}
	player1.add(p1);
    }

    public void addPlayer2Game(ArrayList<IOTuple> player2Game) {
	ArrayList<IOTuple> p2 = new ArrayList<IOTuple>();
	for (IOTuple t : player2Game) {
	    p2.add(t);
	}
	player2.add(p2);
    }

    public ArrayList<IOTuple> getPlayer1Game(int index) {
	return player1.get(index);
    }

    public ArrayList<IOTuple> getPlayer2Game(int index) {
	return player2.get(index);
    }

    public int size() {
	if (player1.size() == player2.size())
	    return player1.size();
	else
	    return -1;
    }

    public void writeFile() {
	try (BufferedWriter out = new BufferedWriter(new FileWriter(NNSettings.p1GameFile, false))) {
	    for (int i = 0; i < player1.size(); i++) {
		ArrayList<IOTuple> p1Game = getPlayer1Game(i);
		for (int j = 0; j < p1Game.size() - 1; j++) {
		    for (double d : p1Game.get(j).getInput()) {
			out.write(d + ",");
		    }
		    out.write("\n");
		    for (double d : p1Game.get(j).getOutput()) {
			out.write(d + ",");
		    }
		    out.write("\n");
		}
		for (double d : p1Game.get(p1Game.size() - 1).getOutput()) {
		    out.write(d + ",");
		}
		out.write("\n\n");
	    }
	} catch (IOException e) {
	}
	try (BufferedWriter out = new BufferedWriter(new FileWriter(NNSettings.p2GameFile, false))) {
	    for (int i = 0; i < player2.size(); i++) {
		ArrayList<IOTuple> p2Game = getPlayer2Game(i);
		for (int j = 0; j < p2Game.size() - 1; j++) {
		    for (double d : p2Game.get(j).getInput()) {
			out.write(d + ",");
		    }
		    out.write("\n");
		    for (double d : p2Game.get(j).getOutput()) {
			out.write(d + ",");
		    }
		    out.write("\n");
		}
		for (double d : p2Game.get(p2Game.size() - 1).getOutput()) {
		    out.write(d + ",");
		}
		out.write("\n\n");
	    }
	} catch (IOException e) {
	}
    }

    public void readFile(String filePath, int player) {
	try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
	    String line;
	    String[] splitline;
	    double[] output = new double[NNSettings.outputSize], input = new double[NNSettings.inputSize];
	    ArrayList<IOTuple> game = new ArrayList<IOTuple>();
	    while ((line = in.readLine()) != null) {
		if (line.equals("")) {
		    game.add(new IOTuple(null, output));
		    if (player == 1)
			addPlayer1Game(game);
		    else
			addPlayer2Game(game);
		    game.clear();
		} else {
		    splitline = line.split(",");
		    if (line.length() < 100) {
			for (int i = 0; i < splitline.length; i++) {
			    output[i] = Double.valueOf(splitline[i]);
			}
		    } else {
			for (int i = 0; i < splitline.length; i++) {
			    input[i] = Double.valueOf(splitline[i]);
			}
		    }
		}
		if (output != null && input != null) {
		    game.add(new IOTuple(input, output));
		    input = new double[NNSettings.outputSize];
		    input = new double[NNSettings.inputSize];
		}
	    }

	} catch (IOException e) {
	}
    }
}
