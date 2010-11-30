package cet.tests;

import org.mt4j.MTApplication;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;

import cet.components.visibleComponents.widgets.CETWindow;
import cet.globalMultiMiceManager.conflictHandlers.ShakeDragConflictHandler;
import cet.globalMultiMiceManager.occlusion.OcclusionPolicy;
import cet.globalMultiMiceManager.occlusion.RedBoxOcclusionHandler;

public class WindowTest extends AbstractScene {

	public WindowTest(MTApplication mtApplication, String name) {
		super(mtApplication, name);

		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		// create a window
		CETWindow window1 = mtApplication.createNewWindow("Test Window 1 - DEFER",10,10,0,400,300,OcclusionPolicy.DEFER);
		window1.addConflictHandler(new ShakeDragConflictHandler());
		window1.addOcclusionHandler(new RedBoxOcclusionHandler());
		this.getCanvas().addChild(window1);
		
		// create another window
		CETWindow window2 = mtApplication.createNewWindow("Test Window 2 - DEFER",450,10,0,600,400,OcclusionPolicy.DEFER);
		window2.addConflictHandler(new ShakeDragConflictHandler());
		window2.addOcclusionHandler(new RedBoxOcclusionHandler());
		this.getCanvas().addChild(window2);
		
		// create another window
		CETWindow window3 = mtApplication.createNewWindow("Test Window 3 - PREVENT",10,400,0,200,200,OcclusionPolicy.PREVENT);
		this.getCanvas().addChild(window3);
		
		// create another window
		CETWindow window4 = mtApplication.createNewWindow("Test Window 4 - NONE",300,450,0,200,200,OcclusionPolicy.NONE);
		this.getCanvas().addChild(window4);

		// create another window
		CETWindow window5 = mtApplication.createNewWindow("Test Window 5 - NONE",550,450,0,200,200,OcclusionPolicy.NONE);
		this.getCanvas().addChild(window5);
	}
	@Override
	public void init() {}
	@Override
	public void shutDown() {}
}
