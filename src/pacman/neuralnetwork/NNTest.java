package pacman.neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NNTest {

	public static void main(String[] args) {
		LoadTest();
		// TODO Auto-generated method stub
		System.out.println("--------------------------------------------------------------------------------------------------------------------");
		System.out.println("And test");
		AndTest();
		
		System.out.println("--------------------------------------------------------------------------------------------------------------------");
		System.out.println("Or test");
		OrTest();
		
		System.out.println("--------------------------------------------------------------------------------------------------------------------");
		System.out.println("XOR test");
		XOrTest();
	}
	
	private static void LoadTest() {
		NeuralNetwork nn = new NeuralNetwork("xor.xml");
	}
	
	private static void AndTest() {
		int[] topology = new int[] {
				2, 1
		};
		NeuralNetwork nn = new NeuralNetwork(topology);

		TrainingTuple t1 = new TrainingTuple();
		t1.input.add(1.0); t1.input.add(1.0); t1.output.add(1.0);
		
		TrainingTuple t2 = new TrainingTuple();
		t2.input.add(1.0); t2.input.add(0.0); t2.output.add(0.0);
		
		TrainingTuple t3 = new TrainingTuple();
		t3.input.add(0.0); t3.input.add(1.0); t3.output.add(0.0);
		
		TrainingTuple t4 = new TrainingTuple();
		t4.input.add(0.0); t4.input.add(0.0); t4.output.add(0.0);
		
		List<TrainingTuple> data = new ArrayList<TrainingTuple>();
		data.add(t1);
		data.add(t2);
		data.add(t3);
		data.add(t4);
		
		nn.BackPropagation(data, 10000, 0.001); 
		TrainingTuple test = t3;
		Random rand = new Random();
		switch (rand.nextInt(4)) {
		case 0:
			test = t1;
			break;
		case 1:
			test = t1;
			break;
		case 2:
			test = t3;
			break;
		case 3:
			test = t4;
			break;
		default:
			break;
		}
		System.out.println("Input: {" + test.input.get(0) + ", " + test.input.get(1) + "}, expected output: " + test.output.get(0));
		List<Double> results = nn.fire(test.input);
		double res = results.get(0);
		if (res < 0.5) {
			res = 0.0;
		} else {
			res = 1.0;
		}
		System.out.println("Result: " + (res == test.output.get(0)));
	}
	
	private static void OrTest() {
		int[] topology = new int[] {
				2, 1
		};
		NeuralNetwork nn = new NeuralNetwork(topology);

		TrainingTuple t1 = new TrainingTuple();
		t1.input.add(1.0); t1.input.add(1.0); t1.output.add(1.0);
		
		TrainingTuple t2 = new TrainingTuple();
		t2.input.add(1.0); t2.input.add(0.0); t2.output.add(1.0);
		
		TrainingTuple t3 = new TrainingTuple();
		t3.input.add(0.0); t3.input.add(1.0); t3.output.add(1.0);
		
		TrainingTuple t4 = new TrainingTuple();
		t4.input.add(0.0); t4.input.add(0.0); t4.output.add(0.0);
		
		List<TrainingTuple> data = new ArrayList<TrainingTuple>();
		data.add(t1);
		data.add(t2);
		data.add(t3);
		data.add(t4);
		
		nn.BackPropagation(data, 10000, 0.001); 
		TrainingTuple test = t3;
		Random rand = new Random();
		switch (rand.nextInt(4)) {
		case 0:
			test = t1;
			break;
		case 1:
			test = t1;
			break;
		case 2:
			test = t3;
			break;
		case 3:
			test = t4;
			break;
		default:
			break;
		}
		System.out.println("Input: {" + test.input.get(0) + ", " + test.input.get(1) + "}, expected output: " + test.output.get(0));
		List<Double> results = nn.fire(test.input);
		double res = results.get(0);
		if (res < 0.5) {
			res = 0.0;
		} else {
			res = 1.0;
		}
		System.out.println("Result: " + (res == test.output.get(0)));
	}

	private static void XOrTest() {
		int[] topology = new int[] {
				2, 2, 1
		};
		NeuralNetwork nn = new NeuralNetwork(topology);

		TrainingTuple t1 = new TrainingTuple();
		t1.input.add(1.0); t1.input.add(1.0); t1.output.add(0.0);
		
		TrainingTuple t2 = new TrainingTuple();
		t2.input.add(1.0); t2.input.add(0.0); t2.output.add(1.0);
		
		TrainingTuple t3 = new TrainingTuple();
		t3.input.add(0.0); t3.input.add(1.0); t3.output.add(1.0);
		
		TrainingTuple t4 = new TrainingTuple();
		t4.input.add(0.0); t4.input.add(0.0); t4.output.add(0.0);
		
		List<TrainingTuple> data = new ArrayList<TrainingTuple>();
		data.add(t1);
		data.add(t2);
		data.add(t3);
		data.add(t4);
		
		nn.BackPropagation(data, 10000, 0.001); 
		TrainingTuple test = t3;
		Random rand = new Random();
		switch (rand.nextInt(4)) {
		case 0:
			test = t1;
			break;
		case 1:
			test = t1;
			break;
		case 2:
			test = t3;
			break;
		case 3:
			test = t4;
			break;
		default:
			break;
		}
		System.out.println("Input: {" + test.input.get(0) + ", " + test.input.get(1) + "}, expected output: " + test.output.get(0));
		List<Double> results = nn.fire(test.input);
		double res = results.get(0);
		if (res < 0.5) {
			res = 0.0;
		} else {
			res = 1.0;
		}
		System.out.println("Result: " + (res == test.output.get(0)));
		try {
			nn.SaveToFile("xor.xml");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
