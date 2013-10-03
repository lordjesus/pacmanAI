package pacman.montecarlo;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MonteCarloPacMan extends Controller<MOVE> {

	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		MonteCarloSimulator simulator = new MonteCarloSimulator(game, timeDue);
		MOVE move = simulator.MCTreeSearch();
		
		return move;
	}

}
