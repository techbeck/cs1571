import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PancakeProblem extends Problem {
	public State initialState;
	public int numPancakes;
	public State goalState;

	public PancakeProblem(Scanner inputReader) {
		if (inputReader.hasNext()) {
			String pancakes = inputReader.nextLine().replaceAll("[\\)\\(]", "");
			String[] sizes = pancakes.split(", ");
			numPancakes = sizes.length;
			initialState = new PancakeState(numPancakes);
			initialState.setState(sizes);
			
			goalState = new PancakeState(numPancakes);
			String[] goal = new String[numPancakes];
			for (int i = 0; i < numPancakes; i++) {
				goal[i] = Integer.toString(i+1);
			}
			goalState.setState(goal);
		}
	}

	public void setInitialState(State init) {
		initialState = init;
	}
	public State getInitialState() {
		return initialState;
	}
	// euclidean distance from goal + number where burnt side is up
	public double h(State state) {
		// double diffSquare = 0.0;
		// int burntUpCount = 0;
  //       for (int i = 0; i < state.getStateArray().length; i++) {
  //       	int stateValue = state.getStateArray()[i];
  //       	if (stateValue < 0) {
  //       		burntUpCount++;
  //       	}
  //       	int goalValue = goalState.getStateArray()[i];
  //           diffSquare += (stateValue[i] - goalValue[i]) * (stateValue[i] - goalValue[i]);
  //       }
  //       return Math.sqrt(diffSquare) + burntUpCount;
		return 0;
	}
	public boolean goalTest(State state) {
		if (state == null) {
			return false;
		}
		return state.toString().equals(goalState.toString());
	}
	public List<Action> actions(State state) {
		List<Action> result = new ArrayList<Action>();
		// you can't flip a stack w/ less than 2 pancakes, so no actions possible
		if (state == null || state.getStateArray().length < 2) {
			return result;
		}
		for (int i = 2; i <= state.getStateArray().length; i++) {
			Action newAction = new Action();
			State newState = new PancakeState(numPancakes);
			newState.setState(flip(state.getStateArray(), i));
			newAction.state1 = state;
			newAction.state2 = newState;
			result.add(newAction);
		}
		return result;
	}
	public State result(State state, Action action) {
		if (Arrays.equals(state.getStateArray(), action.state1.getStateArray())) {
			return action.state2;
		} else {
			return action.state1;
		}
	}
	public double pathCost(double c, State state, Action action) {
		// assume path cost is flip index
		// for (int i = action.state1.getStateArray().length-1; i >= 0; i--) {
		// 	if (!action.state1.getStateArray()[i].equals(action.state2.getStateArray()[i])) {
		// 		return c+i+1;
		// 	}
		// }
		// return c;

		return c+1;
	}

	// will reverse and negate in place input array up to flipIndex (exclusive)
	private String[] flip(String[] input, int flipIndex) {
		//System.out.println("input: " + Arrays.toString(input));
		//System.out.println("flipIndex: " + flipIndex);
		for (int i = 0; i < flipIndex / 2; i++) {
			String temp = input[i];
			input[i] = input[flipIndex - 1 - i];
			input[flipIndex - 1 - i] = temp;
		}
		for (int i = 0; i < flipIndex; i++) {
			int value = Integer.parseInt(input[i]);
			input[i] = Integer.toString(-value);
		}
		//System.out.println("output: " + Arrays.toString(input));
		return input;
	}
}