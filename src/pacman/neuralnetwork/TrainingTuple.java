package pacman.neuralnetwork;

import java.util.ArrayList;
import java.util.List;

public class TrainingTuple {
	public List<Double> input, output;
	
	public TrainingTuple() {
		this.input = new ArrayList<Double>();
		this.output = new ArrayList<Double>();
	}
}
