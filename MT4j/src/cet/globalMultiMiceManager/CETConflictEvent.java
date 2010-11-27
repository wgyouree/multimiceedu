package cet.globalMultiMiceManager;

import org.mt4j.MTApplication;
import org.mt4j.input.inputData.MTInputEvent;

public class CETConflictEvent {

	private MTInputEvent inEvt;
	private CETConflictType type;
	private MTApplication app;
	private Integer[] devices;
	
	public CETConflictEvent(MTInputEvent inEvt, CETConflictType type, MTApplication app, Integer[] devices) {
		this.inEvt = inEvt;
		this.type = type;
		this.app = app;
		this.devices = devices;
	}

	public MTInputEvent getInEvt() {
		return inEvt;
	}

	public CETConflictType getType() {
		return type;
	}

	public MTApplication getApp() {
		return app;
	}

	public Integer[] getDevices() {
		return devices;
	}
	
	
}