package tech.dsstudio.minecraft.taskhook;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<T> implements Set<T> {
	private Set<T> set;

	public ConcurrentHashSet() {
		set = ConcurrentHashMap.newKeySet();
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return set.iterator();
	}

	@NotNull
	@Override
	public Object[] toArray() {
		return set.toArray();
	}

	@NotNull
	@Override
	public <T1> T1[] toArray(@NotNull T1[] t1s) {
		return set.toArray(t1s);
	}

	@Override
	public boolean add(T t) {
		return set.add(t);
	}

	@Override
	public boolean remove(Object o) {
		return set.remove(o);
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> collection) {
		return set.containsAll(collection);
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends T> collection) {
		return set.addAll(collection);
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> collection) {
		return set.retainAll(collection);
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> collection) {
		return set.removeAll(collection);
	}

	@Override
	public void clear() {
		set.clear();
	}
}
