public abstract class State {
	public abstract String[] getStateArray();
	public abstract void setState(String[] s);
	public abstract String toString();
	public abstract boolean equals(State s2);
}