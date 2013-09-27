package pacman.entries.pacman.GA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import pacman.Executor;
import pacman.controllers.examples.StarterGhosts;
import pacman.entries.pacman.FSMPacMan;

public class GeneticAlgorithm {
	public static String GA_FILE_NAME = "myData/bestGene.txt";
	static int CHROMOSOME_SIZE=37;
	static int POPULATION_SIZE=10;
	static int EVALUATION_ITERATIONS = 10;
	static int MAX_GENERATIONS = 10;
	ArrayList<Gene> mPopulation;

	public GeneticAlgorithm(int size) {
		mPopulation = new ArrayList<Gene>();
		for(int i = 0; i < size; i++){
			Gene entry = new Gene();
			entry.randomizeChromosome();
			mPopulation.add(entry);
		}
	}

	/**
	 * For all members of the population, runs a heuristic that evaluates their fitness
	 * based on their phenotype. The evaluation of this problem's phenotype is fairly simple,
	 * and can be done in a straightforward manner. In other cases, such as agent
	 * behavior, the phenotype may need to be used in a full simulation before getting
	 * evaluated (e.g based on its performance)
	 */
	public void evaluateGeneration(){
		for(int i = 0; i < mPopulation.size(); i++){
			// evaluation of the fitness function for each gene in the population goes HERE
			Gene g = mPopulation.get(i);
			// Run game experiments to determine fitness
			Executor exec = new Executor();

			double avg = 0;
			int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;

			for (int j = 0; j < EVALUATION_ITERATIONS; j++) {
				ResultBean result = exec.runSingleExperiment(new EvolvedPacMan(g.getPhenoType()), new StarterGhosts());
				double ppt = result.getPointsPerTime();
//				System.out.println(j + ": Result = " + result.score + ", level: " + (result.getLevel()) + ", time: " + result.totalTime + ", points per time: " + ppt);  
				avg += result.score;
				if (result.score > max) {
					max = result.score;
				}
				if (result.score < min) {
					min = result.score;
				} 
			}
			avg /= EVALUATION_ITERATIONS;
			// Simple fitness rule: fitness = score
			// Other fitness rules to consider: using level, total time and points per time in combination
			System.out.println("\t" + i +": Fitness: " + avg);
			g.setFitness(avg); 
		}
	}
	/**
	 * With each gene's fitness as a guide, chooses which genes should mate and produce offspring.
	 * The offspring are added to the population, replacing the previous generation's Genes either
	 * partially or completely. The population size, however, should always remain the same.
	 * If you want to use mutation, this function is where any mutation chances are rolled and mutation takes place.
	 */
	public void produceNextGeneration(){
		// use one of the offspring techniques suggested in class (also applying any mutations) HERE
		int replacements = 2;
		Collections.sort(mPopulation); // Lowest first
		
		// Print fitness sorted
		System.out.println("Worst to best:");
		for (Gene g : mPopulation) {
			System.out.println("fitness: " + g.getFitness());
		}
		System.out.println("--------------------------------");
		// Remove the worst
		for (int i = 0; i < replacements; i++) {
			mPopulation.remove(0);
		}

		// Force the 100 best to mate and harvest and mutate their children
		Random rand = new Random();
		double mutateProb = 0.3;
		Collections.reverse(mPopulation);
		for (int i = 0; i < replacements; i += 2) {
			Gene[] children = mPopulation.get(i).reproduce(mPopulation.get(i+1));
			mPopulation.add(children[0]);
			mPopulation.add(children[1]);
			if (rand.nextFloat() < mutateProb) {
				children[0].mutate();
			}
			if (rand.nextFloat() < mutateProb) {
				children[1].mutate();
			}
		}
	}
	
	public Gene getBestGene() {
		Collections.sort(mPopulation); // Lowest first
		return mPopulation.get(mPopulation.size() - 1);
	}

	public static void main(String[] args)
	{
		GeneticAlgorithm population = new GeneticAlgorithm(POPULATION_SIZE);
		int generationCount = 0;

		System.out.println("Starting Genetic Algorithm");
		System.out.println("Initial generation");
		
		population.evaluateGeneration();
		population.produceNextGeneration();

		while (true) {
			System.out.println("Generation: " + generationCount);
			// --- evaluate current generation:
			population.evaluateGeneration();

			// produce next generation:
			population.produceNextGeneration();
			generationCount++;
			if (generationCount > MAX_GENERATIONS) {
				break;
			}
			Gene temp = population.getBestGene();
			temp.getPhenoType().SaveToFile("myData/temp.txt", temp.getFitness());
		}
		Gene best = population.getBestGene();
		System.out.println(best.getPhenoType().toString());
		best.getPhenoType().SaveToFile(GA_FILE_NAME + " fitness " + best.getFitness(), best.getFitness());
	}
}
