package cet.globalMultiMiceManager.listeners;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputSources.MultipleMiceInputSource;

import cet.globalMultiMiceManager.CETConflictEvent;
import cet.globalMultiMiceManager.CETConflictType;
import cet.globalMultiMiceManager.ICETConflictListener;

public abstract class AbstractConflictListener implements IMTInputEventListener {

	// the device currently using this input listener
	protected int device = -1;
	
	// the conflict listener
	protected ICETConflictListener listener;
	
	// the component to which this listener belongs
	protected MTComponent component;
	
	// the MTApplication
	protected MTApplication app;

	public AbstractConflictListener(ICETConflictListener listener, MTComponent component, MTApplication app) {
		this.listener = listener;
		this.component = component;
		this.app = app;
	}
	
	public abstract void inputDetected(MTInputEvent inEvt);
	
	public abstract void inputUpdated(MTInputEvent inEvt);
	
	public abstract void inputEnded(MTInputEvent inEvt);
	

	@Override
	public boolean processInputEvent(MTInputEvent inEvt) {
		if( component == null ){
			
		}
		else if ( ( component.getComponentFloorControl() != null && component.getComponentFloorControl().isEventSourcePermitted(inEvt) )
				|| ( component.getComponentFloorControl() == null && inEvt instanceof AbstractCursorInputEvt ) ) {
	//	if (inEvt instanceof AbstractCursorInputEvt) { //Most input events in MT4j are an instance of AbstractCursorInputEvt (mouse, multi-touch..)
			AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
			InputCursor cursor = cursorInputEvt.getCursor();
			IMTComponent3D target = component;
			int theDevice = -1;
			if( inEvt.getSource() instanceof MultipleMiceInputSource ) {
				theDevice = ((MultipleMiceInputSource) inEvt.getSource() ).getEventSourceDevice();
			}
			switch (cursorInputEvt.getId()) {
			case AbstractCursorInputEvt.INPUT_DETECTED:
				System.out.println("Input Detected on TitleBar");
				if ( theDevice >= 0 && device >= 0 ) {
					// conflict
					listener.processConflict(new CETConflictEvent(inEvt, CETConflictType.DRAG, component, app, new Integer[] { device, theDevice }) );
					System.out.println("Conflict detected");
				} else if ( theDevice >= 0 ) {
					inputDetected(inEvt);
					device = theDevice;
				}
				System.out.println("Input detected on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());
				System.out.println("Device = " + theDevice);
				break;
			case AbstractCursorInputEvt.INPUT_UPDATED:
				if ( theDevice >= 0 && device >= 0 && theDevice != device ) {
					// conflict
					//listener.processConflict(new CETConflictEvent(inEvt, CETConflictType.DRAG, component, app, new Integer[] { device, theDevice }) );
					//System.out.println("Conflict detected");
				} else if ( theDevice >= 0 ) {
					inputUpdated(inEvt);
					device = theDevice;
				}
				System.out.println("Input updated on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());
				System.out.println("Device = " + theDevice);
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
					inputEnded(inEvt);
					device = theDevice;
				}
				System.out.println("Input ended on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());
				System.out.println("Device = " + theDevice);
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
