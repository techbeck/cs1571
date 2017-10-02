import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MonitorProblem extends Problem {
	public State initialState;
	public int numSensors, numTargets;
	public String[] rawSensorData, rawTargetData;

	public MonitorProblem(Scanner inputReader) {
		rawSensorData = inputReader.nextLine().split("\\),");
		numSensors = rawSensorData.length;
		rawTargetData = inputReader.nextLine().split("\\),");
		numTargets = rawTargetData.length;	
		this.initialState = new MonitorState(numSensors);
		this.initialState.setState(new String[numSensors]);
	}

	public void setInitialState(State init) {
		initialState = init;
	}
	public State getInitialState() {
		return initialState;
	}
	// sum of unmonitored targets to nearest unassigned sensor
	public double h(State state) {
		int[] sensorsX = new int[numSensors];
		int[] sensorsY = new int[numSensors];
		int[] sensorsBattery = new int[numSensors];
		boolean[] targetsUnmonitored = new boolean[numTargets];
		double maxDistanceToNearest = 0;

		for (int i = 0; i < numSensors; i++) {
			String[] sensorValues = rawSensorData[i].replaceAll("[\"\\s\\(\\)\\[\\]]", "").split(",");
			sensorsX[i] = Integer.parseInt(sensorValues[1]);
			sensorsY[i] = Integer.parseInt(sensorValues[2]);
			sensorsBattery[i] = Integer.parseInt(sensorValues[3]);
			if (state.getStateArray()[i].equals("0")) {
				sensorsX[i] = -1;
				continue;
			}
			int targetMonitored = Integer.parseInt(state.getStateArray()[i]);
			targetsUnmonitored[targetMonitored-1] = false;
		}
		for (int i = 0; i < numTargets; i++) {
			if (targetsUnmonitored[i] == false) {
				continue;
			}
			String[] targetValues = rawTargetData[i].replaceAll("[\"\\s\\(\\)\\[\\]]", "").split(",");
			int targetX = Integer.parseInt(targetValues[1]);
			int targetY = Integer.parseInt(targetValues[2]);
			double minDist = 0;
			for (int j = 0; j < numSensors; j++) {
				if (sensorsX[i] == -1) {
					continue;
				}
				int xDist = sensorsX[i] - targetX;
				int yDist = sensorsY[i] - targetY;
				double distance = Math.sqrt((xDist*xDist)+(yDist*yDist));
				if (distance > minDist) {
					minDist = distance;
				}
			}
			if (minDist > maxDistanceToNearest) {
				maxDistanceToNearest = minDist;
			}
		}
		return maxDistanceToNearest;
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
		return action.getOther(state);
	}
	public double pathCost(double c, State state, Action action) {
		return pathCost(result(state, action));
	}
	// max w/ target, min b/t targets where each is -batteryLife/distance
	public double pathCost(State state2) {
		double[] costs = new double[numSensors];
		for (int i = 0; i < numSensors; i++) {
			if (state2.getStateArray()[i].equals("0")) {
				costs[i] = 0;
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
		double[] targetMax = new double[numTargets];
		for (int i = 0; i < numSensors; i++) {
			if (state2.getStateArray()[i].equals("0")) {
				continue;
			}
			double currentMax = targetMax[Integer.parseInt(state2.getStateArray()[i])-1];
			// because negative
			if (costs[i] < currentMax) {
				targetMax[Integer.parseInt(state2.getStateArray()[i])-1] = costs[i];
			}
		}
		double min = Integer.MIN_VALUE;
		for (int i = 0; i < numTargets; i++) {
			if (targetMax[i] == 0) {
				continue;
			}
			// because negative
			if (targetMax[i] > min) {
				min = targetMax[i];
			}
		}
		return min;
	}
}