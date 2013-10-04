package daja.coolcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Search {
	int depthLimit, ghostLimit = 40;
	Game game;
	int startIndex;
	HashMap<Integer, Integer> ghostMapping = new HashMap<>();
	public final static int GHOST_PENALTY = -500;
	int closestGhostDist;
	double ghostRatio;
	boolean beAfraid; // true if avoid ghosts
	int timeSinceLastEat;
	double eatRatio;
	private double eatWeight;
	public ArrayList<Integer> outputList;
	
	public Search(Game game, int depth, int startIndex) {
		this.depthLimit = depth;
		this.game = game;
		this.startIndex = startIndex;
		mapGhosts();
	}
	
	public Search(Game game, int depth, int startIndex, boolean beAfraid, int timeSinceLastEat, double eatWeight) {
		this.depthLimit = depth;
		this.eatWeight = eatWeight;
		this.game = game;
		this.startIndex = startIndex;
		this.beAfraid = beAfraid;
		this.timeSinceLastEat = timeSinceLastEat;
		eatRatio = 1 + timeSinceLastEat / (double)eatWeight;
		if (beAfraid) {
			mapGhosts();
		}
	}
	
	private void mapGhosts() {
		
		closestGhostDist = Integer.MAX_VALUE;
		for(GHOST ghost : GHOST.values()) {
			int index = game.getGhostCurrentNodeIndex(ghost);
			int dist = game.getShortestPathDistance(startIndex, index);
			if (dist < closestGhostDist) {
				closestGhostDist = dist;
			}
			ghostMapping.put(index, GHOST_PENALTY);
			MOVE dir = game.getGhostLastMoveMade(ghost);
			
			recurseGhosts(index, dir, 0); 
		}
		if (closestGhostDist > ghostLimit) {
			ghostRatio = 1;
		} else {
			ghostRatio = ghostLimit - closestGhostDist;
		}
	}
	
	private void recurseGhosts(int n, MOVE dir, int depth) {
		if (depth > ghostLimit) {
			return;
		}
		int nextIndices[] = game.getNeighbouringNodes(n, dir);
		for (int next : nextIndices) {
			if (next == startIndex) {
				continue;
			}
			int penalty = GHOST_PENALTY + depth * 10;
			if (ghostMapping.containsKey(next)) {
				if (ghostMapping.get(next) < penalty) {
					continue;
				}
			}
			ghostMapping.put(next, penalty);
			recurseGhosts(next, game.getNextMoveTowardsTarget(n, next, DM.PATH), depth + 1);
		}
	}
	
	public Stack<Integer> GetMoveList() {
		Queue<Node> openList = new LinkedList<>();		
		
		Node start = new Node();
		start.index = startIndex;
		start.value = 0;
		start.depth = 0;
		
		openList.add(start);
		ArrayList<Node> exploredList = new ArrayList<>();
		
		ArrayList<Node> leafNodes = new ArrayList<Node>();
		
		while (!openList.isEmpty()) {
			Node node = openList.poll();
			exploredList.add(node);
			
			int[] nindices = game.getNeighbouringNodes(node.index);
			for (int n : nindices) {
				Node neighbour = new Node();
				neighbour.index = n;
				if (exploredList.contains(neighbour)) {
					continue;
				}
				neighbour.depth = node.depth + 1;
				neighbour.value = GetValueOfNode(neighbour.index);
				neighbour.parent = node;
				if (neighbour.value > -500) {
					// Only add nodes that aren't a ghost
					if (node.depth < depthLimit) {
						openList.add(neighbour);
					} else {
						leafNodes.add(neighbour);
					}
				}
			}
		}		
		
		// Backtrack leaf nodes to get highest scoring path
		Node bestLeaf = null;
		double bestValue = Integer.MIN_VALUE;
		
		for (Node leaf : leafNodes) {
			double val = 0;
			Node n = leaf;
			while (n.depth > 0) {
				val += n.value;
				n = n.parent;
			}
			
			if (val > bestValue) {
				bestLeaf = leaf;
				bestValue = val;
			}
		}
		
		if (bestLeaf == null) {
//			System.out.println("Sorry, I failed you");
			return null;
		}
		
		Stack<Integer> retval = new Stack<Integer>();
		Node nextNode = bestLeaf;
		while (nextNode.depth != 0) {
			retval.push(nextNode.index);
			nextNode = nextNode.parent;			
		}		
		return retval;	
	}
	
	public MOVE BeginSearch() {		
		Queue<Node> openList = new LinkedList<>();		
		
		Node start = new Node();
		start.index = startIndex;
		start.value = 0;
		start.depth = 0;
		
		openList.add(start);
		ArrayList<Node> exploredList = new ArrayList<>();
		
		ArrayList<Node> leafNodes = new ArrayList<Node>();
		
		while (!openList.isEmpty()) {
			Node node = openList.poll();
			exploredList.add(node);
			
			int[] nindices = game.getNeighbouringNodes(node.index);
			for (int n : nindices) {
				Node neighbour = new Node();
				neighbour.index = n;
				if (exploredList.contains(neighbour)) {
					continue;
				}
				neighbour.depth = node.depth + 1;
				neighbour.value = GetValueOfNode(neighbour.index);
				neighbour.parent = node;
				if (neighbour.value > -500) {
					// Only add nodes that aren't a ghost
					if (node.depth < depthLimit) {
						openList.add(neighbour);
					} else {
						leafNodes.add(neighbour);
					}
				}
			}
		}		
		
		// Backtrack leaf nodes to get highest scoring path
		Node bestLeaf = null;
		double bestValue = Integer.MIN_VALUE;
		
		for (Node leaf : leafNodes) {
			double val = 0;
			Node n = leaf;
			while (n.depth > 0) {
				val += n.value;
				n = n.parent;
			}
			
			if (val > bestValue) {
				bestLeaf = leaf;
				bestValue = val;
			}
		}
		
		if (bestLeaf == null) {
//			System.out.println("Sorry, I failed you");
			return MOVE.NEUTRAL;
		}
		Node nextNode = bestLeaf;
		ArrayList<Integer> list = new ArrayList<Integer>();
		while (nextNode.depth != 1) {
			nextNode = nextNode.parent;
			list.add(nextNode.index);
		}
		outputList = list;
		MOVE move = game.getNextMoveTowardsTarget(startIndex, nextNode.index, DM.PATH);
//		System.out.println("Best value: " + bestValue + ", number of leaves: " + leafNodes.size() + ", MOVE: " + move.name()); 
		return move;	
	}
	
	private double GetValueOfNode(int index) {
		double value = 0;
		if (containsIndex(game.getActivePillsIndices(), index)) {
			value += 10 * eatRatio;
		} 
		if (containsIndex(game.getActivePowerPillsIndices(), index)) {
			if (beAfraid) {
				value += 5000;
			}
			value += 50;
		}
		
		// Check ghosts
		if (ghostMapping.containsKey(index)) {
			value += ghostMapping.get(index);
		}
		return value;
	}
	
	private boolean containsIndex(int[] array, int index) {
		for (int i : array) {
			if (i == index) {
				return true;
			}
		}
		return false;
	}
}

class Node {
	public Node parent;
	public int depth;
	boolean closed;
	public int index;
	public double value;
	
	@Override
	public boolean equals(Object another) {
		return index == ((Node)another).index;
	}
	
	public boolean isEqual(Node another)
    {
        return index == another.index;
    }
	
}