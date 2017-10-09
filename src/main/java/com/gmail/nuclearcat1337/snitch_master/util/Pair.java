package com.gmail.nuclearcat1337.snitch_master.util;

public class Pair<T, K> {
	private T t;
	private K k;

	public Pair(T t, K k) {
		this.t = t;
		this.k = k;
	}

	public T getOne() {
		return t;
	}

	public K getTwo() {
		return k;
	}

	public void setValues(T t, K k) {
		this.t = t;
		this.k = k;
	}
}
