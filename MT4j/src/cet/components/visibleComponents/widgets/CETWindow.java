package cet.components.visibleComponents.widgets;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.MTApplication;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.widgets.MTWindow;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputSources.MultipleMiceInputSource;

import cet.globalMultiMiceManager.CETConflictEvent;
import cet.globalMultiMiceManager.CETConflictType;
import cet.globalMultiMiceManager.ICETConflictHandler;

public class CETWindow extends MTWindow {
	
	private static final float defaultArcWidth = 1;
	private static final float defaultArcHeight = 1;
	
	private List<ICETConflictHandler> conflictHandlers = new ArrayList<ICETConflictHandler>();
	
	private ConflictListener conflictListener;
	
	private class ConflictListener implements IMTInputEventListener {
		
		// the device currently using this input listener
		private int device = -1;
		
		// the window to which this listener belongs
		private CETWindow window;
		
		// the MTApplication
		private MTApplication app;
		
		public ConflictListener(CETWindow window, MTApplication app) {
			this.window = window;
			this.app = app;
		}
		
		@Override
		public boolean processInputEvent(MTInputEvent inEvt) {
			if (inEvt instanceof AbstractCursorInputEvt) { //Most input events in MT4j are an instance of AbstractCursorInputEvt (mouse, multi-touch..)
				AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
				InputCursor cursor = cursorInputEvt.getCursor();
				IMTComponent3D target = cursorInputEvt.getTargetComponent();
				int theDevice = -1;
				if( inEvt.getSource() instanceof MultipleMiceInputSource ) {
					theDevice = ((MultipleMiceInputSource) inEvt.getSource() ).getEventSourceDevice();
				}
				switch (cursorInputEvt.getId()) {
				case AbstractCursorInputEvt.INPUT_DETECTED:
					if ( theDevice >= 0 && device >= 0 ) {
						// conflict
						window.fireConflict(new CETConflictEvent(inEvt, CETConflictType.MOVE, app, new Integer[] { device, theDevice }) );
						System.out.println("Conflict detected");
					} else if ( theDevice >= 0 ) {
						device = theDevice;
					}
					System.out.println("Input detected on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());
					break;
				case AbstractCursorInputEvt.INPUT_UPDATED:
					if ( theDevice >= 0 && device >= 0 ) {
						// conflict
						window.fireConflict(new CETConflictEvent(inEvt, CETConflictType.MOVE, app, new Integer[] { device, theDevice }) );
						System.out.println("Conflict detected");
					} else if ( theDevice >= 0 ) {
						device = theDevice;
					}
					System.out.println("Input updated on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());			
					break;
				case AbstractCursorInputEvt.INPUT_ENDED:
					if ( device == theDevice ) {
						device = -1;
					}
					else if ( theDevice >= 0 && device >= 0 ) {
						// conflict
						window.fireConflict(new CETConflictEvent(inEvt, CETConflictType.MOVE, app, new Integer[] { device, theDevice }) );
						System.out.println("Conflict detected");
					} else if ( theDevice >= 0 ) {
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

	public CETWindow(float x, float y, float z, float width, float height, MTApplication applet) {
		super(x, y, z, width, height, defaultArcWidth, defaultArcHeight, applet);
		this.conflictListener = new ConflictListener(this, applet);
		this.addInputListener(conflictListener);
	}
	
	public void addConflictHandler(ICETConflictHandler handler) {
		conflictHandlers.add(handler);
	}
	
	public void removeConflictHandler(ICETConflictHandler handler) {
		conflictHandlers.remove(handler);
	}
	
	public void fireConflict(CETConflictEvent event) {
		for ( ICETConflictHandler handler : conflictHandlers ) {
			handler.handleConflict(event);
		}
	}
}
