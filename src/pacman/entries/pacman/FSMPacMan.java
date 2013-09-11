package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class FSMPacMan extends Controller<MOVE> {

	STATE currentState = STATE.EAT_PILL;
	int numberOfLives = 0;
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		int current = game.getPacmanCurrentNodeIndex();
		
		// Determine what state pacman is in
		STATE startState = currentState;
		do {
			System.out.println("CurrentState: " + currentState.name());
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

				for(GHOST ghost : GHOST.values()) {
					if (game.getGhostLairTime(ghost) == 0) {
//						if(game.getGhostEdibleTime(ghost)==0) {
						if (!game.isGhostEdible(ghost)) {
							
							int ghostDist = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));
							if (ghostDist < closeEnoughToSearch) {
								currentState = STATE.SEARCH_POWER_PILL;
								break;
							}
						} else
//							if (game.getGhostEdibleTime(ghost) > edibleTime) {
							if (game.isGhostEdible(ghost)) {
							System.out.print(ghost.name() + ": " + game.getGhostEdibleTime(ghost) + ", ");
							currentState = STATE.EAT_GHOST;
							break;
						}
					}
				}

				break;
			case EAT_GHOST:
				for(GHOST ghost : GHOST.values()) {
					if (game.getGhostLairTime(ghost) == 0) {
						if(game.getGhostEdibleTime(ghost) <= edibleTime) {
							System.out.print(ghost.name() + " eat ghost: " + game.getGhostEdibleTime(ghost) + ", ");
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
		
		int lives = game.getPacmanNumberOfLivesRemaining();
		if (lives != numberOfLives) {
			currentState = STATE.EAT_PILL;
			numberOfLives = lives;
		}

		System.out.println("CurrentState: " + currentState.name());

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

	private MOVE EatPill(Game game, int current) {
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

