package cet.globalMultiMiceManager;

public interface ICETConflictListener {
	public void addConflictHandler(ICETConflictHandler handler);
	public void removeConflictHandler(ICETConflictHandler handler);
	public void processConflict(CETConflictEvent event);
}
