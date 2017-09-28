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
		// System.out.println(toString());
	}

	public String toString() {
		String result = "";
		for (int i = 0; i < pancakes.length; i++) {
			result = result + pancakes[i] + ",";
		}
		return result;
	}
}