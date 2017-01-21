package com.jarvis.server.data;

import java.util.ArrayList;
import java.util.Collection;

public class UniqueArrayList<T> extends ArrayList<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -819311320442943286L;

	public boolean add(T e) {
		if (!contains(e))
			return super.add(e);
		else
			return false;
	}

	public void add(int index, T element) {
		if (!contains(element))
			super.add(index, element);
	}

	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for (T element : c) {
			if (!contains(element)) {
				super.add(element);
				changed = true;
			}
		}
		return changed;
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		boolean changed = false;
		for (T element : c) {
			if (!contains(element)) {
				super.add(index++, element);
				changed = true;
			}
		}
		return changed;
	}

}
