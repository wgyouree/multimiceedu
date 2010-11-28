package cet.globalMultiMiceManager;

import java.util.ArrayList;
import java.util.WeakHashMap;

import cet.componentMultiMiceManager.ICETMiceChangeListener;

public class CETMultipleMiceManager {
	/** The device to mouse info from MultipleMiceInputSource. */
	private WeakHashMap<Integer, MouseInfo> deviceToMouseInfo = null;
	private int instructorDevice = -1;
	
	protected ArrayList<ICETMiceChangeListener> listenerList;
	
	public CETMultipleMiceManager(){
		this.listenerList = new ArrayList<ICETMiceChangeListener>();
		this.deviceToMouseInfo = new WeakHashMap<Integer, MouseInfo>();
	}
	
	public WeakHashMap<Integer, MouseInfo> getDeviceToMouseInfo(){
		return deviceToMouseInfo;
	}
	
	public MouseInfo getMouseInfo(int device) {
		return deviceToMouseInfo.get(device);
	}
	
	public void setDeviceToMouseInfo( WeakHashMap<Integer, MouseInfo> dtmi ){
		deviceToMouseInfo = dtmi;
	}
	
	public void addDevice( int device ){
		if( instructorDevice < 0 ) {
			instructorDevice = device;
			for( ICETMiceChangeListener l : listenerList )
				l.instructorChanged( device );
		}
		for( ICETMiceChangeListener l : listenerList )
			l.deviceConnected( device );
	}
	
	public void loseDevice( int device ){
		if( instructorDevice == device ){
			if( deviceToMouseInfo.isEmpty() )
				instructorDevice = -1;
			else
				instructorDevice = deviceToMouseInfo.keySet().iterator().next();
			for( ICETMiceChangeListener l : listenerList )
				l.instructorChanged( instructorDevice );
		}
		for( ICETMiceChangeListener l : listenerList )
			l.deviceDisconnected( device );
	}
	
	public int getInstructorDevice(){
		return instructorDevice;
	}
	
	public boolean changeInstructorDeviceTo(int device){
		if( deviceToMouseInfo.containsKey(device) ){
			instructorDevice = device;
			deviceToMouseInfo.get(device).setMouseRank(CETMouseRank.INSTRUCTOR);
			for( ICETMiceChangeListener l : listenerList )
				l.instructorChanged( device );
			return true;
		}
		return false;
	}
	
	public void addMultiMiceChangeListener( ICETMiceChangeListener l ) {
		listenerList.add( l );
	}
	
	public void removeMultiMiceChangeListener( ICETMiceChangeListener l ) {
		listenerList.remove( l );
	}
	
	public ArrayList<ICETMiceChangeListener> getMultiMiceChangeListeners() {
		return listenerList;
	}
}
