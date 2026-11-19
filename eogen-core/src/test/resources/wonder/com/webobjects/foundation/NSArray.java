package com.webobjects.foundation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NSArray<T> implements Iterable<T> {
	final List<T> _objects = new ArrayList<T>();

	public NSArray() {
	}

	public NSArray(T object) {
		_objects.add(object);
	}

	public NSArray(T[] objects) {
		for (T object : objects) {
			_objects.add(object);
		}
	}

	public NSArray(NSArray<? extends T> otherArray) {
		_objects.addAll(otherArray._objects);
	}

	public int count() {
		return _objects.size();
	}

	public T objectAtIndex(int index) {
		return _objects.get(index);
	}

	public NSArray<T> arrayByAddingObjectsFromArray(NSArray<? extends T> otherArray) {
		NSArray<T> array = new NSArray<T>(this);
		if (otherArray != null) {
			array._objects.addAll(otherArray._objects);
		}
		return array;
	}

	@Override
	public Iterator<T> iterator() {
		return _objects.iterator();
	}
}
