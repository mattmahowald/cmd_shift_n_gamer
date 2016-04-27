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

public final class MyGamerRandom extends SampleGamer {

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


	// Internal state about the current state of the state machine.
    private Role role;
    private MachineState currentState;
    private StateMachine stateMachine = getInitialStateMachine();

}
