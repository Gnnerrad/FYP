package dataStructures;

public class IOTuple {

	private double[] input, output;
	private int type, index;
	/*
	 * &types; 0 = chose game mode 1 = discard six 2 = accept or next 3 = choose
	 * who goes first 4 = play card 5 = outcome
	 */

	public IOTuple(int type, int index, double[] input, double[] output) {
		this.input = input;
		this.output = output;
		this.type = type;
		this.index = index;
	}

	public double[] getInput() {
		return input;
	}

	public double[] getOutput() {
		return output;
	}

	public int getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}

	// public int getId(){
	// return id;
	// }

	public String toString() {
		String s = new String();
		for (double d : input) {
			s += String.valueOf(d);
		}
		s += "\n";
		for (double d : output) {
			s += String.valueOf(d);
		}
		return s;
	}
}