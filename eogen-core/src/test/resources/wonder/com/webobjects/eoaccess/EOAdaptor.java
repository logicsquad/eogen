package com.webobjects.eoaccess;

public class EOAdaptor {
	private final EOSynchronizationFactory _synchronizationFactory;

	/**
	 * Constructor, for the tests: Wonder's makes its plugin's own factory.
	 */
	public EOAdaptor(EOSynchronizationFactory synchronizationFactory) {
		_synchronizationFactory = synchronizationFactory;
	}

	public EOSynchronizationFactory synchronizationFactory() {
		return _synchronizationFactory;
	}
}
