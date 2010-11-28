package cet.globalMultiMiceManager.listeners;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputSources.MultipleMiceInputSource;
import org.mt4j.util.math.Vector3D;

import cet.globalMultiMiceManager.ICETConflictListener;
import cet.globalMultiMiceManager.cursors.CursorType;

public class ScaleConflictListener extends AbstractConflictListener {
	
	// the previous position
	private Vector3D previousPos;
	
	private CursorType type;
	
	public ScaleConflictListener(CursorType type, ICETConflictListener listener, MTComponent component, MTApplication app) {
		super(listener, component, app);
		this.type = type;
	}
	
	public void inputDetected(MTInputEvent inEvt) {
		AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
		InputCursor cursor = cursorInputEvt.getCursor();
		if( inEvt.getSource() instanceof MultipleMiceInputSource ) {
			int device = ((MultipleMiceInputSource) inEvt.getSource() ).getEventSourceDevice();
			app.getCETMultiMiceManager().getMouseInfo(device).useCursorIcon(
				type, app, app.getCurrentScene(), app.getCurrentScene().getSceneCam()
			);
		}
		IMTComponent3D target = component;
		Vector3D vector = new Vector3D(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
		//Put target on top -> draw on top of others
		if (target instanceof MTComponent){
			MTComponent baseComp = (MTComponent)target;
			baseComp.sendToFront();
		}
		previousPos = vector;
	}
	
	public void inputUpdated(MTInputEvent inEvt) {
		AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
		InputCursor cursor = cursorInputEvt.getCursor();
		IMTComponent3D target = component;
		Vector3D vector = new Vector3D(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
		switch ( type ) {
		case TOP_LEFT:
			target.scaleGlobal(
				vector.x - previousPos.x,
				vector.y - previousPos.y,
				0,
				vector
			);
			break;
		case TOP:
			break;
		case TOP_RIGHT:
			break;
		case RIGHT:
			break;
		case BOTTOM_RIGHT:
			break;
		case BOTTOM:
			break;
		case BOTTOM_LEFT:
			break;
		case LEFT:
			break;
		default:
			System.err.println("Unrecognzied CursorType");
			break;
		}
		Vector3D translation = new Vector3D(
			vector.x - previousPos.x,
			vector.y - previousPos.y
		);
		previousPos = vector;
		target.translateGlobal(translation);
	}

	public void inputEnded(MTInputEvent inEvt) {
		previousPos = null;
		app.getCETMultiMiceManager().getMouseInfo(device).useCursorIcon(
			CursorType.ARROW, app, app.getCurrentScene(), app.getCurrentScene().getSceneCam()
		);
	}
}
