package pacman.neuralnetwork;

import java.util.ArrayList;
import java.util.List;

public class Neuron {
	public int layer, id;
	public List<Synapse> incoming_synapses, outgoing_synapses;
	public double inputValue, outputValue, bias, error;
	
	public Neuron(int layer, int id) {
		this.layer = layer;
		this.id = id;
		this.incoming_synapses = new ArrayList<>();
		this.outgoing_synapses = new ArrayList<>();
	}
	
	public void SetInputValue(double value) {
		this.inputValue = value;
	}
	
	public double ComputeOutputValue() {
		if (layer == NeuralNetwork.INPUT_LAYER) {
			this.outputValue = this.inputValue;
		} else {
			double input = computeNetInput();
			this.inputValue = input;
			this.outputValue = sigmoid(input);
		}
		return outputValue;
	}
	
	private double sigmoid(double d) {
		return 1.0 / (1.0 + Math.pow(Math.E, -1.0 * d));
	}
	
	private double computeNetInput() {
		double input = 0;
		for (Synapse s : incoming_synapses) {
			input += s.weight * s.left_neuron.outputValue;
		}
		input += this.bias;
		return input;
	}
	
	@Override
	public String toString() {
		return "Id: " + id + ", Layer: " + layer + "\t InputValue: " + inputValue + "\t OutputValue: " + outputValue;
	}
}
