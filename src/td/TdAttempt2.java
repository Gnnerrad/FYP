//package td;
//
//import java.util.ArrayList;
//import java.util.concurrent.ThreadLocalRandom;
//import objects.IOTuple;
//import org.neuroph.core.NeuralNetwork;
//
//public class TdAttempt2 {
//    private NeuralNetwork network = NeuralNetwork.load("lib/NN.nnet");
//    private ArrayList<IOTuple> neuralNetworkData = new ArrayList<IOTuple>();
//
//    /* Experimental Parameters: */
//
//    int n = network.getInputsCount(), num_hidden = network.getLayerAt(1).getNeuronsCount(), m = network.getOutputsCount();
//    // number of inputs, hidden, and output units
//    int MAX_UNITS = n + 1; /* maximum total number of units (to set array sizes) */
//    int time_steps;/* number of time steps to simulate */
//    double BIAS = 1; /* strength of the bias (constant input) contribution */
//    double ALPHA = 1 / n; /* 1st layer learning rate (typically 1/n) */
//    double BETA = 1 / num_hidden; // 2nd layer learning rate (typically
//				  // 1/num_hidden)
//    double GAMMA = 0.9; /* discount-rate parameter (typically 0.9) */
//    double LAMBDA = 0.5; /* trace decay parameter (should be <= gamma) */
//
//    /* Network Data Structure: */
//
//    double[][] x; /* input data (units) */
//    double[] h = new double[MAX_UNITS];; /* hidden layer */
//    double[] y = new double[MAX_UNITS]; /* output layer */
//    double[][] w = new double[num_hidden][m]; /* hidden -> out weights */
//    double[][] v = new double[n][num_hidden]; // in -> hidden weights
//
//    /* Learning Data Structure: */
//
//    double[] old_y = new double[MAX_UNITS];
//    double[][][] ev = new double[MAX_UNITS][MAX_UNITS][MAX_UNITS];
//    // hidden trace
//    double[][] ew = new double[MAX_UNITS][MAX_UNITS]; /* output trace */
//    double[][] r; /* reward */
//    double[] error = new double[MAX_UNITS]; /* TD error */
//    int t; /* current time step */
//
//    private void populateInputs() {
//	for (int time = 0; time < neuralNetworkData.size(); time++) {
//	    IOTuple data = neuralNetworkData.get(time);
//	    int i = 0;
//	    for (double d : data.getInput()) {
//		x[time][i] = d;
//		i++;
//	    }
//	    while (i < MAX_UNITS) {
//		x[time][i] = 0;
//		i++;
//	    }
//	}
//    }
//
//    public void addIOTuple(double[] input, double[] output) {
//	neuralNetworkData.add(new IOTuple(input, output));
//    }
//
//    private void initialiseVars() {
//	time_steps = neuralNetworkData.size();
//	x = new double[time_steps][MAX_UNITS];
//	r = new double[time_steps][MAX_UNITS];
//    }
//
//    public void notMain() {
//	initialiseVars();
//	int i, j, k, z = 0;
//	populateInputs();
//	InitNetwork();
//
//	t = 0; /* No learning on time step 0 */
//	Response(); /* Just compute old response (old_y)... */
//	for (k = 0; k < m; k++)
//	    old_y[k] = y[k];
//	UpdateElig(); /* ...and prepare the eligibilities */
//
//	for (t = 1; t < time_steps; t++) {
//	    Response(); /* forward pass - compute activities */
//	    for (k = 0; k < m; k++)
//		error[k] = r[t][k] + GAMMA * y[k] - old_y[k]; /* form errors */
//	    TDlearn(); /* backward pass - learning */
//	    Response(); /* forward pass must be done twice to form TD errors */
//	    for (k = 0; k < m; k++)
//		old_y[k] = y[k]; /* for use in next cycle's TD errors */
//	    UpdateElig(); /* update eligibility traces */
//	} /* end t */
//	// Do one more for final time step where output = game outcome
//	// ???????????????????????????????????????????????
//	double[] newWeights = new double[network.getWeights().length];
//	for (j = 0; j < num_hidden - 1; j++) {
//	    for (i = 0; i < n; i++) {
//		newWeights[z] = v[i][j];
//		z++;
//	    }
//	}
//	for (k = 0; k < m; k++) {
//	    for (j = 0; j < num_hidden; j++) {
//		newWeights[z] = w[j][k];
//		z++;
//	    }
//	}
//	for (z = 0; z < newWeights.length; z++) {
//	    // System.out.println(z+"\t"+network.getWeights()[z] + "\t" +
//	    // newWeights[z]);
//	}
//	// network.setWeights(arg0);
//    }
//
//    private void InitNetwork() {
//	int s, j, k, i, y = 0, z = 0;
//
//	for (s = 0; s < time_steps; s++) {
//	    x[s][n] = BIAS;
//	}
//	h[num_hidden] = BIAS;
//	int fux = 0;
//	for (j = 0; j < num_hidden; j++) {
//	    for (k = 0; k < m; k++) {
//		// Neuroph assigns the weights in a rear-ward fashion.
//		// The nodes on the input layer have no weights, while each node
//		// in the hidden layer has 215 weights, and each node in the
//		// output layer has 61 weights. The output layer is layer 2.
//		fux++;
//		w[j][k] = network.getLayerAt(2).getNeuronAt(k).getWeights()[j].value;
//		ew[j][k] = 0.0;
//		old_y[k] = 0.0;
//	    }
//	    for (i = 0; i < n; i++) {
//		// This is to avoid looking for connecting weight between the
//		// hidden bias node and the input layer.
//		fux++;
//		v[i][j] = network
//			.getLayerAt(1)
//			.getNeuronAt(j)
//			.getWeights()[i]
//				.value;
//		// System.out.println("v["+i+"]["+j+"] -> "+(i*j));
//		for (k = 0; k < m; k++) {
//		    ev[i][j][k] = 0.0;
//		}
//	    }
//	}
//	System.out.println(network.getWeights().length + "\t" + fux);
//    }
//
//    private void Response() {
//	int i, j, k;
//
//	h[num_hidden] = BIAS; // MAYBE
//	x[t][n] = BIAS;
//
//	for (j = 0; j < num_hidden; j++) {
//	    h[j] = 0.0;
//	    for (i = 0; i < n; i++) {
//		h[j] += x[t][i] * v[i][j];
//	    }
//	    h[j] = 1.0 / (1.0 + Math.pow(Math.E, -h[j])); /* asymmetric sigmoid */
//	}
//	for (k = 0; k < m; k++) {
//	    y[k] = 0.0;
//	    for (j = 0; j < num_hidden; j++) {
//		y[k] += h[j] * w[j][k];
//	    }
//	    y[k] = 1.0 / (1.0 + Math.pow(Math.E, -y[k]));
//	    // asymmetric sigmoid (OPTIONAL)
//	}
//    }
//
//    private void TDlearn()
//
//    {
//	int i, j, k;
//
//	for (k = 0; k < m; k++) {
//	    for (j = 0; j < num_hidden; j++) {
//		w[j][k] += BETA * error[k] * ew[j][k];
//		for (i = 0; i < n; i++) {
//		    v[i][j] += ALPHA * error[k] * ev[i][j][k];
//		}
//	    }
//	}
//    }
//
//    /*****
//     * UpdateElig()
//     *
//     * Calculate new weight eligibilities
//     *
//     *****/
//
//    private void UpdateElig() {
//	int i, j, k;
//	double[] temp = new double[MAX_UNITS];
//
//	for (k = 0; k < m; k++)
//	    temp[k] = y[k] * (1 - y[k]);
//
//	for (j = 0; j < num_hidden; j++) {
//	    for (k = 0; k < m; k++) {
//		ew[j][k] = LAMBDA * ew[j][k] + temp[k] * h[j];
//		for (i = 0; i <= n; i++) {
//		    ev[i][j][k] = LAMBDA * ev[i][j][k] + temp[k] * w[j][k] * h[j] * (1 - h[j]) * x[t][i];
//		}
//	    }
//	}
//    }
//}