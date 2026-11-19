package com.webobjects.eoaccess;

public class EOAdaptorChannel {
	private final EOAdaptorContext _context;

	public EOAdaptorChannel(EOAdaptorContext context) {
		_context = context;
	}

	public EOAdaptorContext adaptorContext() {
		return _context;
	}
}
