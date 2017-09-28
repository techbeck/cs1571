import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MonitorProblem extends Problem {
	public State initialState;
	public int numSensors, numTargets;
	public String[] rawSensorData, rawTargetData;

	public MonitorProblem(Scanner inputReader) {
		if (inputReader.hasNext()) {
			rawSensorData = inputReader.nextLine().split("\\),");
			numSensors = rawSensorData.length;
			if (inputReader.hasNext()) {
				rawTargetData = inputReader.nextLine().split("\\),");
				numTargets = rawTargetData.length;
			}
		}
		
		this.initialState = new MonitorState(numSensors);
		this.initialState.setState(new String[numSensors]);
	}

	public void setInitialState(State init) {
		initialState = init;
	}
	public State getInitialState() {
		return initialState;
	}
	public double h(State state) {
		return 0;
	}
	public boolean goalTest(State state) {
		if (state == null || state.getStateArray().length != numSensors) {
			return false;
		}
		boolean[] monitored = new boolean[numTargets];
		for (int i = 0; i < numSensors; i++) {
			if (state.getStateArray()[i] != null && !state.getStateArray()[i].equals("0")) {
				monitored[Integer.parseInt(state.getStateArray()[i])-1] = true;
			}
		}
		for (int i = 0; i < numTargets; i++) {
			if (monitored[i] == false)
				return false;
		}
		return true;
	}
	public List<Action> actions(State state) {
		List<Action> result = new ArrayList<Action>();
		int index = -1;
		for (int i = 0; i < state.getStateArray().length; i++) {
			if (state.getStateArray()[i].equals("0")) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			return result;
		}
		for (int i = 1; i <= numTargets; i++) {
			Action newAction = new Action();
			State newState = new MonitorState(numSensors);
			String[] sensors = state.getStateArray();
			sensors[index] = Integer.toString(i);
			newState.setState(sensors);
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
		return pathCost(result(state, action));
	}
	// max w/ target, min b/t targets where each is batteryLife/distance
	public double pathCost(State state2) {
		double[] costs = new double[numSensors];
		for (int i = 0; i < numSensors; i++) {
			if (state2.getStateArray()[i].equals("0")) {
				costs[i] = Double.MAX_VALUE;
				continue;
			}
			String[] sensorValues = rawSensorData[i].replaceAll("[\"\\s\\(\\)\\[\\]]", "").split(",");
			int sensorX = Integer.parseInt(sensorValues[1]);
			int sensorY = Integer.parseInt(sensorValues[2]);
			int batteryLife = Integer.parseInt(sensorValues[3]);
			
			String[] targetValues = rawTargetData[Integer.parseInt(state2.getStateArray()[i])-1].replaceAll("[\"\\s\\(\\)\\[\\]]", "").split(",");
			int targetX = Integer.parseInt(targetValues[1]);
			int targetY = Integer.parseInt(targetValues[2]);

			int xDist = sensorX - targetX;
			int yDist = sensorY - targetY;

			double distance = Math.sqrt((xDist*xDist)+(yDist*yDist));
			double cost = -(batteryLife/distance);
			costs[i] = cost;
		}
		for (int i = 0; i < numSensors; i++) {
			for (int j = 0; j < numSensors; j++) {
				if (state2.getStateArray()[i].equals(state2.getStateArray()[j])) {
					if (costs[i] < costs[j]) {
						costs[j] = costs[i];
					} else if (costs[j] < costs[i]) {
						costs[i] = costs[j];
					}
				}
			}
		}
		double min = Double.MAX_VALUE;
		for (int i = 0; i < numSensors; i++) {
			if (costs[i] < min) {
				min = costs[i];
			}
		}
		return min;
	}
}