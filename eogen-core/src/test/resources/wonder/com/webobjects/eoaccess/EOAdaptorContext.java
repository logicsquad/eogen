package com.webobjects.eoaccess;

public abstract class EOAdaptorContext {
	private final EOAdaptor _adaptor;

	public EOAdaptorContext(EOAdaptor adaptor) {
		_adaptor = adaptor;
	}

	public EOAdaptor adaptor() {
		return _adaptor;
	}
}
