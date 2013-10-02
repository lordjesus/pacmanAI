package pacman.neuralnetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import dataRecording.DataTuple;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class NeuralPacMan extends Controller<MOVE> {
	private NeuralNetwork nn;
	private List<String> parameters;
	public NeuralPacMan() {
		nn = new NeuralNetwork("PacMan.xml");
		parameters = loadParameters();
	}

	private List<String> loadParameters() {
		List<String> param = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("NeuralNetworks/config.txt"));
			String line = br.readLine();
			while (!line.equals(":input:")) {
				line = br.readLine();
			}
			while (br.ready()) {
				line = br.readLine();
				param.add(line.trim());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return param;
	}
	
	private List<Double> generateInput(DataTuple tuple) {
		List<Double> input = new ArrayList<Double>();
		
		for (String param : parameters) {
			input.add(PacManTrainer.getParamValue(param, tuple));
		}
		
		return input;
	}

	private MOVE getBestMove(List<Double> output, MOVE[] possibleMoves) {
		MOVE myMove = MOVE.NEUTRAL;
		int bestMove = -1;
		double bestVal = -1;
		for (MOVE move : possibleMoves) {
			int i = move.ordinal();
			double val = output.get(i);
			if (val > bestVal) {
				bestMove = i;
				bestVal = val;
			}
		}
		myMove = MOVE.values()[bestMove];
		System.out.println("Outputs: " + output);
		System.out.println("Best move is " + myMove.name().toLowerCase() + ", with value " + bestVal);
		return myMove;
	}
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		DataTuple tuple = new DataTuple(game, MOVE.NEUTRAL);
		List<Double> input = generateInput(tuple);
		List<Double> output = nn.fire(input);
		MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		
		
		
		return getBestMove(output, possibleMoves); 
	}

}
