package cet.globalMultiMiceManager.listeners;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputSources.MultipleMiceInputSource;
import org.mt4j.util.math.Vector3D;

import cet.globalMultiMiceManager.CETConflictEvent;
import cet.globalMultiMiceManager.CETConflictType;
import cet.globalMultiMiceManager.ICETConflictListener;

public class DragConflictListener implements IMTInputEventListener {
	
	// the device currently using this input listener
	private int device = -1;
	
	// the conflict listener
	private ICETConflictListener listener;
	
	// the component to which this listener belongs
	private MTComponent component;
	
	// the MTApplication
	private MTApplication app;
	
	// the previous position
	private Vector3D previousPos;
	
	public DragConflictListener(ICETConflictListener listener, MTComponent component, MTApplication app) {
		this.listener = listener;
		this.component = component;
		this.app = app;
	}
	
	@Override
	public boolean processInputEvent(MTInputEvent inEvt) {
		if (inEvt instanceof AbstractCursorInputEvt) { //Most input events in MT4j are an instance of AbstractCursorInputEvt (mouse, multi-touch..)
			AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
			InputCursor cursor = cursorInputEvt.getCursor();
			IMTComponent3D target = component;
			Vector3D vector = new Vector3D(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
			int theDevice = -1;
			if( inEvt.getSource() instanceof MultipleMiceInputSource ) {
				theDevice = ((MultipleMiceInputSource) inEvt.getSource() ).getEventSourceDevice();
			}
			switch (cursorInputEvt.getId()) {
			case AbstractCursorInputEvt.INPUT_DETECTED:
				if ( theDevice >= 0 && device >= 0 ) {
					// conflict
					listener.processConflict(new CETConflictEvent(inEvt, CETConflictType.DRAG, component, app, new Integer[] { device, theDevice }) );
					System.out.println("Conflict detected");
				} else if ( theDevice >= 0 ) {
					//Put target on top -> draw on top of others
					if (target instanceof MTComponent){
						MTComponent baseComp = (MTComponent)target;
						baseComp.sendToFront();
					}
					previousPos = vector;
					device = theDevice;
				}
				System.out.println("Input detected on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());
				break;
			case AbstractCursorInputEvt.INPUT_UPDATED:
				if ( theDevice >= 0 && device >= 0 && theDevice != device ) {
					// conflict
					listener.processConflict(new CETConflictEvent(inEvt, CETConflictType.DRAG, component, app, new Integer[] { device, theDevice }) );
					System.out.println("Conflict detected");
				} else if ( theDevice >= 0 ) {
					Vector3D translation = new Vector3D(
						vector.x - previousPos.x,
						vector.y - previousPos.y
					);
					previousPos = vector;
					target.translateGlobal(translation);
					device = theDevice;
				}
				System.out.println("Input updated on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());			
				break;
			case AbstractCursorInputEvt.INPUT_ENDED:
				if ( device == theDevice ) {
					System.out.println("Resetting device");
					device = -1;
				}
				else if ( theDevice >= 0 && device >= 0 && theDevice != device ) {
					// conflict
					listener.processConflict(new CETConflictEvent(inEvt, CETConflictType.DRAG, component, app, new Integer[] { device, theDevice }) );
					System.out.println("Conflict detected");
				} else if ( theDevice >= 0 ) {
					previousPos = null;
					device = theDevice;
				}
				System.out.println("Input ended on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());
				break;
			default:
				break;
			}
		}else{
			//handle other input events
		}
		return false;
	}
}
