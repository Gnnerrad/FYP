package objects;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Writer {
    public Writer() {
	// TODO Auto-generated constructor stub
    }

    public void writeGameDecisions(int gameCount, ArrayList<IOTuple> data) {
	try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data/game" + gameCount + ".txt", true)))) {
	    for (IOTuple tuple : data) {
		out.println(Arrays.toString(tuple.getInput()));
		out.println(Arrays.toString(tuple.getOutput()));
	    }
	} catch (IOException e) {
	}
    }

    public void writeGameOutcome(int gameCount, double[] outcome, int player) {
	try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data/game" + gameCount + ".txt", true)))) {
	    out.println("& "+player+", "+Arrays.toString(outcome));
	} catch (IOException e) {

	}
    }
}
