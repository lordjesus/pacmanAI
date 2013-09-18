package pacman.neuralnetwork;

import java.util.ArrayList;
import java.util.List;

public class Neuron {
	public int layer;
	public List<Synapse> synapses;
	public double inputValue, outputValue, bias;
	
	public Neuron(int layer) {
		this.layer = layer;
		this.synapses = new ArrayList<>();
	}
	
	public void SetInputValue(double value) {
		this.inputValue = value;
	}
	
	public double ComputeOutputValue() {
		if (layer == NeuralNetwork.INPUT_LAYER) {
			outputValue = inputValue;
		} else {
			double input = computeNetInput();
			outputValue = sigmoid(input);
		}
		return outputValue;
	}
	
	private double sigmoid(double d) {
		return 1.0 / (1.0 + Math.pow(Math.E, -1.0 * d));
	}
	
	private double computeNetInput() {
		double input = 0;
		for (Synapse s : synapses) {
			input += s.weight * s.n_from.outputValue;
		}
		input += this.bias;
		return input;
	}
	
	@Override
	public String toString() {
		return "Layer: " + layer + "\t InputValue: " + inputValue + "\t OutputValue: " + outputValue;
	}
}
