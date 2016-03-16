package dataStructures;

public class IOTuple{

    private double[] input, output;
    private int id;

    public IOTuple(double[] left, double[] right) {
	this.input = left;
	this.output = right;
	this.id = id;
    }

    public double[] getInput() {
	return input;
    }

    public double[] getOutput() {
	return output;
    }
    
//    public int getId(){
//	return id;
//    }
    
    public String toString(){
	String s = new String();
	for(double d : input){
	    s += String.valueOf(d);
	}
	s += "\n";
	for(double d : output){
	    s += String.valueOf(d);
	}
	return s;
    }
}