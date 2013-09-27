package pacman.entries.pacman.GA;

public class ResultBean {
	public int score;
	public int totalTime;
	public int level;
	
	public double getPointsPerTime() {
		return (double)score / totalTime;
	}
	
	public double getTimePerLevel() {
		return (double)totalTime / level;
	}
	
	public int getLevel() {
		return level + 1;
	}
}
