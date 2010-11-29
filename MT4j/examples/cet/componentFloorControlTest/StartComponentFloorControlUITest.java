package cet.componentFloorControlTest;

import org.mt4j.MTApplication;

import cet.globalMultiMiceManager.CETMultipleMiceManager;
import cet.helloWorld.HelloWorldScene;

public class StartComponentFloorControlUITest  extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		addCETMultiMiceManager( new CETMultipleMiceManager() );
		addScene(new ComponentFloorControlTestScene(this, "Component Floor Control Test Scene"));
	}
}
