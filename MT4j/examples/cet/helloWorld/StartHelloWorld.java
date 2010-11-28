package cet.helloWorld;

import org.mt4j.MTApplication;

import cet.globalMultiMiceManager.CETMultipleMiceManager;

public class StartHelloWorld extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		addCETMultiMiceManager( new CETMultipleMiceManager() );
		addScene(new HelloWorldScene(this, "Hello World Scene"));
	}
}
