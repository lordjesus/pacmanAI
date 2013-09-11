package pacman.entries.pacman;

import java.util.EnumSet;
import java.util.Set;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class FinitePacMan extends Controller<MOVE> {

	StateMachine currentState = StateMachine.EAT_PILL;
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		int currentPos = game.getPacmanCurrentNodeIndex();
		
		GetCurrentState(game, currentPos); 
		// TODO Auto-generated method stub
		return null;
	}
	
	private void GetCurrentState(Game game, int currentPos) {
		
	}

}

enum StateMachine {
	EAT_PILL {
		@Override
		public Set<StateMachine> transitions() {
			return EnumSet.of(EAT_GHOST, FLEE);
		}
	},
	EAT_GHOST {
		public Set<StateMachine> transitions() {
			return EnumSet.of(EAT_PILL);
		}
	},	
	FLEE {
		public Set<StateMachine> transitions() {
			return EnumSet.of(EAT_PILL);
		}
	};
	
	public Set<StateMachine> transitions() {
		return EnumSet.noneOf(StateMachine.class);
	}
}