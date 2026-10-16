package net.logicsquad.eogen.model.history;

public interface IModelEvent {
	public boolean isEncompassedBy(IModelEvent event);

	public boolean isReplacedBy(IModelEvent event);
}
