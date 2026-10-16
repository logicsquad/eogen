package net.logicsquad.eogen.model.history;

import net.logicsquad.eogen.model.EOAttribute;

public class EOAttributeAddedEvent extends AbstractModelEvent {
	private EOAttribute _attribute;

	public EOAttributeAddedEvent(EOAttribute attribute) {
		_attribute = attribute;
	}

	public EOAttribute getAttribute() {
		return _attribute;
	}

	public boolean isEncompassedBy(IModelEvent event) {
		return event instanceof EOEntityAddedEvent && ((EOEntityAddedEvent) event).getEntity().equals(_attribute.getEntity());
	}

	public boolean isReplacedBy(IModelEvent event) {
		return event instanceof EOAttributeDeletedEvent && ((EOAttributeDeletedEvent) event).getAttribute().equals(_attribute);
	}
}
