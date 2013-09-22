package pacman.neuralnetwork;

public class Synapse {
	public Neuron left_neuron, right_neuron;
	public double weight;
	public int index;
	
	@Override
	public String toString() {
		return "w" + left_neuron.id + "," + right_neuron.id + "=" + weight;
	}
}
