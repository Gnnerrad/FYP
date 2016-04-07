package NN;

public class NNSettings {
    public static int inputSize = 216, outputSize = 5, testCount = 0, gmC = 0, disC = 0, acnxtC = 0, playC = 0, whoC = 0;
    public static String nn = "lib/Whist(216i - 60h - 5o).nnet", 
	    randNN = "lib/Whist(216i - 60h - 5o).nnet", 
	    player1GameFile = "data/(Game) Player1 Train 50.txt", 
	    player2GameFile = "data/(Game) Player2 Train 50.txt",
	    player1TestFile = "data/(Game) Player1 Test 50.txt",
	    player2TestFile = "data/(Game) Player2 Test 50.txt";
    public static double learningRate = 0.00005, lambda = 1;
}