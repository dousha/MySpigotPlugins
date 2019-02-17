package tech.dsstudio.minecraft.portapack.internal;

import java.util.Comparator;
import java.util.HashMap;

public class Cascade<T> {
	public T get(Integer i) {
		return content.keySet().stream().filter(it -> it >= i).min(Comparator.comparingInt(l -> l)).map(integer -> content.get(integer)).orElse(null);
	}

	public void put(Integer i, T v) {
		content.put(i, v);
	}

	public void clear() {
		content.clear();
	}

	public HashMap<Integer, T> getContent() {
		return content;
	}

	private HashMap<Integer, T> content = new HashMap<>();
}
