import java.util.Arrays;

public class AggregationState extends State {
	String[] nodes;
	public AggregationState(int numNodes) {
		nodes = new String[numNodes];
	}
	public String[] getStateArray() {
		return nodes;	
	}
	public void setState(String[] s) {
		nodes = s;
	}
	public String toString() {
		return Arrays.toString(nodes).replaceAll("[\\[\\]]","");
	}

	public boolean equals(State s2) {
		return this.toString().equals(s2.toString());
	}
}