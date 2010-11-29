package cet.globalMultiMiceManager.listeners;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputSources.MultipleMiceInputSource;
import org.mt4j.util.math.Vector3D;

import cet.components.visibleComponents.widgets.CETWindow;
import cet.globalMultiMiceManager.ICETConflictListener;
import cet.globalMultiMiceManager.Size;
import cet.globalMultiMiceManager.cursors.CursorType;

public class ScaleConflictListener extends AbstractConflictListener {
	
	// the previous position
	private Vector3D previousPos;
	
	private CursorType type;
	
	private CETWindow window;
	
	public ScaleConflictListener(CursorType type, ICETConflictListener listener, CETWindow component, MTApplication app) {
		super(listener, component, app);
		this.type = type;
		this.window = component;
	}
	
	public void inputDetected(MTInputEvent inEvt) {
		if ( app.checkOcclusionPolicy(window) ) {
			AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
			InputCursor cursor = cursorInputEvt.getCursor();
			/*
			if( inEvt.getSource() instanceof MultipleMiceInputSource ) {
				int device = ((MultipleMiceInputSource) inEvt.getSource() ).getEventSourceDevice();
				app.getCETMultiMiceManager().getMouseInfo(device).useCursorIcon(
					type, app, app.getCurrentScene(), app.getCurrentScene().getSceneCam()
				);
			}
			*/
			Vector3D vector = new Vector3D(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
			previousPos = vector;
		}
	}
	
	public void inputUpdated(MTInputEvent inEvt) {
		if ( !app.checkOcclusionPolicy(window) ) {
			AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
			InputCursor cursor = cursorInputEvt.getCursor();
			Vector3D vector = new Vector3D(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
			float deltaX = vector.x - previousPos.x;
			float deltaY = vector.y - previousPos.y;
			float deltaWidth = 0;
			float deltaHeight = 0;
			Vector3D translation = null;
			switch ( type ) {
			case TOP_LEFT:
				System.out.println("scaling top left");
				deltaWidth = window.getWidth() + (-1 * deltaX);
				deltaHeight = window.getHeight() + (-1 * deltaY);
				translation = new Vector3D(
					deltaX,
					deltaY
				);
				previousPos = vector;
				window.translateGlobal(translation);
				window.setSize(new Size(
					deltaWidth,
					deltaHeight)
				);
				break;
			case TOP:
				System.out.println("scaling top");
				translation = new Vector3D(
					0,
					deltaY
				);
				previousPos = vector;
				window.translateGlobal(translation);
				window.setSize(new Size(
					window.getWidth(),
					deltaHeight)
				);
				break;
			case TOP_RIGHT:
				deltaWidth = window.getWidth() + deltaX;
				deltaHeight = window.getHeight() + (-1 * deltaY);
				translation = new Vector3D(
					0,
					deltaY
				);
				previousPos = vector;
				window.translateGlobal(translation);
				window.setSize(new Size(
					deltaWidth,
					deltaHeight)
				);
				break;
			case RIGHT:
				previousPos = vector;
				window.setSize(new Size(
					deltaWidth,
					window.getHeight())
				);
				break;
			case BOTTOM_RIGHT:
				deltaWidth = window.getWidth() + deltaX;
				deltaHeight = window.getHeight() + deltaY;
				previousPos = vector;
				window.setSize(new Size(
					deltaWidth,
					deltaHeight)
				);
				break;
			case BOTTOM:
				previousPos = vector;
				window.setSize(new Size(
					window.getWidth(),
					deltaHeight)
				);
				break;
			case BOTTOM_LEFT:
				deltaWidth = window.getWidth() + (-1 * deltaX);
				deltaHeight = window.getHeight() + deltaY;
				translation = new Vector3D(
					deltaX,
					0
				);
				previousPos = vector;
				window.translateGlobal(translation);
				window.setSize(new Size(
					deltaWidth,
					deltaHeight)
				);
				break;
			case LEFT:
				translation = new Vector3D(
					deltaX,
					0
				);
				previousPos = vector;
				window.translateGlobal(translation);
				window.setSize(new Size(
					deltaWidth,
					window.getHeight())
				);
				break;
			default:
				System.err.println("Unrecognzied CursorType");
				break;
			}
		}
	}

	public void inputEnded(MTInputEvent inEvt) {
		previousPos = null;
		/*
		app.getCETMultiMiceManager().getMouseInfo(device).useCursorIcon(
			CursorType.ARROW, app, app.getCurrentScene(), app.getCurrentScene().getSceneCam()
		);
		*/
	}
}
