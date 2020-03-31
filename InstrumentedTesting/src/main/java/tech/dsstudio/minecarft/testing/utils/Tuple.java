package tech.dsstudio.minecarft.testing.utils;

public class Tuple<U, V> {
	public Tuple(U u, V v) {
		this.u = u;
		this.v = v;
	}

	public U getLeft() {
		return u;
	}

	public V getRight() {
		return v;
	}

	private U u;
	private V v;
}
