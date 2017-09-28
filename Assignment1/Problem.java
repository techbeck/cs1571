import java.util.List;
import java.util.Scanner;

public abstract class Problem {
	public abstract void setInitialState(State init);
	public abstract State getInitialState();
	public abstract double h(State state);
	public abstract boolean goalTest(State state);
	public abstract List<Action> actions(State state);
	public abstract State result(State state, Action action);
	public abstract double pathCost(State state, Action action);
}