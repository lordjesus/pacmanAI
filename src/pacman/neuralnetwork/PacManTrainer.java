package pacman.neuralnetwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;

public class PacManTrainer {
	public static void main(String[] args) {
		
		List<TrainingTuple> data = PrepareData();
		
		
		
		if (data.size() > 0) {
			int hiddenUnits = 10;
			int[] topology = new int[] {
					data.get(0).input.size(),
					hiddenUnits,
					data.get(0).output.size()
			};
			NeuralNetwork nn = new NeuralNetwork(topology);
			nn.BackPropagation(data, 1000, 0.0001);
			try {
				nn.SaveToFile("PacMan.xml");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	private static List<TrainingTuple> PrepareData() {
		List<TrainingTuple> data = new ArrayList<TrainingTuple>();
		try {
			List<String> parameters = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader("NeuralNetworks/config.txt"));
			String line = br.readLine();
			while (!line.equals(":input:")) {
				line = br.readLine();
			}
			while (br.ready()) {
				line = br.readLine();
				parameters.add(line.trim());
			}
			
			DataTuple[] tuples = DataSaverLoader.LoadPacManData();

			for (DataTuple tuple : tuples) {
				TrainingTuple tt = new TrainingTuple();
				for (int i = 0; i < 4; i++) {
					if (i == tuple.DirectionChosen.ordinal()) {
						tt.output.add(1.0);
					} else {
						tt.output.add(0.0);
					}
				}
				for (String param : parameters) {
					tt.input.add(getParamValue(param, tuple));
				}
				data.add(tt);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static double getParamValue(String param, DataTuple tuple) {
		switch (param) {
		case "mazeIndex":
			return tuple.normalizeLevel(tuple.mazeIndex);
		case "currentLevel":
			return tuple.normalizeLevel(tuple.currentLevel);
		case "pacmanPosition":
			return tuple.normalizePosition(tuple.pacmanPosition);
		case "pacmanLivesLeft":
			return (double)tuple.pacmanLivesLeft / 4;
		case "currentScore":
			return tuple.normalizeCurrentScore(tuple.currentScore);
		case "totalGameTime":
			return tuple.normalizeTotalGameTime(tuple.totalGameTime);
		case "currentLevelTime":
			return tuple.normalizeCurrentLevelTime(tuple.currentLevelTime);
		case "numOfPillsLeft":
			return tuple.normalizeNumberOfPills(tuple.numOfPillsLeft);
		case "numOfPowerPillsLeft":
			return tuple.normalizeNumberOfPowerPills(tuple.numOfPowerPillsLeft);
		case "isBlinkyEdible":
			return tuple.normalizeBoolean(tuple.isBlinkyEdible);
		case "isInkyEdible":
			return tuple.normalizeBoolean(tuple.isInkyEdible);
		case "isPinkyEdible":
			return tuple.normalizeBoolean(tuple.isPinkyEdible);
		case "isSueEdible":
			return tuple.normalizeBoolean(tuple.isSueEdible);
		case "blinkyDist":
			return tuple.normalizeDistance(tuple.blinkyDist);
		case "inkyDist":
			return tuple.normalizeDistance(tuple.inkyDist);
		case "pinkyDist":
			return tuple.normalizeDistance(tuple.pinkyDist);
		case "sueDist":
			return tuple.normalizeDistance(tuple.sueDist);
		case "blinkyDir":
			return tuple.blinkyDir.ordinal() / 3.0;
		case "inkyDir":
			return tuple.inkyDir.ordinal() / 3.0;
		case "pinkyDir":
			return tuple.pinkyDir.ordinal() / 3.0;
		case "sueDir":
			return tuple.sueDir.ordinal() / 3.0;		
		default:
			break;
		}
		return 0.0;
	}
}
