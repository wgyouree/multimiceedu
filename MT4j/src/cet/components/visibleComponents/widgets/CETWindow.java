package cet.components.visibleComponents.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.mt4j.MTApplication;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTWindow;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputSources.MultipleMiceInputSource;
import org.mt4j.util.MTColor;

import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

import cet.globalMultiMiceManager.CETConflictEvent;
import cet.globalMultiMiceManager.CETConflictType;
import cet.globalMultiMiceManager.ICETConflictHandler;

public class CETWindow extends MTRectangle {
	
	/** The clip. */
	private Clip clip;
	
	/** The draw inner border. */
	private boolean drawInnerBorder;
	
	/** The saved no stroke setting. */
	private boolean savedNoStrokeSetting;
	
	private static final float titleBarHeight = 40;
	
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
					if ( theDevice >= 0 && device >= 0 && theDevice != device ) {
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
						System.out.println("Resetting device");
						device = -1;
					}
					else if ( theDevice >= 0 && device >= 0 && theDevice != device ) {
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
		super(x, y, z, width, height, applet);
		
		//Create inner children clip shape
		float border = 1;
		GL gl = ((PGraphicsOpenGL)applet.g).gl;
		MTRectangle clipRect =  new MTRectangle(x+border, y+border, z, width-(2*border), height-(2*border), applet);
		clipRect.setDrawSmooth(true);
		clipRect.setNoStroke(true);
		clipRect.setBoundsBehaviour(MTRectangle.BOUNDS_ONLY_CHECK);
		this.clip = new Clip(gl, clipRect);
		this.setChildClip(this.clip);
		this.drawInnerBorder = true;
		
		//Add window background
		final MTRectangle windowBackGround = new MTRectangle(x, y, z, width, height, applet);
		windowBackGround.setFillColor(new MTColor(200,200,200,255));
		windowBackGround.setNoStroke(true);
		windowBackGround.setPickable(false);
		this.addChild(windowBackGround);
		
		// add title bar
		final MTRectangle titleBar = new MTRectangle(0, 0, width, titleBarHeight, applet);
		titleBar.setFillColor(new MTColor(100,100,100,255));
		titleBar.setNoStroke(true);
		titleBar.setPickable(false);
		this.addChild(titleBar);
		
		// remove gesture listeners
		this.removeAllGestureEventListeners(ScaleProcessor.class);
		
		// add conflict listener
		this.conflictListener = new ConflictListener(this, applet);
		this.addInputListener(conflictListener);
		
		//Draw this component and its children above 
		//everything previously drawn and avoid z-fighting //FIXME but we cant use 3D stuff in there then..
		this.setDepthBufferDisabled(true);
	}
	
	public void addConflictHandler(ICETConflictHandler handler) {
		conflictHandlers.add(handler);
	}
	
	public void removeConflictHandler(ICETConflictHandler handler) {
		conflictHandlers.remove(handler);
	}
	
	private void fireConflict(CETConflictEvent event) {
		for ( ICETConflictHandler handler : conflictHandlers ) {
			handler.handleConflict(event);
		}
	}

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.AbstractVisibleComponent#preDraw(processing.core.PGraphics)
	 */
	@Override
	public void preDraw(PGraphics graphics) {
		this.savedNoStrokeSetting = this.isNoStroke();
		super.preDraw(graphics);
	}
	

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.AbstractVisibleComponent#postDrawChildren(processing.core.PGraphics)
	 */
	@Override
	public void postDrawChildren(PGraphics g) {
		this.clip.disableClip(g);
		
		//Draw clipshape outline over all children to get an
		//antialiased border
		AbstractVisibleComponent clipShape = this.getChildClip().getClipShape();
//		if (!clipShape.isNoStroke()){
		if (this.drawInnerBorder){
			clipShape.setNoFill(true);
			clipShape.setNoStroke(false);
				clipShape.drawComponent(g);
			clipShape.setNoStroke(true);
			clipShape.setNoFill(false);
		}
		
		if (!savedNoStrokeSetting){
			boolean noFillSetting = this.isNoFill();
			this.setNoFill(true);
			this.setNoStroke(false);
			this.drawComponent(g);
			this.setNoFill(noFillSetting);
			this.setNoStroke(savedNoStrokeSetting);
		}
		
		this.setChildClip(null);
		super.postDrawChildren(g);
		this.setChildClip(clip);
	}



//	@Override
//	public void setStrokeColor(float r, float g, float b, float a) {
//		super.setStrokeColor(r, g, b, a);
//		this.clip.getClipShape().setStrokeColor(r, g, b, a);
//	}
	
	/* (non-Javadoc)
 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setStrokeColor(org.mt4j.util.MTColor)
 */
@Override
	public void setStrokeColor(MTColor strokeColor) {
		super.setStrokeColor(strokeColor);
		this.clip.getClipShape().setStrokeColor(strokeColor); //FIXME wtf? not needed!?
	}

}
