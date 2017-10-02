import java.util.Arrays;

public class PancakeState extends State {
	public int[] pancakes;

	public PancakeState(int numSensors) {
		pancakes = new int[numSensors];
	}

	public String[] getStateArray() {
		String[] result = new String[pancakes.length];
		for (int i = 0; i < pancakes.length; i++) {
			result[i] = Integer.toString(pancakes[i]);
		}
		return result;
	}

	public void setState(String[] p) {
		pancakes = new int[p.length];
		for (int i = 0; i < p.length; i++) {
			try {
				pancakes[i] = Integer.parseInt(p[i]);
			} catch (Exception ex) {
				pancakes[i] = 0;
			}
		}
	}

	public String toString() {
		return Arrays.toString(pancakes).replaceAll("[\\[\\]]","");
	}

	public boolean equals(State s2) {
		return this.toString().equals(s2.toString());
	}
}