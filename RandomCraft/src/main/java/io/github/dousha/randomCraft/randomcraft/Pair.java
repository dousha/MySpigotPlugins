package io.github.dousha.randomCraft.randomcraft;

public class Pair<K, V> {
	private K first;
	private V second;
	
	Pair(){}
	
	Pair(K k, V v){
		first = k;
		second = v;
	}
	
	K getFirst(){
		return first;
	}
	
	void setFirst(K k){
		first = k;
	}
	
	V getSecond(){
		return second;
	}
	
	void setSecond(V v){
		second = v;
	}
	
	void put(K k, V v){
		first = k;
		second = v;
	}
	
}
