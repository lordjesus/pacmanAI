package pacman.neuralnetwork;

import java.util.ArrayList;
import java.util.List;

public class NNTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] topology = new int[] {
				2, 2, 1
		};
		NeuralNetwork nn = new NeuralNetwork(topology);
		List<Double> input = new ArrayList<Double>();
		input.add(0.0);
		input.add(1.0);
		List<Double> outputs = nn.fire(input);
		
		
	}

}
