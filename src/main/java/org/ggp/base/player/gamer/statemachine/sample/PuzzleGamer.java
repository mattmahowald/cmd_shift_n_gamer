package org.ggp.base.player.gamer.statemachine.sample;

import java.util.List;
import java.util.Random;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class PuzzleGamer extends SampleGamer {

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		role = getRole();
		currentState = getCurrentState();
		stateMachine = getStateMachine();
		Random rgen = new Random();
		List<Move> legalMoves = stateMachine.findLegals(role, currentState);
		int randomMove = rgen.nextInt(legalMoves.size());
		return legalMoves.get(randomMove);
	}

	private boolean findBestMove(MachineState state, Role r) throws MoveDefinitionException {
		if (stateMachine.findTerminalp(state)) {
			try {
				if(stateMachine.findReward(r, state) == 100) return true;
				else return false;
			} catch (GoalDefinitionException e) {

			}
		}
		List<Move> legalMoves = stateMachine.findLegals(r, state);
		// findBestMove
		// find score from best move
		int score = 0;
		// find plan from best move
		// Add to the end of the plan the new legal move
		for(int i = 0; i < legalMoves.size(); i++) {
			// findBestMove[i]
			int result = 0 /* score from findBestMove */;
			if(result > score) {
				score = result;
				/* plan = plan from findBestMove */
				/* append actions[i] to plan */
			}
		}

		return false;
	}


	//	var plan;
	//	var step;
	//
	//	function start (id,player,rules,start,play)
	// 	{
	//		game = rules;
	//  	role = player;
	//  	roles = findroles(game);
	//  	state = findinits(game);
	//  	plan = bestplan(role,state)[1];
	//  	step = 0;
	//  	return 'ready'
	//	}
	//
	//	function play (id,move)
	//  {
	//  	var action = plan[step];
	//  	step = step + 1;
	//  	return action
	//  }
	//
	//
	//	function bestplan (role,state)
	// 	{
	//		if (terminalp(state)) {
	//			return [findreward(role,state,game),[]]
	//  	};
	//  	var actions = findlegals(role,state,game);
	//  	var result = bestplan(role,findnext(roles,[actions[0]],state,game));
	//  	var score = result[0];
	//  	var plan = result[1];
	//  	plan[plan.length] = actions[0];
	//  	for (var i=1; i<actions.length; i++)
	//      {
	//			var result = bestplan(role,findnext(roles,[actions[i]],state,game));
	//       	if (result[0]>score)
	//          {
	//				score = result[0];
	//           	plan = result[1];
	//           	plan[plan.length] = actions[i]
	//			};
	//  		return [score,plan]
	//		}


	// Internal state about the current state of the state machine.
    private Role role;
    private MachineState currentState;
    private StateMachine stateMachine = getInitialStateMachine();

}
