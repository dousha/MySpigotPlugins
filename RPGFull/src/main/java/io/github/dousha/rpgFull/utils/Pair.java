package io.github.dousha.rpgFull.utils;

public class Pair<K, V> implements Cloneable{
	private K first;
	private V second;
	
	public K getFirst() {
		return first;
	}
	public void setFirst(K first) {
		this.first = first;
	}
	public V getSecond() {
		return second;
	}
	public void setSecond(V second) {
		this.second = second;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Pair<K, V> clone(){
		Pair<K, V> o = null;
		try{
			o = (Pair<K, V>) super.clone();
		}catch(CloneNotSupportedException ex){
			ex.printStackTrace();
		}
		return o;
	}
}
