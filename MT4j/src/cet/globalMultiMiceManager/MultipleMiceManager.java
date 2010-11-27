package cet.globalMultiMiceManager;

import java.util.WeakHashMap;

public class MultipleMiceManager {
	/** The device to mouse info from MultipleMiceInputSource. */
	private WeakHashMap<Integer, MouseInfo> deviceToMouseInfo = null;
	private int instructorDevice = -1;
	
	public MultipleMiceManager(){
		
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
		if( instructorDevice < 0 )
			instructorDevice = device;
	}
	
	public void loseDevice( int device ){
		if( instructorDevice == device ){
			if( deviceToMouseInfo.isEmpty() )
				instructorDevice = -1;
			else{
				instructorDevice = deviceToMouseInfo.keySet().iterator().next();
			}
		}
	}
	
	public int getInstructorDevice(){
		return instructorDevice;
	}
	
	public boolean changeInstructorDeviceTo(int device){
		if( deviceToMouseInfo.containsKey(device) ){
			instructorDevice = device;
			deviceToMouseInfo.get(device).setMouseRank(CETMouseRank.INSTRUCTOR);
			return true;
		}
		return false;
	}
}
