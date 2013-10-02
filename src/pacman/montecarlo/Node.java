package pacman.montecarlo;

import java.util.ArrayList;
import java.util.List;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Class to store node information, e.g.
 * state, children, parent, accumulative reward, visited times
 * @author dariusv
 * @modified A. Hartzen
 *
 */
public class Node{
	
	public int NodePos;
	public List<Node> children = new ArrayList<Node>();
	public Node parent = null;
	public int parentAction=-1;
	public float reward =0;
	public int timesvisited = 0;
	public MOVE move;
	
	public boolean IsFullyExpanded(Game game) {
		int[] n = game.getNeighbouringNodes(NodePos);
		return n.length == children.size();
	}
	
	
}
