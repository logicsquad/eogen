package net.logicsquad.eogen.model.history;

import net.logicsquad.eogen.model.EOEntity;

public class EOEntityAddedEvent extends AbstractModelEvent {
	private EOEntity _entity;

	public EOEntityAddedEvent(EOEntity entity) {
		_entity = entity;
	}

	public EOEntity getEntity() {
		return _entity;
	}

	public boolean isEncompassedBy(IModelEvent event) {
		return false;
	}

	public boolean isReplacedBy(IModelEvent event) {
		return event instanceof EOEntityDeletedEvent && ((EOEntityDeletedEvent) event).getEntity().equals(_entity);
	}
}
