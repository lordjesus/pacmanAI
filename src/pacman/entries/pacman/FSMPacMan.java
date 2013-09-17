package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class FSMPacMan extends Controller<MOVE> {

	STATE currentState = STATE.EAT_PILL;
	int numberOfLives = 0;
	MovingWindowList<Integer> lastMoves = new MovingWindowList<>(10);
	int timeSinceLastEat;
	Stack<Integer> movePath;

	@Override
	public MOVE getMove(Game game, long timeDue) {
		int current = game.getPacmanCurrentNodeIndex();
		lastMoves.put(current);

		// Determine what state pacman is in
		GetCurrentState(game, current); 

		int lives = game.getPacmanNumberOfLivesRemaining();
		if (lives != numberOfLives) {
			currentState = STATE.EAT_PILL;
			numberOfLives = lives;
		}
		if (game.wasPillEaten()) {
			timeSinceLastEat = 0;
		} else {
			timeSinceLastEat++;
		}

		//		System.out.println("CurrentState: " + currentState.name());
		if (currentState != STATE.EAT_PILL) {
			movePath = null;
		}
		return GetNextMove(game, current);
	}

	private MOVE SearchForMove(Game game, int current) {



		return MOVE.NEUTRAL;
	}

	private boolean isStuck() {
		if (lastMoves.size() > 3) {
			List<Integer> moves = lastMoves.asList();
			List<Integer> usedMoves = new ArrayList<Integer>();
			for (int pos : moves) {
				if (!usedMoves.contains(pos)) {
					usedMoves.add(pos);
				}
			}
			if (usedMoves.size() < 3) {
				return true;
			}
		}
		return false;
	}

	private MOVE GetNextMove(Game game, int current) {
		// Check if PacMan is stuck
//		if (isStuck()) { 
////			System.out.println("Was stuck");
//			return MOVE.NEUTRAL;
//		}
		MOVE myMove = MOVE.NEUTRAL;
		// Determine action based on state
		switch (currentState) {
		case EAT_GHOST:
			myMove = EatGhost(game, current);
			break;
		case EAT_PILL:
			myMove = EatPill(game, current);
			break;
		case FLEE:
			myMove = Flee(game, current);
			break;
		case SEARCH_POWER_PILL:
			myMove = SearchPowerPill(game, current);
			break;

		default:
			break;
		}

		return myMove;
	}

	private void GetCurrentState(Game game, int current) {
		STATE startState = currentState;
		do {
//						System.out.println("CurrentState: " + currentState.name());
			startState = currentState;

			int edibleTime = 0;
			int closeEnoughToSearch = 20;
			int closeEnoughToFlee = 10;

			GHOST closest = null;
			int minDistance=Integer.MAX_VALUE;

			for(GHOST ghost : GHOST.values()) {
				if(game.getGhostLairTime(ghost)==0) {
					int dist = game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost));
					if (dist < minDistance) {
						closest = ghost;
						minDistance = dist;
					}
				}
			}

			switch (currentState) {
			case EAT_PILL:

				if (closest != null) {
					if (game.getGhostLairTime(closest) == 0) {
						if (game.isGhostEdible(closest)) {
							if (game.getGhostEdibleTime(closest) > edibleTime) {
								currentState = STATE.EAT_GHOST;
							}
						} else {
							int ghostDist = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(closest));
							if (ghostDist < closeEnoughToSearch) {
								currentState = STATE.SEARCH_POWER_PILL;								
							}
						}
					}
				}



				break;
			case EAT_GHOST:

				if (closest != null) {
					if (game.getGhostLairTime(closest) == 0) {
						if (game.getGhostEdibleTime(closest) <= edibleTime) {
							currentState = STATE.EAT_PILL;
						}
					}
				}


				break;
			case SEARCH_POWER_PILL:
				// Find closest power pill
				int[] powerPills=game.getActivePowerPillsIndices();
				int closestPowerPill = -1;
				int closestPowerPillDistance = Integer.MAX_VALUE;
				for (int pp : powerPills) {
					int dist = game.getShortestPathDistance(current, pp);
					if (dist < closestPowerPillDistance) {
						closestPowerPillDistance = dist;
						closestPowerPill = pp;
					}
				}



				if (closest != null) {
					if (game.getGhostLairTime(closest) == 0) {
						if(game.getGhostEdibleTime(closest)==0 || closestPowerPill == -1) {
							if (game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(closest)) > closeEnoughToSearch) {
								currentState = STATE.EAT_PILL;
								break;
							}
							if (game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(closest)) < closeEnoughToFlee)
								//|| game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost)) < closestPowerPillDistance) 
							{
								currentState = STATE.FLEE;
								break;
							}
						} else if (game.getGhostEdibleTime(closest) > edibleTime) {
							currentState = STATE.EAT_GHOST;
							break;
						}
					}
				}

				break;
			case FLEE:				

				if (closest != null) {
					if (game.getGhostLairTime(closest) == 0) {
						if(game.getGhostEdibleTime(closest) > edibleTime) {
							currentState = STATE.EAT_GHOST;
							break;
						} 
						if (game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(closest)) > closeEnoughToFlee) {
							currentState = STATE.SEARCH_POWER_PILL;
						}
					}
				}

				break;

			default:
				break;
			}
		}
		while (startState != currentState);

	}

	private MOVE SearchPowerPill(Game game, int current) {
		int[] powerPills=game.getActivePowerPillsIndices();
		int closestPowerPill = -1;
		int closestPowerPillDistance = Integer.MAX_VALUE;
		for (int pp : powerPills) {
			int dist = game.getShortestPathDistance(current, pp);
			if (dist < closestPowerPillDistance) {
				closestPowerPillDistance = dist;
				closestPowerPill = pp;
			}
		}

		if (closestPowerPill > 0) {
			return game.getNextMoveTowardsTarget(current,closestPowerPill,DM.PATH); 
		}
		return MOVE.NEUTRAL;
	}

	private MOVE Flee(Game game, int current) {
		if (true) {
			Search search = new Search(game, 100, current, true, timeSinceLastEat);
			return search.BeginSearch();
		} else {
			GHOST closest = null;
			int minDistance=Integer.MAX_VALUE;

			for(GHOST ghost : GHOST.values()) {
				if(game.getGhostLairTime(ghost)==0) {
					int dist = game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost));
					if (dist < minDistance) {
						closest = ghost;
						minDistance = dist;
					}
				}
			}

			if (closest != null) {
				if (game.getGhostEdibleTime(closest) == 0) {		
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(closest),DM.PATH); 
				}
			}
			return MOVE.NEUTRAL;
		}
	}

	private MOVE EatPill(Game game, int current) {
		if (true) {
			if (false) {
				if (movePath == null || movePath.empty()) {
					Search search = new Search(game, 50, current, false, timeSinceLastEat);
					movePath = search.GetMoveList();
//					System.out.println("Searching for path");
				} 
				int nextPos = movePath.pop();
//				System.out.println("Go from " + current + " to " + nextPos);
				return game.getNextMoveTowardsTarget(current, nextPos, DM.PATH); 
			}
			else {
				Search search = new Search(game, 50, current, false, timeSinceLastEat);
				return search.BeginSearch();
			}
		} else {
			int[] activePills = game.getActivePillsIndices();
			int closestPill = -1;
			int closestPillDistance = Integer.MAX_VALUE;

			for (int pill : activePills) {
				int dist = game.getShortestPathDistance(current,pill);
				if (dist < closestPillDistance) {
					closestPill = pill;
					closestPillDistance = dist;
				}
			}

			return game.getNextMoveTowardsTarget(current,closestPill,DM.PATH); 
		}
	}

	private MOVE EatGhost(Game game, int current) {
		// Simple - eat closest ghost
		
		GHOST closest = null;
		int minDistance=Integer.MAX_VALUE;

		for(GHOST ghost : GHOST.values()) {
			if(game.getGhostLairTime(ghost)==0) {
				int dist = game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost));
				if (dist < minDistance) {
					closest = ghost;
					minDistance = dist;
				}
			}
		}

		if (closest != null) {
			if (game.getGhostEdibleTime(closest) > 0) {		
				return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(closest),DM.PATH); 
			}
		}
		return MOVE.NEUTRAL;
	}

}

enum STATE {
	EAT_PILL,
	EAT_GHOST,
	SEARCH_POWER_PILL,
	FLEE
}

