package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public final class BeastMinimax extends SampleGamer {

	public /*static final*/ int LIMIT; /*= 15;*/
	public static final int MAX_SCORE = 100;

	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		//Implement
	}


	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		long start = System.currentTimeMillis();
		return bestMove(start);
	}



	private Move bestMove(long start) throws TransitionDefinitionException,
	MoveDefinitionException, GoalDefinitionException {
		List<Move> legalMoves = getStateMachine().findLegals(getRole(), getCurrentState());
		if (legalMoves.size() == 1) return legalMoves.get(0);
		Move action = legalMoves.get(0);
		int score = 0;
		List<Role> roles = getStateMachine().findRoles();
		for (int i = 0; i < legalMoves.size(); i++) {
			int result;
			if (roles.size() == 1) {
				LIMIT = 4;
				List<Move> nextMove = legalMoves.subList(i, i + 1);
				result = maxScore(getStateMachine().getNextState(getCurrentState(), nextMove), 0);
				if (result == MAX_SCORE) return legalMoves.get(i);
			} else {
				LIMIT = 2;
				result = minScore(legalMoves.get(i), getCurrentState(), 0);
			}
			if (result > score) {
				score = result;
				action = legalMoves.get(i);
			}
		}
		long end = System.currentTimeMillis();
		notifyObservers(new GamerSelectedMoveEvent(legalMoves, action, end - start));
		return action;
	}

	//Function is only called in a 2 player game.
	private int minScore(Move action, MachineState currState, int level) throws MoveDefinitionException,
	GoalDefinitionException, TransitionDefinitionException {
		List<Role> roles = getStateMachine().findRoles();
		Role opponent = (roles.get(0).equals(getRole()) ? roles.get(1) : roles.get(0));
		List<Move> opplegalMoves = getStateMachine().findLegals(opponent, currState);
		int score = MAX_SCORE;
		for (int i = 0; i < opplegalMoves.size(); i++) {
			ArrayList<Move> m = new ArrayList<Move>();
			if (getRole().equals(roles.get(0))) {
				m.add(action);
				m.add(opplegalMoves.get(i));
			} else {
				m.add(opplegalMoves.get(i));
				m.add(action);
			}
			MachineState newState = getStateMachine().findNext(m, currState);
			int result = maxScore(newState, level + 1);
			if (result < score) {
				score = result;
			}
		}
		return score;
	}

	private int maxScore(MachineState state, int level)  throws MoveDefinitionException,
	GoalDefinitionException, TransitionDefinitionException {
		if (getStateMachine().findTerminalp(state)) return getStateMachine().getGoal(state, getRole());
		if (level >= LIMIT) return monteCarlo(state, 4);
		List<Move> legalMoves =  getStateMachine().findLegals(getRole(), state);
		int score = 0;
		List<Role> roles = getStateMachine().findRoles();
		for (int i = 0; i < legalMoves.size(); i++) {
			int result;
			if (roles.size() == 1) {
				List<Move> nextMove = legalMoves.subList(i, i + 1);
				result = maxScore(getStateMachine().getNextState(state, nextMove), level + 1);
			} else {
				result = minScore(legalMoves.get(i), state, level);
			}
			//if (result == 100) return 100;
			if (result > score) score = result;
		}
		return score;
	}

	private int monteCarlo(MachineState state, int count) throws MoveDefinitionException,
	GoalDefinitionException, TransitionDefinitionException {
		int total = 0;
		for (int i = 0; i < count; i++) {
			total += depthCharge(state);
		}
		return total / count;
	}

	private int depthCharge(MachineState state) throws MoveDefinitionException,
	GoalDefinitionException, TransitionDefinitionException {
		if (getStateMachine().findTerminalp(state)) return getStateMachine().findReward(getRole(), state);
		ArrayList<Move> moves = new ArrayList<Move>();
		List<Role> roles = getStateMachine().findRoles();
		for (int i = 0; i < roles.size(); i++) {
			List<Move> options = getStateMachine().findLegals(roles.get(i), state);
			Random r = new Random();
			int randomMove = r.nextInt(options.size());
			moves.add(options.get(randomMove));
		}
		MachineState newState = getStateMachine().findNext(moves, state);
		return depthCharge(newState);
	}


}
