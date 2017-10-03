import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class AggregationProblem extends Problem {
	public State initialState;
	public int numNodes;
	public HashMap<String,List<Edge>> graph;
	public HashMap<String,GraphNode> nodes;
	public HashMap<String,Double> distancesFromInit;

	public AggregationProblem(Scanner inputReader) {
		graph = new HashMap<String,List<Edge>>();
		nodes = new HashMap<String,GraphNode>();
		String rawNodeData = inputReader.nextLine();
		String[] rndArray = rawNodeData.split("\\),\\s*");
		numNodes = rndArray.length;
		for (String rnd : rndArray) {
			rnd = rnd.replaceAll("[\\[\\]()\"]", "");
			String[] values = rnd.split(",\\s*");
			GraphNode n = new GraphNode();
			n.id = values[0];
			n.x = Integer.parseInt(values[1]);
			n.y = Integer.parseInt(values[2]);
			graph.put(n.id, new ArrayList<Edge>());
			nodes.put(n.id, n);
		}
		while (inputReader.hasNext()) {
			String rawEdgeData = inputReader.nextLine();
			rawEdgeData = rawEdgeData.replaceAll("[\\[\\]()\"]", "");
			String[] values = rawEdgeData.split(",\\s*");
			String id1 = values[0];
			String id2 = values[1];
			Edge e = new Edge();
			e.node1 = nodes.get(id1);
			e.node2 = nodes.get(id2);
			e.delay = Integer.parseInt(values[2]);
			graph.get(id1).add(e);
			graph.get(id2).add(e);
		}
		initialState = new AggregationState(numNodes);
		initialState.setState(nodes.keySet().toArray(new String[0]));
	}
	public void setInitialState(State init) {
		initialState = init;
		setDomainKnowledge();
	}
	public State getInitialState() {
		return initialState;
	}
	public double h(State state) {
		String[] visited = state.getStateArray();
		String lastVisited = visited[visited.length-1];
		double distance = distancesFromInit.get(lastVisited);

		String s = state.toString();
		double avgUnvisitedX = 0;
		double avgUnvisitedY = 0;
		int numUnvisited = 0;
		for (GraphNode node : nodes.values()) {
			if (!s.contains(node.id)) {
				avgUnvisitedX += node.x;
				avgUnvisitedY += node.y;
				numUnvisited++;
			}
		}
		if (numUnvisited > 0) {
			avgUnvisitedX = avgUnvisitedX/numUnvisited;
			avgUnvisitedY = avgUnvisitedY/numUnvisited;
		} else {
			return 0;
		}
		GraphNode lastNode = nodes.get(state.getStateArray()[0]);
		double xDist = avgUnvisitedX - lastNode.x;
		double yDist = avgUnvisitedY - lastNode.y;
		double centerDist =  Math.sqrt((xDist*xDist)+(yDist*yDist));
		return -distance;
	}
	public boolean goalTest(State state) {
		String s = state.toString();
		for (GraphNode node : nodes.values()) {
			if (!s.contains(node.id)) {
				return false;
			}
		}
		return true;
	}
	public List<Action> actions(State state) {
		String[] stateArray = state.getStateArray();
		String lastNodeID = stateArray[stateArray.length-1];
		List<Edge> neighbors = graph.get(lastNodeID);
		List<Action> actions = new ArrayList<Action>();
		for (Edge e : neighbors) {
			String neighborID = e.getOtherId(lastNodeID);
			String[] singleState = {neighborID};
			String[] newStateArray = new String[stateArray.length + 1];
			System.arraycopy(stateArray, 0, newStateArray, 0, stateArray.length);
			System.arraycopy(singleState, 0, newStateArray, stateArray.length, 1);
			Action newAction = new Action();
			newAction.state1 = new AggregationState(stateArray.length);
			newAction.state1.setState(stateArray);
			newAction.state2 = new AggregationState(newStateArray.length);
			newAction.state2.setState(newStateArray);
			if (duplicatesCheck(newAction.state2)) {
				continue;
			}
			actions.add(newAction);
		}
		return actions;
	}
	public State result(State state, Action action) {
		return action.getOther(state);
	}
	public double pathCost(double c, State state, Action action) {
		String[] state1 = state.getStateArray();
		String id1 = state1[state1.length-1];
		String[] state2 = result(state, action).getStateArray();
		String id2 = state2[state2.length-1];
		Edge edge = null;;
		for (Edge e : graph.get(id1)) {
			if (e.getOtherId(id1).equals(id2)) {
				edge = e;
				break;
			}
		}
		double cost = edge.delay;
		return c+cost;
	}

	private boolean duplicatesCheck(State state) {
		String[] stateArray = state.getStateArray();
		for (int j = 0; j < stateArray.length; j++) {
			for (int k = j + 1; k < stateArray.length; k++) {
				if (stateArray[k].equals(stateArray[j])) {
					return true;
				}
			}
		}
		return false;
	}
	private void setDomainKnowledge() {
		distancesFromInit = new HashMap<String,Double>();
		GraphNode init = nodes.get(initialState.getStateArray()[0]);
		for (GraphNode gn : nodes.values()) {
			int xDist = init.x - gn.x;
			int yDist = init.y - gn.y;
			double distance = Math.sqrt((xDist*xDist)+(yDist*yDist));
			distancesFromInit.put(gn.id,distance);
		}
	}
	private class GraphNode {
		private String id;
		private int x;
		private int y;
	}
	private class Edge {
		private GraphNode node1;
		private GraphNode node2;
		private int delay;

		private String getOtherId(String nodeID) {
			if (nodeID.equals(node2.id)) {
				return node1.id;
			} else {
				return node2.id;
			}
		}
	}
}