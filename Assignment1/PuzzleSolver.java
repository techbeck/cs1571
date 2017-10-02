import java.io.*;
import java.lang.Comparable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class PuzzleSolver {
	public static String problemType;
	public static String algorithm;
	public static String finalPath = "";
	public static int finalTime = 0;
	public static int finalSpaceFrontier = 0;
	public static int finalSpaceExplored = 0;
	public static double finalCost = Double.MAX_VALUE;
	public static String localPath = "";
	public static int localTime = 0;
	public static int localSpaceFrontier = 0;
	public static int localSpaceExplored = 0;
	public static double localCost = 0;
	public static Scanner inputReader = null;

	public static void main(String[] args) {
		new PuzzleSolver(args);
	}

	public PuzzleSolver(String[] args) {
		try {
			inputReader = new Scanner(new File(args[0]));
			algorithm = args[1];
		} catch (IOException io) {
			System.out.println("Missing data input file");
			System.exit(0);
		} catch (Exception ex) {
			System.out.println("Missing input");
			System.exit(0);
		}
		if (inputReader.hasNext()) {
			problemType = inputReader.nextLine().toLowerCase();
			if (problemType.equals("monitor")) {
				solveMonitorProblem();
			} else if (problemType.equals("aggregation")) {
				solveAggregationProblem();
			} else if (problemType.equals("pancakes")) {
				solvePancakeProblem();
			} else {
				System.out.println("Invalid problem type");
				System.exit(0);
			}
		}

		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter(algorithm + "_output_" + args[0]);
		} catch (IOException io) {
			System.out.println("Problem creating output file");
			System.exit(0);
		}
		if (finalPath == null) {
			System.out.println("No solution");
			outputWriter.println("No solution");
		} else {
			System.out.print("Solution:\n" + finalPath);
			outputWriter.print("Solution:\n" + finalPath);
		}
		System.out.println("Final Path Cost: " + finalCost);
		outputWriter.println("Final Path Cost: " + finalCost);
		System.out.println("Final Time: " + finalTime);
		outputWriter.println("Final Time: " + finalTime);
		System.out.println("Max Space Frontier: " + finalSpaceFrontier);
		outputWriter.println("Max Space Frontier: " + finalSpaceFrontier);
		System.out.println("Final Space Explored: " + finalSpaceExplored);
		outputWriter.println("Final Space Explored: " + finalSpaceExplored);
		inputReader.close();
		outputWriter.close();
	}

	public void solveMonitorProblem() {
		Problem problem = new MonitorProblem(inputReader);

		Node solution = null;
		if (algorithm.equals("bfs")) {
			solution = bfs(problem);
		} else if (algorithm.equals("unicost")) {
			solution = unicost(problem);
		} else if (algorithm.equals("iddfs")) {
			solution = iddfs(problem);
		} else if (algorithm.equals("greedy")) {
			solution = greedy(problem);
		} else if (algorithm.equals("Astar")) {
			solution = Astar(problem);
		}
		if (solution == null) {
			finalPath = null;
			finalCost = 0;
		} else {
			for (int i = 0; i < solution.state.getStateArray().length; i++) {
				String s = solution.state.getStateArray()[i];
				if (!s.equals("0")) {
					finalPath += ("S" + (i+1) + "-T" + s + "\n");
				} else {
					finalPath += ("S" + (i+1) + "-x" + "\n");
				}
			}
			finalCost = (-(solution.pathCost));
		}
		finalTime = localTime;
		finalSpaceFrontier = localSpaceFrontier;
		finalSpaceExplored = localSpaceExplored;
	}

	public void solveAggregationProblem() {
		Problem problem = new AggregationProblem(inputReader);
		int numNodes = problem.getInitialState().getStateArray().length;
		String[] start = new String[1];
		for (String s : problem.getInitialState().getStateArray()) {
			localPath = "";
			start[0] = s;
			State startState = new AggregationState(numNodes);
			startState.setState(start);
			problem.setInitialState(startState);
			Node solution = null;
			if (algorithm.equals("bfs")) {
				solution = bfs(problem);
			} else if (algorithm.equals("unicost")) {
				solution = unicost(problem);
			} else if (algorithm.equals("iddfs")) {
				solution = iddfs(problem);
			} else if (algorithm.equals("greedy")) {
				solution = greedy(problem);
			} else if (algorithm.equals("Astar")) {
				solution = Astar(problem);
			}
			if (solution == null) {
				localPath = null;
				localCost = Integer.MAX_VALUE;;
			} else {
				for (int j = 0; j < solution.state.getStateArray().length; j++) {
					localPath = localPath + solution.state.getStateArray()[j] + "\n";
				}
				localCost = solution.pathCost;
			}
			if (localCost < finalCost) {
				finalPath = localPath;
				finalCost = localCost;
			}
			finalTime += localTime;
			finalSpaceFrontier += localSpaceFrontier;
			finalSpaceExplored += localSpaceExplored;
		}
		if (finalPath == null) {
			finalCost = 0;
		}
	}

	public void solvePancakeProblem() {
		Problem problem = new PancakeProblem(inputReader);

		Node solution = null;
		if (algorithm.equals("bfs")) {
			solution = bfs(problem);
		} else if (algorithm.equals("unicost")) {
			solution = unicost(problem);
		} else if (algorithm.equals("iddfs")) {
			solution = iddfs(problem);
		} else if (algorithm.equals("greedy")) {
			solution = greedy(problem);
		} else if (algorithm.equals("Astar")) {
			solution = Astar(problem);
		}
		if (solution == null) {
			finalPath = null;
			finalCost = 0;
		} else {
			finalPath = "";
			finalCost = solution.pathCost;
			System.out.println("h: " + solution.h);
			System.out.println("pathCost: " + solution.pathCost);
			Stack<String> path = new Stack<String>();
			path.push(solution.state.toString());
			solution = solution.parentNode;
			while (solution != null) {
				path.push(solution.state.toString());
				solution = solution.parentNode;
			}
			while (!path.empty()) {
				finalPath = finalPath + path.pop() + "\n";
			}
		}
		finalTime = localTime;
		finalSpaceFrontier = localSpaceFrontier;
		finalSpaceExplored = localSpaceExplored;
	}

	// returns a Node on success or null on failure
	public Node bfs(Problem problem) {
		Node node = new Node();
		localTime++;
		node.state = problem.getInitialState();
		node.pathCost = 0;
		if (problem.goalTest(node.state)) {
			return node;
		}
		LinkedList<Node> frontier = new LinkedList<Node>();
		frontier.addLast(node);
		if (frontier.size() > localSpaceFrontier) {
			localSpaceFrontier = frontier.size();
		}
		List<String> explored = new LinkedList<String>();
		while (true) {
			if (frontier.isEmpty()) {
				return null;
			}
			node = frontier.removeFirst();
			explored.add(node.state.toString());
			if (explored.size() > localSpaceExplored) {
				localSpaceExplored = explored.size();
			}
			for (Action action : problem.actions(node.state)) {
				Node child = new Node();
				localTime++;
				child.state = problem.result(node.state, action);
				child.parentNode = node;
				child.pathCost = problem.pathCost(node.pathCost, node.state, action);
				child.f = child.pathCost;
				if (!explored.contains(child.state.toString()) && !frontier.contains(child)) {
					if (problem.goalTest(child.state)) {
						return child;
					}
					frontier.addLast(child);

					if (frontier.size() > localSpaceFrontier) {
						localSpaceFrontier = frontier.size();
					}
				}
			}
		}
	}

	// returns a Node on success or null on failure
	public Node unicost(Problem problem) {
		Node node = new Node();
		localTime++;
		node.state = problem.getInitialState();
		node.pathCost = 0;
		if (problem.goalTest(node.state)) {
			return node;
		}
		PriorityQueue<Node> frontier = new PriorityQueue<Node>();
		frontier.add(node);
		if (frontier.size() > localSpaceFrontier) {
			localSpaceFrontier = frontier.size();
		}
		List<String> explored = new LinkedList<String>();
		while (true) {
			if (frontier.isEmpty()) {
				return null;
			}
			node = frontier.poll();
			if (problem.goalTest(node.state)) {
				return node;
			}
			explored.add(node.state.toString());
			if (explored.size() > localSpaceExplored) {
				localSpaceExplored = explored.size();
			}
			for (Action action : problem.actions(node.state)) {
				Node child = new Node();
				localTime++;
				child.state = problem.result(node.state, action);
				child.parentNode = node;
				child.pathCost = problem.pathCost(node.pathCost, node.state, action);
				child.f = child.pathCost;
				if (!explored.contains(child.state.toString()) && !frontier.contains(child)) {
					frontier.add(child);
					if (frontier.size() > localSpaceFrontier) {
						localSpaceFrontier = frontier.size();
					}
				}
				if (frontier.contains(child)) {
					Node[] nodeArray = frontier.toArray(new Node[0]);
					for (int i = 0; i < nodeArray.length; i++) {
						if (nodeArray[i].state.equals(child.state)) {
							if (nodeArray[i].pathCost > child.pathCost) {
								frontier.remove(nodeArray[i]);
								frontier.add(child);
							} else {
								break;
							}
						}
					}
				}
			}
		}
	}

	// returns a Node on success or null on failure
	public Node iddfs(Problem problem) {
		int depth = 0;
		while (true) {
			Node result = dls(problem, depth);
			if (result != null) {
				if (result.state != null) {
					return result;
				}
				return null;
			}
			depth++;
		}
	}

	// returns a Node with state on success, an empty Node on cutoff, or null on failure
	private Node dls(Problem problem, int limit) {
		boolean cutoff = false;
		Node node = new Node();
		localTime++;
		node.state = problem.getInitialState();
		node.pathCost = 0;
		node.depth = 0;
		if (problem.goalTest(node.state)) {
			return node;
		}
		Stack<Node> frontier = new Stack<Node>();
		frontier.add(node);
		if (frontier.size() > localSpaceFrontier) {
			localSpaceFrontier = frontier.size();
		}
		List<String> explored = new LinkedList<String>();
		while (true) {
			if (frontier.isEmpty()) {
				if (cutoff) {
					return null;
				}
				return new Node();
			}
			node = frontier.pop();
			explored.add(node.state.toString());
			if (explored.size() > localSpaceExplored) {
				localSpaceExplored = explored.size();
			}
			for (Action action : problem.actions(node.state)) {
				Node child = new Node();
				localTime++;
				child.state = problem.result(node.state, action);
				child.parentNode = node;
				child.pathCost = problem.pathCost(node.pathCost, node.state, action);
				child.f = child.pathCost;
				child.depth = node.depth + 1;
				if (child.depth > limit) {
					cutoff = true;
					continue;
				}
				if (problem.goalTest(child.state)) {
					return child;
				}
				frontier.push(child);

				if (frontier.size() > localSpaceFrontier) {
					localSpaceFrontier = frontier.size();
				}
			}
		}
	}

	public Node greedy(Problem problem) {
		Node node = new Node();
		localTime++;
		node.state = problem.getInitialState();
		node.pathCost = 0;
		if (problem.goalTest(node.state)) {
			return node;
		}
		PriorityQueue<Node> frontier = new PriorityQueue<Node>();
		frontier.add(node);
		if (frontier.size() > localSpaceFrontier) {
			localSpaceFrontier = frontier.size();
		}
		List<String> explored = new LinkedList<String>();
		while (true) {
			if (frontier.isEmpty()) {
				return null;
			}
			node = frontier.poll();
			if (problem.goalTest(node.state)) {
				return node;
			}
			explored.add(node.state.toString());
			if (explored.size() > localSpaceExplored) {
				localSpaceExplored = explored.size();
			}
			for (Action action : problem.actions(node.state)) {
				Node child = new Node();
				localTime++;
				child.state = problem.result(node.state, action);
				child.parentNode = node;
				child.pathCost = problem.pathCost(node.pathCost, node.state, action);
				child.f = child.pathCost + problem.h(child.state);
				if (!explored.contains(child.state.toString()) && !frontier.contains(child)) {
					frontier.add(child);
					if (frontier.size() > localSpaceFrontier) {
						localSpaceFrontier = frontier.size();
					}
				}
				if (frontier.contains(child)) {
					Node[] nodeArray = frontier.toArray(new Node[0]);
					for (int i = 0; i < nodeArray.length; i++) {
						if (nodeArray[i].state.equals(child.state)) {
							if (nodeArray[i].pathCost > child.pathCost) {
								frontier.remove(nodeArray[i]);
								frontier.add(child);
							} else {
								break;
							}
						}
					}
				}
			}
		}
	}

	public Node Astar(Problem problem) {
		Node node = new Node();
		localTime++;
		node.state = problem.getInitialState();
		node.pathCost = 0;
		node.h = 0;
		if (problem.goalTest(node.state)) {
			return node;
		}
		PriorityQueue<Node> frontier = new PriorityQueue<Node>();
		frontier.add(node);
		if (frontier.size() > localSpaceFrontier) {
			localSpaceFrontier = frontier.size();
		}
		List<String> explored = new LinkedList<String>();
		while (true) {
			if (frontier.isEmpty()) {
				return null;
			}
			node = frontier.poll();
			if (problem.goalTest(node.state)) {
				return node;
			}
			explored.add(node.state.toString());
			if (explored.size() > localSpaceExplored) {
				localSpaceExplored = explored.size();
			}
			for (Action action : problem.actions(node.state)) {
				Node child = new Node();
				localTime++;
				child.state = problem.result(node.state, action);
				child.parentNode = node;
				child.pathCost = problem.pathCost(node.pathCost, node.state, action);
				child.f = child.pathCost + problem.h(child.state);
				child.h = node.h + problem.h(child.state);
				if (!explored.contains(child.state.toString()) && !frontier.contains(child)) {
					frontier.add(child);
					if (frontier.size() > localSpaceFrontier) {
						localSpaceFrontier = frontier.size();
					}
				}
				if (frontier.contains(child)) {
					Node[] nodeArray = frontier.toArray(new Node[0]);
					for (int i = 0; i < nodeArray.length; i++) {
						if (nodeArray[i].state.equals(child.state)) {
							if (nodeArray[i].pathCost > child.pathCost) {
								frontier.remove(nodeArray[i]);
								frontier.add(child);
							} else {
								break;
							}
						}
					}
				}
			}
		}
	}

	public class Node implements Comparable<Node> {
		public State state;
		public Node parentNode;
		public double pathCost;	// always gn
		public double f; 		// fn could be gn or gn+hn
		public double h;
		public int depth;

		public int compareTo(Node n2) {
			if (f < n2.f)
				return -1;
			else if (f > n2.f)
				return 1;
			else
				return 0;
		}

		public boolean equals(Node n2) {
			return state.equals(n2.state);
		}
	}
}