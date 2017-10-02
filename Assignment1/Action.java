public class Action {
	public State state1;
	public State state2;

	public State getOther(State state) {
		if (state.toString().equals(state2.toString())) {
			return state1;
		} else {
			return state2;
		}
	}
}