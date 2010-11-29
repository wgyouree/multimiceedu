package cet.componentMultiMiceManager;

import java.util.ArrayList;

import processing.core.PApplet;

import cet.globalMultiMiceManager.CETMultipleMiceManager;

/**
 * Controls which student mice have access to the component, 
 * and whether the students need to work together or take turns.
 * Instructor mouse always have access to the component.
 * 
 * @author Mengdie
 */

public class CETcomponentMultiMiceControl implements ICETMiceChangeListener{
	private CETMultipleMiceManager globalMMmanager;
	private ArrayList<Integer> studentDeviceList;
	private boolean isCollaborative;
	private CETcomponentMultiMiceControlUI ui = null;
	
	/**
	 * Floor Control for components
	 * @param m global multiple mice manager
	 */
	public CETcomponentMultiMiceControl( CETMultipleMiceManager m ){
		globalMMmanager = m;
		m.addMultiMiceChangeListener(this);
		studentDeviceList = new ArrayList<Integer>();
		isCollaborative = false;
		addAllStudentDevices();
	}
	
	public void addStudentDevice( int device ) {
		if( !studentDeviceList.contains(device) && globalMMmanager.getInstructorDevice() != device )
			studentDeviceList.add( device );
	}
	
	public void removeStudentDevice( int device ) {
		int index = studentDeviceList.indexOf(device);
		if( index > -1 )
			studentDeviceList.remove( index );
	}
	
	public void addAllStudentDevices() {
		studentDeviceList.clear();
		for( int device : globalMMmanager.getDeviceToMouseInfo().keySet() ){
			if( device != globalMMmanager.getInstructorDevice() )
				studentDeviceList.add( device );
		}
	}
	
	public void removeAllStudentDevices() {
		studentDeviceList.clear();
	}
	
	public ArrayList<Integer> getStudentDeviceList(){
		return studentDeviceList;
	}
	
	public boolean isDeviceAccessible(int device){
		return studentDeviceList.contains(device);
	}
	
	public boolean isCollaborative() {
		return isCollaborative;
	}
	
	public void setCollaborative( boolean is ) {
		isCollaborative = is;
	}
	
	// Multiple mice change events handling
	
	public void deviceConnected(int device) {
		addStudentDevice( device );
		if( ui != null )
			ui.update();
	}
	
	public void deviceDisconnected(int device) {
		if( studentDeviceList.contains(device) )
			studentDeviceList.remove( device );
		if( ui != null )
			ui.update();
	}

	public void instructorChanged(int device) {
		int instructorDevice = globalMMmanager.getInstructorDevice();
		if( studentDeviceList.contains( instructorDevice ) ) 
			studentDeviceList.remove( instructorDevice );
		if( ui != null )
			ui.update();
	}
	
	/**
	 * Create floor control UI for component
	 * @param x The x-center of the UI icon
	 * @param y The y-center of the UI icon
	 * @param p The MT application
	 * @return
	 */
	public CETcomponentMultiMiceControlUI createUI( int x, int y, PApplet p ){
		ui = new CETcomponentMultiMiceControlUI( x, y, this, p );
		return ui;
	}
}
