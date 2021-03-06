import java.util.Arrays;

public class MonitorState extends State {
	public int[] sensors;

	public MonitorState(int numSensors) {
		sensors = new int[numSensors];
	}
	public String[] getStateArray() {
		String[] result = new String[sensors.length];
		for (int i = 0; i < sensors.length; i++) {
			result[i] = Integer.toString(sensors[i]);
		}
		return result;
	}
	public void setState(String[] s) {
		sensors = new int[s.length];
		for (int i = 0; i < s.length; i++) {
			try {
				sensors[i] = Integer.parseInt(s[i]);
			} catch (Exception ex) {
				sensors[i] = 0;
			}
		}
	}
	public String toString() {
		return Arrays.toString(sensors).replaceAll("[\\[\\]]","");
	}
	public boolean equals(State s2) {
		return this.toString().equals(s2.toString());
	}
}