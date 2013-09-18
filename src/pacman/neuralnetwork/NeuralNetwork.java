package pacman.neuralnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NeuralNetwork {
	public final static int INPUT_LAYER = 0;
	public static int OUTPUT_LAYER = 2;
	
	public HashMap<Integer, List<Neuron>> layers;
	
	public NeuralNetwork(int[] topology) {
		OUTPUT_LAYER = topology.length - 1;
		layers = new HashMap<Integer, List<Neuron>>();
		
		
		for (int layer = 0; layer < topology.length; layer++) {
			int l = topology[layer];
			for (int j = 0; j < l; j++) {
				Neuron neuron = new Neuron(layer);	
				List<Neuron> layerList = layers.get(layer);
				if (layerList == null) {
					layerList = new ArrayList<Neuron>();
					layers.put(layer, layerList);
				}
				layerList.add(neuron);
			}
		}
		
		int idx = 0;
		for (int i = OUTPUT_LAYER; i > INPUT_LAYER; i--) {
			List<Neuron> thisLayer = layers.get(i);
			List<Neuron> previousLayer = layers.get(i-1);
			for (Neuron n : thisLayer) {
				for (Neuron other : previousLayer) {
					Synapse s = new Synapse();
					s.index = idx++;
					s.n_from = other;
					s.n_to = n;
					s.weight = 1 - 2 * Math.random();
					
					n.synapses.add(s);
				}
			}
		}
	}
	
	public List<Double> fire(List<Double> input) {
		List<Neuron> inputNeurons = layers.get(INPUT_LAYER);
		for (int i = 0; i < inputNeurons.size(); i++) {
			Neuron n = inputNeurons.get(i);
			n.SetInputValue(input.get(i));
			
		}
		for (int i = INPUT_LAYER; i <= OUTPUT_LAYER; i++) {
			List<Neuron> layerNeurons = layers.get(i);
			for (Neuron n : layerNeurons) {
				n.ComputeOutputValue();
			}
		}
		List<Neuron> outputNeurons = layers.get(OUTPUT_LAYER);
		List<Double> outputs = new ArrayList<Double>();
		for (Neuron n : outputNeurons) {
			outputs.add(n.outputValue);
			System.out.println(n.toString());
		}
		return outputs;
	}
}
