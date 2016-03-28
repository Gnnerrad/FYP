package NN;

public class NNSettings {
    public static int inputSize = 216, outputSize = 5, testCount = 0;
    public static String nn = "lib/NN.nnet", 
	    randNN = "lib/Whist(216i - 60h - 5o).nnet", 
	    player1GameFile = "data/(Game) Player1 Train 100.txt", 
	    player2GameFile = "data/(Game) Player2 Train 100.txt",
	    player1TestFile = "data/(Game) Player1 Test 100.txt",
	    player2TestFile = "data/(Game) Player2 Test 100.txt";
    public static double learningRate = 0.0005, lambda = 0.5;
}
