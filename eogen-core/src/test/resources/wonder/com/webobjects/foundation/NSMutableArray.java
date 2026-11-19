package com.webobjects.foundation;

public class NSMutableArray<T> extends NSArray<T> {
	public NSMutableArray() {
	}

	public NSMutableArray(T object) {
		super(object);
	}

	public NSMutableArray(T[] objects) {
		super(objects);
	}

	public NSMutableArray(NSArray<? extends T> otherArray) {
		super(otherArray);
	}

	public void addObject(T object) {
		_objects.add(object);
	}

	public boolean removeObject(Object object) {
		return _objects.remove(object);
	}
}
