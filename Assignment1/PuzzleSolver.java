import java.io.*;
import java.lang.Comparable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class PuzzleSolver {
	public static String algorithm;
	public static String finalPath = "";
	public static int finalTime = Integer.MAX_VALUE;
	public static int finalSpaceFrontier = Integer.MAX_VALUE;
	public static int finalSpaceExplored = Integer.MAX_VALUE;
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
			String problemType = inputReader.nextLine();
			if (problemType.equals("monitor")) {
				solveMonitorProblem(inputReader);
			} else if (problemType.equals("(aggregation)")) {
				solveAggregationProblem(inputReader);
			} else {
				System.out.println("Invalid problem type");
				System.exit(0);
			}
		}

		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter("output.txt");
		} catch (IOException io) {
			System.out.println("Problem creating output file");
			System.exit(0);
		}
		if (finalPath == null) {
			System.out.println("No solution");
		} else {
			System.out.print("Solution:\n" + finalPath);
			outputWriter.println("Final path: " + finalPath);
			System.out.println("Final Path Cost: " + finalCost);
			outputWriter.println("Total Cost: " + finalCost);
			System.out.println("Final Time: " + finalTime);
			outputWriter.println("Time: " + finalTime);
			System.out.println("Final Space Frontier: " + finalSpaceFrontier);
			outputWriter.println("Space used for frontier: " + finalSpaceFrontier);
			System.out.println("Final Space Explored: " + finalSpaceExplored);
			outputWriter.println("Space used for explored: " + finalSpaceExplored);
		}
	}

	public void solveMonitorProblem(Scanner inputReader) {
		Problem problem = new MonitorProblem(inputReader);

		Node solution = null;
		if (algorithm.equals("bfs")) {
			solution = bfs(problem);

		} else if (algorithm.equals("unicost")) {
			solution = unicost(problem);
		} else if (algorithm.equals("iddfs")) {
			solution = iddfs(problem);
		}
		// } else if (algorithm.equals("greedy")) {
		// 	solution = greedy(problem);
		// } else if (algorithm.equals("Astar")) {
		// 	solution = Astar(problem);
		// }
		if (solution == null) {
			System.out.println("No solution");
			finalPath = null;
			return;
		} else {
			finalCost = (-(solution.pathCost));
			finalTime = localTime;
			finalSpaceFrontier = localSpaceFrontier;
			finalSpaceExplored = localSpaceExplored;
			for (int i = 0; i < solution.state.getState().length; i++) {
				String s = solution.state.getState()[i];
				if (!s.equals("0")) {
					finalPath += ("S" + (i+1) + "-T" + s + "\n");
				} else {
					finalPath += ("S" + (i+1) + "-x" + "\n");
				}
			}
		}
	}

	public void solveAggregationProblem(Scanner inputReader) {
		// TODO
		Problem problem = new AggregationProblem(inputReader);

		Node solution = null;
		if (algorithm.equals("bfs")) {
			solution = bfs(problem);
		} else if (algorithm.equals("unicost")) {
			solution = unicost(problem);
		} else if (algorithm.equals("iddfs")) {
			solution = iddfs(problem);
		}
		// } else if (algorithm.equals("greedy")) {
		// 	solution = greedy(problem);
		// } else if (algorithm.equals("Astar")) {
		// 	solution = Astar(problem);
		// }
		if (solution == null) {
			finalPath = null;
			return;
		}
	}

	// returns a Node on success or null on failure
	public Node bfs(Problem problem) {
		// LinkedList addLast(element) and removeFirst() for FIFO queue

		Node node = new Node();
		localTime++;
		node.state = problem.getInitialState();
		node.pathCost = 0;
		node.depth = 0;
		// node ←a node with STATE = problem.INITIAL-STATE, PATH-COST = 0
		if (problem.goalTest(node.state)) {
			return node;
			// return SOLUTION(node)
		}
		LinkedList<Node> frontier = new LinkedList<Node>();
		frontier.addLast(node);
		if (frontier.size() > localSpaceFrontier) {
			localSpaceFrontier = frontier.size();
		}
		//frontier ←a FIFO queue with node as the only element
		List<State> explored = new LinkedList<State>();
		// explored ←an empty set
		while (true) {
			if (frontier.isEmpty()) {
				return null;
			}
			node = frontier.removeFirst();
			explored.add(node.state);
			if (explored.size() > localSpaceExplored) {
				localSpaceExplored = explored.size();
			}
			for (Action action : problem.actions(node.state)) {
				Node child = new Node();
				localTime++;
				child.state = problem.result(node.state, action);
				child.parentNode = node;
				child.pathCost = problem.pathCost(node.state, action);
				child.depth = node.depth + 1;
				if (!explored.contains(child.state) && !frontier.contains(child.state)) {
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
		node.depth = 0;
		if (problem.goalTest(node.state)) {
			return node;
		}
		PriorityQueue<Node> frontier = new PriorityQueue<Node>();
		frontier.add(node);
		if (frontier.size() > localSpaceFrontier) {
			localSpaceFrontier = frontier.size();
		}
		List<State> explored = new LinkedList<State>();
		while (true) {
			if (frontier.isEmpty()) {
				return null;
			}
			node = frontier.poll();
			explored.add(node.state);
			if (explored.size() > localSpaceExplored) {
				localSpaceExplored = explored.size();
			}
			for (Action action : problem.actions(node.state)) {
				Node child = new Node();
				localTime++;
				child.state = problem.result(node.state, action);
				child.parentNode = node;
				child.pathCost = problem.pathCost(node.state, action);
				child.depth = node.depth + 1;
				if (!explored.contains(child.state) && !frontier.contains(child.state)) {
					if (problem.goalTest(child.state)) {
						return child;
					}
					frontier.add(child);
					if (frontier.size() > localSpaceFrontier) {
						localSpaceFrontier = frontier.size();
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
			if (result == null) {
				return null;
			} else if (result.state != null) {
				return result;
			}
			depth++;
		}
	}

	// returns a Node with state on success, an empty Node on cutoff, or null on failure
	private Node dls(Problem problem, int limit) {
		Node node = new Node();
		node.state = problem.getInitialState();
		return recDLS(node, problem, limit);
	}

	// returns a Node with state on success, an empty Node on cutoff, or null on failure
	private Node recDLS(Node node, Problem problem, int limit) {
		if (node.state != null && problem.goalTest(node.state)) {
			return node;
		} else if (limit == 0) {
			return new Node();
		} else {
			boolean cutoffOccurred = false;
			for (Action action : problem.actions(node.state)) {
				Node child = new Node();
				child.state = problem.result(node.state, action);
				Node result = recDLS(child , problem, limit-1);
				if (result != null && result.state == null) { // aka if cutoff occurred
					cutoffOccurred = true;
				} else if (result != null) {
					return result;
				}
			}
			if (cutoffOccurred) {
				return new Node();
			} else {
				return null;
			}
		}
	}

	public class Node implements Comparable<Node> {
		public State state;
		public Node parentNode;
		public double pathCost;
		public int depth;

		public int compareTo(Node n2) {
			if (pathCost < n2.pathCost)
				return -1;
			else if (pathCost > n2.pathCost)
				return 1;
			else
				return 0;
		}
	}
}