package cet.componentMultiMiceManager;

public interface ICETMiceChangeListener {
	public void deviceConnected( int device );
	public void deviceDisconnected( int device );
	public void instructorChanged( int device );
}
