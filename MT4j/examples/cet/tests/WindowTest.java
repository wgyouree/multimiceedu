package cet.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.math.Vector3D;

import cet.components.visibleComponents.widgets.CETButton;
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
		
		// Add buttons to Window2
		final MTTextArea textField = new MTTextArea(mtApplication);
		textField.setNoStroke(true);
		textField.setNoFill(true);
		textField.setPickable(false);
		textField.setPositionRelativeToParent( new Vector3D(600, 250) );
		textField.setText("Click on left button.");
		window2.addChild( textField );
		CETButton clickButton = new CETButton("Click me", 450 + 100, 10 + 150, 150, 50, mtApplication);
		clickButton.addEventListener( new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				textField.setText("Left button clicked. Click on right button to reset.");
			}			
		});
		CETButton resetButton = new CETButton("Reset", 450 + 350, 10 + 150, 150, 50, mtApplication);
		resetButton.addEventListener( new ActionListener() {
			public void actionPerformed(ActionEvent event){
				textField.setText("Click on left button.");
			}
		});
		window2.addChild( clickButton );
		window2.addChild( resetButton );
		
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
