package cet.tests;

import org.mt4j.MTApplication;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;

import cet.components.visibleComponents.widgets.CETWindow;
import cet.globalMultiMiceManager.conflictHandlers.ShakeDragConflictHandler;
import cet.globalMultiMiceManager.occlusion.OcclusionPolicy;

public class WindowTest extends AbstractScene {

	public WindowTest(MTApplication mtApplication, String name) {
		super(mtApplication, name);

		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		// create a window
		CETWindow window1 = mtApplication.createNewWindow("Test Window 1",10,10,0,400,400,OcclusionPolicy.PREVENT);
		window1.addConflictHandler(new ShakeDragConflictHandler());
		this.getCanvas().addChild(window1);
		
		// create another window
		CETWindow window2 = mtApplication.createNewWindow("Test Window 2",450,10,0,400,400);
		window2.addConflictHandler(new ShakeDragConflictHandler());
		this.getCanvas().addChild(window2);
	}
	@Override
	public void init() {}
	@Override
	public void shutDown() {}
}
