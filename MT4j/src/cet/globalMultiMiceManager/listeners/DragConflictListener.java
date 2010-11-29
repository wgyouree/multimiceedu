package cet.globalMultiMiceManager.listeners;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.util.math.Vector3D;

import cet.components.visibleComponents.widgets.CETWindow;
import cet.globalMultiMiceManager.ICETConflictListener;

public class DragConflictListener extends AbstractConflictListener {
	
	// the previous position
	private Vector3D previousPos;
	
	public DragConflictListener(ICETConflictListener listener, MTComponent component, MTApplication app) {
		super(listener, component, app);
	}
	
	public void inputDetected(MTInputEvent inEvt) {
		if ( !(component instanceof CETWindow) || app.checkOcclusionPolicy((CETWindow)component) ) {
			AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
			InputCursor cursor = cursorInputEvt.getCursor();
			IMTComponent3D target = component;
			Vector3D vector = new Vector3D(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
			//Put target on top -> draw on top of others
			if (target instanceof MTComponent){
				MTComponent baseComp = (MTComponent)target;
				baseComp.sendToFront();
			}
			previousPos = vector;
		}
	}
	
	public void inputUpdated(MTInputEvent inEvt) {
		if ( !(component instanceof CETWindow) || app.checkOcclusionPolicy((CETWindow)component) ) {
			AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
			InputCursor cursor = cursorInputEvt.getCursor();
			IMTComponent3D target = component;
			Vector3D vector = new Vector3D(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
			Vector3D translation = new Vector3D(
				vector.x - previousPos.x,
				vector.y - previousPos.y
			);
			previousPos = vector;
			target.translateGlobal(translation);
		}
	}

	public void inputEnded(MTInputEvent inEvt) {
		previousPos = null;
	}
}
