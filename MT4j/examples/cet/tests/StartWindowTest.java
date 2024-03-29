package cet.tests;

import org.mt4j.MTApplication;

import cet.globalMultiMiceManager.CETMultipleMiceManager;

public class StartWindowTest extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		addCETMultiMiceManager( new CETMultipleMiceManager() );
		addScene(new WindowTest(this, "Window Test Scene"));
	}
}
