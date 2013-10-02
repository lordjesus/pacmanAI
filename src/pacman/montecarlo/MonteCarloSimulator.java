package pacman.montecarlo;

import java.util.EnumMap;
import java.util.Stack;

import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MonteCarloSimulator {
	
	private Stack<Game> gameStack = new Stack<Game>();
	private Node rootNode;
	private Game game;
	Controller<EnumMap<GHOST,MOVE>> ghostController;
	/**
	 * Exploration coefficient
	 */
	private float C = (float) (1.0/Math.sqrt(2));
	
	public MOVE MCTreeSearch() {
		rootNode = new Node();
		rootNode.NodePos = game.getPacmanCurrentNodeIndex();
		pushToGameStack();
		try {
			Node currentNode = rootNode;
			advanceGameToJunction();
			
			while (!terminate()) {
				Node vl = TreePolicy(currentNode);
				float delta = DefaultPolicy(vl);
				Backup(vl, delta);
			}
		}
		finally {
			popFromGameStack();
		}
		
		return getBestMove();
	}
	
	private Node TreePolicy(Node current) {
		while (!terminalState()) {
			if (!current.IsFullyExpanded(game)) {
				return Expand(current);
			}
			else {
				current = BestChild(current, C);
			}
		}
		return current;
	}
	
	private Node Expand(Node current) {
		return null;
	}
	
	private Node BestChild(Node current, float c) {
		return null;
	}
	
	private float DefaultPolicy(Node vl) { 
		// TODO Auto-generated method stub
		return 0;
	}
		
	private void Backup(Node vl, float delta) {
		// TODO Auto-generated method stub
		
	}

	private MOVE getBestMove() {
		return MOVE.NEUTRAL;
	}
	
	private boolean terminalState() {
		return false;
	}
	
	private boolean terminate() {
		return false;
	}
	
	private Game pushToGameStack() {
		gameStack.push(game);
		game = game.copy();
		return game;
	}
	
	private Game popFromGameStack() {
		game = gameStack.pop();
		return game;
	}
	
	private void advanceGameToJunction() {
		MOVE move = game.getPacmanLastMoveMade();
		
		while (!isAtJunction()) {
			game.advanceGame(move, ghostController.getMove(game.copy(), -1));
		}
	}
	
	/**
	 * Checks if pacman is at a junction or game is ended
	 * @return
	 */
	private boolean isAtJunction() {
		return false;
	}
	
}
