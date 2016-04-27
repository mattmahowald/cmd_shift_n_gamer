package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public final class BeastMonteCarloTreeSearch extends SampleGamer {

	//Global Variable for the root of the MCST
	Node root;

	//Call initially to begin building the tree
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		root = new Node(getCurrentState(), 0, 0, null, new ArrayList<Node>(), null);
		//stateMachineSelectMove(timeout);
	}


	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		long start = System.currentTimeMillis();
		//Leaves 3 Seconds to then make the final decision
		while (System.currentTimeMillis() - start < timeout - 3000) {
			Node currNode = select(root);
			expand(currNode);
			int result = monteCarlo(currNode.state, 4);
			backPropogate(currNode, result);
		}
		int highestScore = 0;
		Move action = null;
		Node newRoot = null;
		for (int i = 0; i < root.children.size(); i++) {
			if (root.children.get(i).utility > highestScore) {
				highestScore = root.children.get(i).utility;
				action = root.children.get(i).action;
				newRoot = root.children.get(i);
			}
		}
		root = newRoot;	//updates the root
		return action;	//returns the final action
	}


	private Node select(Node node) {
		if (node.visits == 0) return node;
		for (int i = 0; i < node.children.size(); i++) {
			if (node.children.get(i).visits == 0) return node.children.get(i);
		}
		double score = 0;
		Node result = node;
		for (int i = 0; i < node.children.size(); i++) {
			double newScore = selectfn(node.children.get(i));
			if (newScore > score) {
				score = newScore;
				result = node.children.get(i);
			}
		}
		return select(result);
	}

	private double selectfn(Node node) {
		return (node.utility + Math.sqrt( (double) 2*Math.log(node.parent.visits)/node.visits));
	}

	private void expand(Node node) throws MoveDefinitionException, TransitionDefinitionException {
		List<Move> actions = getStateMachine().findLegals(getRole(), node.state);
		for (int i = 0; i < actions.size(); i++) {
			ArrayList<Move> moves = new ArrayList<Move>();
			moves.add(actions.get(i));
			MachineState newState = getStateMachine().findNext(moves, node.state);
			Node newNode = new Node(newState, 0, 0, node, new ArrayList<Node>(), actions.get(i));
			node.children.add(newNode);
		}
	}

	private void backPropogate(Node node, int score) {
		node.visits++;
		node.utility += score;
		if (node.parent != null) backPropogate(node.parent, score);
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

	private class Node {
		int visits;
		int utility;
		MachineState state;
		Node parent;
		ArrayList<Node> children;
		Move action;
		public Node(MachineState state, int utility, int visits, Node parent, ArrayList<Node> children, Move action) {
			this.state = state;
			this.visits = visits;
			this.utility = utility;
			this.parent = parent;
			this.children = children;
			this.action = action;
		}
	}

}
