package pacman.entries.pacman.GA;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhenoBean {
	// This is also the ordering in the genotype
	public int edibleTime; // 4 bits
	public int closeEnoughToSearch; // 6 bits
	public int closeEnoughToFlee; // 6 bits
	public int eatWeight; // 5 bits
	public boolean fleeBeAfraid; // 1 bit
	public boolean eatBeAfraid; // 1 bit
	public int fleeDepth; // 7 bits
	public int eatDepth; // 7 bits
	int[] genoType;
	
	public PhenoBean(String filename) {
		LoadFromFile(filename); 
	}
	
	public PhenoBean(int[] genotype) {
		this.genoType = genotype;
		String bin ="";
	    for(int genom : genotype) {
	        bin=bin+genom;
	    }
	    
	    int i = 0;
	    int j = 4;
	    edibleTime = Integer.parseInt(bin.substring(i, j), 2);
	    i = j;
	    j += 6;
	    closeEnoughToSearch = Integer.parseInt(bin.substring(i, j), 2);
	    i = j;
	    j += 6;
	    closeEnoughToFlee = Integer.parseInt(bin.substring(i, j), 2);
	    i = j;
	    j += 5;
	    eatWeight = Integer.parseInt(bin.substring(i, j), 2);
	    i = j;
	    j += 1;
	    fleeBeAfraid = Integer.parseInt(bin.substring(i, j), 2) == 1;
	    i = j;
	    j += 1;
	    eatBeAfraid = Integer.parseInt(bin.substring(i, j), 2) == 1;
	    i = j;
	    j += 7;
	    fleeDepth = Integer.parseInt(bin.substring(i, j), 2);
	    i = j;
	    j += 7;
	    eatDepth = Integer.parseInt(bin.substring(i, j), 2); 
	}
	
	public void SaveToFile(String filename, double score) {
		String save = edibleTime + "," + closeEnoughToSearch + "," + closeEnoughToFlee + "," + eatWeight + "," + (fleeBeAfraid ? "1" : "0")
				+ "," + (eatBeAfraid ? "1" : "0") +	"," + fleeDepth + "," + eatDepth;
		Path path = Paths.get(filename);
		List<String> lines = new ArrayList<>();
		lines.add(save);
		lines.add("Fitness: " + score);
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void LoadFromFile(String filename) {
		Path path = Paths.get(filename);
		try {
			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
			String[] parts = lines.get(0).split(",");
			this.edibleTime = Integer.parseInt(parts[0]);
			this.closeEnoughToSearch = Integer.parseInt(parts[1]);
			this.closeEnoughToFlee = Integer.parseInt(parts[2]);
			this.eatWeight = Integer.parseInt(parts[3]);
			this.fleeBeAfraid = Integer.parseInt(parts[4]) == 1;
			this.eatBeAfraid = Integer.parseInt(parts[5]) == 1;
			this.fleeDepth = Integer.parseInt(parts[6]);
			this.eatDepth = Integer.parseInt(parts[7]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "Genotype: " + Arrays.toString(genoType) + "\nedibleTime=" + edibleTime + ", closeEnoughToSearch=" + closeEnoughToSearch +
				", closeEnoughToFlee=" + closeEnoughToFlee + ", eatWeight=" + eatWeight + ", fleeBeAfraid=" + fleeBeAfraid + 
				", eatBeAfraid=" + eatBeAfraid + ", fleeDepth=" + fleeDepth + ", eatDepth=" + eatDepth;
	}
}
