package cet.components.visibleComponents.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IclickableButton;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTClipRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputSources.MultipleMiceInputSource;
import org.mt4j.util.MTColor;

import cet.componentMultiMiceManager.CETcomponentMultiMiceControl;
import cet.componentMultiMiceManager.CETcomponentMultiMiceControlUI;
import cet.globalMultiMiceManager.CETMultipleMiceManager;

import processing.core.PApplet;

public class CETButton extends MTRectangle implements ActionListener{
	private PApplet papplet;
	private String label;
	private float left, top, width, height;
	
	private MTColor notpressedFill = new MTColor(200, 200, 150, 100);
	private MTColor notpressedStroke = new MTColor(100, 100, 100, 100);
	private MTColor pressedFill = new MTColor(150, 150, 100, 100);
	private MTColor pressedStroke = new MTColor(50, 50, 50, 100);
	
	private MTTextArea labelBox;
	
	private ArrayList<ActionListener> registeredActionListeners;
	
	private CETMultipleMiceManager globalMiceManager;
	private CETcomponentMultiMiceControl floorControl;
	private CETcomponentMultiMiceControlUI floorControlUI;
	
	private WeakHashMap<Integer, MTRectangle> micePressed;
	private int miceTotalNum;
	private int micePressedNum;
	
	public CETButton(String l, float x, float y, float z, float w, float h, PApplet p) {
		super(x, y, z, w, h, p);
		label = l;
		papplet = p;
		left = x;
		top = y;
		width = w;
		height = h;
		
		this.registeredActionListeners = new ArrayList<ActionListener>();
		
		this.setEnabled(true);
		this.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
		
		//Draw this component and its children above 
		//everything previously drawn and avoid z-fighting
		this.setDepthBufferDisabled(true);
		
		this.setNoFill(false);
		this.setNoFill(false);
		this.setStrokeWeight(1);
		
		labelBox = new MTTextArea( papplet );
		labelBox.removeAllGestureEventListeners();
		labelBox.setPickable(false);
		labelBox.setNoFill(true);
		labelBox.setNoStroke(true);
		labelBox.setText(label);
		this.addChild(labelBox);
		labelBox.setPositionRelativeToParent( this.getCenterPointLocal() );
		
		if( ((MTApplication)papplet).getCETMultiMiceManager() != null ) {
			this.globalMiceManager = ((MTApplication)papplet).getCETMultiMiceManager();
			this.floorControl = new CETcomponentMultiMiceControl(((MTApplication)papplet).getCETMultiMiceManager());
			this.floorControl.addEventListener( this );
			this.floorControl.setCollaborative(true);
			this.floorControlUI = this.floorControl.createUI( (int)(x+20), (int)(y+20), papplet);
			this.addChild( floorControlUI );
			floorControlUI.sendToFront();
			miceTotalNum = this.floorControl.getStudentDeviceList().size();
			micePressedNum = 0;
		}
		
		micePressed = new WeakHashMap<Integer, MTRectangle>();
		
		removeAllGestureEventListeners();
		
		drawButton(false);
		
		this.addInputListener( new ButtonEventListener());
	}
	
	public CETButton(String l, float x, float y, float w, float h, PApplet p) {
		this(l, x, y, 0, w, h, p);
	}
	
	public CETButton(float x, float y, float w, float h, PApplet p) {
		this( new String("Button"), x, y, w, h, p);
	}

	private void drawButton(boolean isPressed){
		if( isPressed ){
			this.setFillColor( pressedFill );
			this.setStrokeColor( pressedStroke );
		}
		else {
			this.setFillColor( notpressedFill );
			this.setStrokeColor( notpressedStroke );
		}
	}	
	
	private MTRectangle createBox(int device) {
		MTRectangle box = null;
		if( miceTotalNum > 0 ) {
			float boxWidth = this.width / miceTotalNum;
			box = new MTRectangle(top + boxWidth*micePressedNum, top, boxWidth, this.height, papplet);
			MTColor boxColor = globalMiceManager.getMouseInfo(device).getCursorIconColor();
			box.setFillColor( new MTColor( boxColor.getR(), boxColor.getG(), boxColor.getB(), 150) );
			box.setNoStroke(true);
			box.setPickable(false);
		}
		return box;
	}

	

	/**
	 * Called when floor control options change
	 */
	public void actionPerformed(ActionEvent event) {
		if( event.getActionCommand().equals("Collaborative mode changed.")) {
			if( !floorControl.isCollaborative() ) {
				for( MTRectangle box : micePressed.values() )
					this.removeChild( box );
				micePressed.clear();
			}
		}
		else {
			if( floorControl.isCollaborative() ) {
				for( MTRectangle box : micePressed.values() )
					this.removeChild( box );
				Set<Integer> micePressedSet = micePressed.keySet();
				micePressed.clear();			
				
				miceTotalNum = floorControl.getStudentDeviceList().size();
				micePressedNum = 0;
				if( miceTotalNum > 0 ) {
					for( int device : micePressedSet ) {
						if( floorControl.isDevicePermitted(device) ) {
							MTRectangle box = createBox(device);
							this.addChild(box);		
							micePressed.put(device, box);
							micePressedNum++;
						}
					}		
				}
			}
		}
	}
	
	// Post event to registered listeners upon button click
	public void addEventListener( ActionListener l ) {
		registeredActionListeners.add(l);
	}
	
	public void removeEventListener( ActionListener l ) {
		registeredActionListeners.remove(l);
	}
	
	public ArrayList<ActionListener> getAllEventListeners() {
		return registeredActionListeners;
	}
	
	private void fireAction(String command) {
		System.out.println(command);
		for( ActionListener l : registeredActionListeners )
			l.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, command) );
	}
	
	/**
	 * Handles button clicks
	 */
	class ButtonEventListener implements IMTInputEventListener {
		public boolean processInputEvent( MTInputEvent event ){
			if( floorControl.isEventSourcePermitted(event) )	{
				AbstractCursorInputEvt cursorEvt = (AbstractCursorInputEvt)event;
				switch( cursorEvt.getId() ) {
					case AbstractCursorInputEvt.INPUT_DETECTED:
						drawButton(true);
						break;
					case AbstractCursorInputEvt.INPUT_ENDED:
						drawButton(false);
						if( floorControl.isCollaborative() 
							&& ((MultipleMiceInputSource)event.getSource()).getEventSourceDevice() != globalMiceManager.getInstructorDevice() ) {
							int device = ((MultipleMiceInputSource)event.getSource()).getEventSourceDevice();
							MTRectangle box = createBox( device );
							addChild(box);
							micePressed.put(device, box);
							micePressedNum++;
							if( micePressedNum == miceTotalNum ) {
								fireAction("Button clicked");
								for( MTRectangle b : micePressed.values() )
									removeChild( b );
								micePressed.clear();			
								micePressedNum = 0;
							}
						}
						else
							fireAction("Button clicked");
						break;
					default:
						break;
				}
			}
			return false;
		}
	}
}
