package net.logicsquad.eogen.model.history;

import net.logicsquad.eogen.model.EOEntity;

public class EOEntityRenamedEvent extends AbstractModelEvent {
	private EOEntity _entity;

	public EOEntityRenamedEvent(EOEntity entity) {
		_entity = entity;
	}

	public EOEntity getEntity() {
		return _entity;
	}

	public boolean isEncompassedBy(IModelEvent event) {
		return event instanceof EOEntityAddedEvent && ((EOEntityAddedEvent) event).getEntity().equals(_entity.getEntity());
	}

	public boolean isReplacedBy(IModelEvent event) {
		return (event instanceof EOEntityDeletedEvent && ((EOEntityDeletedEvent) event).getEntity().equals(_entity)) || ((event instanceof EOEntityRenamedEvent && ((EOEntityRenamedEvent) event).getEntity().equals(_entity)));
	}
}
