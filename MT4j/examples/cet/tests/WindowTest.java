package cet.tests;

import org.mt4j.MTApplication;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;

import cet.components.visibleComponents.widgets.CETWindow;

public class WindowTest extends AbstractScene {

	public WindowTest(MTApplication mtApplication, String name) {
		super(mtApplication, name);

		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		// create a window
		CETWindow window = new CETWindow("Test Window",0,0,0,400,400,mtApplication);
		this.getCanvas().addChild(window);
	}
	@Override
	public void init() {}
	@Override
	public void shutDown() {}
}
