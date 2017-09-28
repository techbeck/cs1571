import java.util.List;
import java.util.Scanner;

public class AggregationProblem extends Problem {
	public State initialState;

	public AggregationProblem(Scanner inputReader) {
	}

	public void setInitialState(State init) {
		initialState = init;
	}
	public State getInitialState() {
		return null;
	}
	public double h(State state) {
		return 0;
	}
	public boolean goalTest(State state) {
		return false;
	}
	public List<Action> actions(State state) {
		return null;
	}
	public State result(State state, Action action) {
		return null;
	}
	public double pathCost(State state, Action action) {
		return 0;
	}
}