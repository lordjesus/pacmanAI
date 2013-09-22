package pacman.neuralnetwork;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class NeuralNetwork {
	public final static int INPUT_LAYER = 0;
	public static int OUTPUT_LAYER = 2;
	public static final String LAYER_STORE = "LayerStore";
	public static final String LAYER = "Layer";
	public static final String NEURON = "Neuron";
	public static final String SYNAPSE_STORE = "SynapseStore";
	public static final String SYNAPSE = "Synapse";
	public static final String NEURAL_NETWORK = "NeuralNetwork";

	public HashMap<Integer, List<Neuron>> layers;
	List<Synapse> synapses;

	public NeuralNetwork(int[] topology) {
		OUTPUT_LAYER = topology.length - 1;
		layers = new HashMap<Integer, List<Neuron>>();

		int id = 1;
		for (int layer = 0; layer < topology.length; layer++) {
			int l = topology[layer];
			for (int j = 0; j < l; j++) {
				Neuron neuron = new Neuron(layer, id++);	
				List<Neuron> layerList = layers.get(layer);
				if (layerList == null) {
					layerList = new ArrayList<Neuron>();
					layers.put(layer, layerList);
				}
				layerList.add(neuron);
			}
		}
		synapses = new ArrayList<Synapse>();
		// Generate synapses on front pass to assign to outgoing
		for (int i = INPUT_LAYER; i < OUTPUT_LAYER; i++) {
			List<Neuron> thisLayer = layers.get(i);
			List<Neuron> nextLayer = layers.get(i+1);
			for (Neuron n : thisLayer) {
				for (Neuron other : nextLayer) {
					Synapse s = new Synapse();
					s.left_neuron = n;
					s.right_neuron = other;
					s.weight = 1 - 2 * Math.random();
					n.outgoing_synapses.add(s);
					other.incoming_synapses.add(s);
					synapses.add(s);
				}
			}
		}					

	}

	/***
	 * Initialize NeuralNetwork from file
	 * @param filename name of file containing Neural Network
	 */
	public NeuralNetwork(String filename) {
		try {
			LoadNeuralNetworkFromFile(filename);
		} catch (FileNotFoundException | XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		} 
	}

	/***
	 * Loads a neural network from file
	 * @param filename
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public void LoadNeuralNetworkFromFile(String filename) throws XMLStreamException, FileNotFoundException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// Setup a new eventReader
		InputStream in = new FileInputStream("NeuralNetworks/" + filename);
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		int currentLayer = 0;
		List<Neuron> neuronList = null;
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart().equals(LAYER_STORE)) {
					System.out.println("End of LayerStore");
				}
			}
			
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				
				if (startElement.getName().getLocalPart().equals(LAYER_STORE)) {
					layers = new HashMap<Integer, List<Neuron>>();
					
					
				}
				
				if (startElement.getName().getLocalPart().equals(LAYER)) {
					Iterator<Attribute> attributes = startElement.getAttributes();
					currentLayer = Integer.parseInt(attributes.next().getValue());
					neuronList = new ArrayList<Neuron>();
					layers.put(currentLayer, neuronList);
				}
				
				if (startElement.getName().getLocalPart().equals(NEURON)) {
					String idString = "";
					String biasString = "";
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attr  = attributes.next();
						switch (attr.getName().toString()) {
						case "id":
							idString = attr.getValue();
							break;
						case "bias":
							biasString = attr.getValue();
							break;
						default:
							break;
						}
					}
					int id = Integer.parseInt(idString);
					double bias = Double.parseDouble(biasString);
					Neuron n = new Neuron(currentLayer, id);
					n.bias = bias;
					neuronList.add(n);
				}
				
				if (startElement.getName().getLocalPart().equals(SYNAPSE_STORE)) {
					synapses = new ArrayList<Synapse>();
				}
				
				if (startElement.getName().getLocalPart().equals(SYNAPSE)) {
					Synapse s = new Synapse();
					String leftNeuronString = "";
					String rightNeuronString = "";
					String weightString = "";
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attr  = attributes.next();
						switch (attr.getName().toString()) {
						case "left_neuron":
							leftNeuronString = attr.getValue();
							break;
						case "right_neuron":
							rightNeuronString = attr.getValue();
							break;
						case "weight":
							weightString = attr.getValue();
							break;
						}
					}
					s.left_neuron = findNeuronById(Integer.parseInt(leftNeuronString));
					s.right_neuron = findNeuronById(Integer.parseInt(rightNeuronString));
					s.weight = Double.parseDouble(weightString);
					s.left_neuron.outgoing_synapses.add(s);
					s.right_neuron.incoming_synapses.add(s);
					synapses.add(s);
				}
			}
		}
		System.out.println("Finished loading from XML");
	}
	
	private Neuron findNeuronById(int id) {
		for (List<Neuron> layer : layers.values()) {
			for (Neuron n : layer) {
				if (n.id == id) {
					return n;
				}
			}
		}
		return null;
	}

	/***
	 * Saves neural network to file
	 * @param filename
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public void SaveToFile(String filename) throws FileNotFoundException, XMLStreamException {
		System.out.println("Saving Neural network to file " + filename);
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

		XMLEventWriter writer = outputFactory.createXMLEventWriter(new FileOutputStream("NeuralNetworks/" + filename));
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");

		// Create and write start tag
		StartDocument startDocument = eventFactory.createStartDocument();
		writer.add(startDocument);
		writer.add(end);
		writer.add(eventFactory.createStartElement("", "", NEURAL_NETWORK)); 
		writer.add(eventFactory.createStartElement("", "", LAYER_STORE));
		writer.add(end);
		for (int i = 0; i <= OUTPUT_LAYER; i++) {
			List<Neuron> neurons = layers.get(i);
			// Create layer open tag					
			writer.add(tab);
			writer.add(eventFactory.createStartElement("", "", LAYER));
			writer.add(eventFactory.createAttribute("id", i + ""));
			writer.add(end);

			// Add neurons
			for (Neuron n : neurons) {
				writer.add(tab);
				writer.add(tab);
				writer.add(eventFactory.createStartElement("", "", NEURON));
				writer.add(eventFactory.createAttribute("id", n.id + ""));
				writer.add(eventFactory.createAttribute("bias", n.bias + ""));
				writer.add(eventFactory.createEndElement("", "", NEURON));
				writer.add(end);
			}
			// Close tag
			writer.add(tab);
			writer.add(eventFactory.createEndElement("", "", LAYER_STORE));
			writer.add(end);

		}
		writer.add(eventFactory.createEndElement("", "", NEURON));
		writer.add(end);

		// Write Synapses tag
		writer.add(eventFactory.createStartElement("", "", SYNAPSE_STORE));
		writer.add(end);

		// Write each synapse
		for (Synapse s : synapses) {
			writer.add(tab);
			writer.add(eventFactory.createStartElement("", "", SYNAPSE));
			writer.add(eventFactory.createAttribute("left_neuron", s.left_neuron.id + ""));
			writer.add(eventFactory.createAttribute("right_neuron", s.right_neuron.id + ""));
			writer.add(eventFactory.createAttribute("weight", s.weight + ""));
			writer.add(eventFactory.createEndElement("", "", SYNAPSE));
			writer.add(end);
		}

		writer.add(eventFactory.createEndElement("", "", SYNAPSE_STORE));
		writer.add(end);
		writer.add(eventFactory.createEndElement("", "", NEURAL_NETWORK));
		writer.add(end);
		// End document
		writer.add(eventFactory.createEndDocument());
		writer.close();
	}

	public void BackPropagation(List<TrainingTuple> data) {
		BackPropagation(data, 5000, 0.1);
	}

	public void BackPropagation(List<TrainingTuple> data, int epochs, double w_threshold) {
		System.out.println("Starting Back propagation");
		long startTime = System.currentTimeMillis();
		boolean running = true;
		boolean classify = true;
		int correctlyClassified = 0;
		double c_threshold = 0.1;
		double acc = 0.0;
		int epoch = 1;
		double largest_delta_w = Integer.MIN_VALUE;
		while (running) {	
			correctlyClassified = 0;
			largest_delta_w = Integer.MIN_VALUE;
			double learning_rate = 1.0 / epoch;
			for (TrainingTuple tuple : data) {
				// Propagate inputs forward
				List<Neuron> inputNeurons = layers.get(INPUT_LAYER);
				for (int i = 0; i < inputNeurons.size(); i++) {
					Neuron n = inputNeurons.get(i);
					n.SetInputValue(tuple.input.get(i));

				}
				for (int i = INPUT_LAYER; i <= OUTPUT_LAYER; i++) {
					List<Neuron> layerNeurons = layers.get(i);
					for (Neuron n : layerNeurons) {

						n.ComputeOutputValue();
					}
				}
				
				// Check classification ability
				List<Neuron> outputNeurons = layers.get(OUTPUT_LAYER);
				if (classify) {
					// For PacMan, assume that only one direction is correct
					// Thus, the highest scoring output determines the direction
					int bestDirection = -1;
					double bestValue = -1;
					for (int j = 0; j < outputNeurons.size(); j++) {
						Neuron n = outputNeurons.get(j);
						if (n.outputValue > bestValue) {
							bestValue = n.outputValue;
							bestDirection = j;
						}
					}
					// Search through class labels of training data to check
					boolean correct = false;
					for (int i = 0; i < tuple.output.size(); i++) {						
						if (tuple.output.get(i) > 0.9) {
							if (bestDirection == i) {
								// Classified as true
								correct = true;
								break;
							}
						}
					}
					if (correct) {
						correctlyClassified++;
					}
				}

				// Backpropagate the errors				
				for (int j = 0; j < outputNeurons.size(); j++) {
					Neuron n = outputNeurons.get(j);
					n.error = n.outputValue * (1 - n.outputValue) * (tuple.output.get(j) - n.outputValue);
				}
				for (int j = OUTPUT_LAYER - 1; j >= 0; j--) {
					List<Neuron> layerNeurons = layers.get(j);
					for (Neuron n : layerNeurons) {
						double sum_error = 0;
						for (Synapse s : n.outgoing_synapses) {
							sum_error += s.right_neuron.error * s.weight;
						}
						n.error = n.outputValue * (1 - n.outputValue) * sum_error;
					}
				}

				// Weight updates
				for (Synapse s : synapses) {
					double delta_w = learning_rate * s.right_neuron.error * s.left_neuron.outputValue;
					if (delta_w > largest_delta_w) {
						largest_delta_w = delta_w;
					}
					s.weight += delta_w;
				}
				// Bias update
				for (List<Neuron> layer : layers.values()) {
					for (Neuron n : layer) {
						double delta_b = learning_rate * n.error;
						n.bias += delta_b;
					}
				}
			}
			// Stopping conditions
			if (epoch >= epochs) {
				running = false;
			}
			if (largest_delta_w < w_threshold) {
				running = false;
			}
			double misClassified = 1 - (double)correctlyClassified / data.size();
			if (misClassified < c_threshold) {
				running = false;
			}
			acc = 1 - misClassified;
			epoch++;
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Took " + (endTime - startTime) + " ms. Stopped at epoch " + epoch + ", with delta_w: " + largest_delta_w
				+ ". Acc=" + acc);
	}
	
	public void BackPropagationTest(List<TrainingTuple> data, int epochs, double w_threshold) {
		boolean running = true;
		int epoch = 1;
		double largest_delta_w = Integer.MIN_VALUE;
		while (running) {	
			largest_delta_w = Integer.MIN_VALUE;
			double learning_rate = 0.9;
			for (TrainingTuple tuple : data) {
				// Propagate inputs forward
				List<Neuron> inputNeurons = layers.get(INPUT_LAYER);
				for (int i = 0; i < inputNeurons.size(); i++) {
					Neuron n = inputNeurons.get(i);
					n.SetInputValue(tuple.input.get(i));

				}
				for (int i = INPUT_LAYER; i <= OUTPUT_LAYER; i++) {
					List<Neuron> layerNeurons = layers.get(i);
					for (Neuron n : layerNeurons) {

						n.ComputeOutputValue();
					}
				}

				// Backpropagate the errors
				List<Neuron> outputNeurons = layers.get(OUTPUT_LAYER);
				for (int j = 0; j < outputNeurons.size(); j++) {
					Neuron n = outputNeurons.get(j);
					n.error = n.outputValue * (1 - n.outputValue) * (tuple.output.get(j) - n.outputValue);
				}
				for (int j = OUTPUT_LAYER - 1; j >= 0; j--) {
					List<Neuron> layerNeurons = layers.get(j);
					for (Neuron n : layerNeurons) {
						double sum_error = 0;
						for (Synapse s : n.outgoing_synapses) {
							sum_error += s.right_neuron.error * s.weight;
						}
						n.error = n.outputValue * (1 - n.outputValue) * sum_error;
					}
				}

				// Weight updates
				for (Synapse s : synapses) {
					double delta_w = learning_rate * s.right_neuron.error * s.left_neuron.outputValue;
					if (delta_w > largest_delta_w) {
						largest_delta_w = delta_w;
					}
					s.weight += delta_w;
					int x = 0;
				}
				// Bias update
				for (List<Neuron> layer : layers.values()) {
					for (Neuron n : layer) {
						double delta_b = learning_rate * n.error;
						n.bias += delta_b;
					}
				}
			}
			// Stopping conditions
			if (epoch >= epochs) {
				running = false;
			}
			if (largest_delta_w < w_threshold) {
				running = false;
			}

			epoch++;
		}
		System.out.println("Stopped at epoch " + epoch + ", with delta_w: " + largest_delta_w);
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
//			System.out.println(n.toString());
		}
		return outputs;
	}
}
