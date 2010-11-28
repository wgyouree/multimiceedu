package cet.componentMultiMiceManager;

import java.util.ArrayList;

import cet.globalMultiMiceManager.CETMultipleMiceManager;

/**
 * Controls which student mice have access to the component, 
 * and whether the students need to work together or take turns.
 * Instructor mouse always have access to the component.
 * 
 * @author Mengdie
 */

public class CETcomponentMultiMiceControl implements ICETMiceChangeListener{
	CETMultipleMiceManager globalMMmanager;
	ArrayList<Integer> studentDeviceList;
	boolean isCollaborative;
	
	public CETcomponentMultiMiceControl( CETMultipleMiceManager m ){
		globalMMmanager = m;
		studentDeviceList = new ArrayList<Integer>();
		isCollaborative = false;
	}
	
	public void addStudentDevice( int device ) {
		if( !studentDeviceList.contains(device) )
			studentDeviceList.add( device );
	}
	
	public void removeStudentDevice( int device ) {
		if( studentDeviceList.contains(device) )
			studentDeviceList.remove( device );
	}
	
	public ArrayList<Integer> getStudentDeviceList(){
		return studentDeviceList;
	}
	
	public boolean isCollaborative() {
		return isCollaborative;
	}
	
	public void setCollaborative( boolean is ) {
		isCollaborative = is;
	}
	
	// Multiple mice change events handling
	
	public void deviceConnected(int device) {
	}
	
	public void deviceDisconnected(int device) {
		if( studentDeviceList.contains(device) )
			studentDeviceList.remove( device );
	}

	public void instructorChanged(int device) {
		int instructorDevice = globalMMmanager.getInstructorDevice();
		if( studentDeviceList.contains( instructorDevice ) )
			studentDeviceList.remove( instructorDevice );
	}
	
}
