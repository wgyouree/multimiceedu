package cet.components.visibleComponents.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputSources.MultipleMiceInputSource;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

import cet.globalMultiMiceManager.CETConflictEvent;
import cet.globalMultiMiceManager.CETConflictType;
import cet.globalMultiMiceManager.ICETConflictHandler;
import cet.globalMultiMiceManager.ICETConflictListener;
import cet.globalMultiMiceManager.listeners.DragConflictListener;

public class CETWindow extends MTRectangle implements ICETConflictListener {
	
	/** The clip. */
	private Clip clip;
	
	/** The draw inner border. */
	private boolean drawInnerBorder;
	
	/** The saved no stroke setting. */
	private boolean savedNoStrokeSetting;
	
	private static final float titleBarHeight = 30;
	private static final float titleMarginLeft = 5;
	private static final float titleMarginTop = 5;
	private static final int titleFontSize = 12;
	
	private String title = "Window";
	private MTTextArea titleTextArea;
	
	private List<ICETConflictHandler> conflictHandlers = new ArrayList<ICETConflictHandler>();
	
	private MTApplication app;
	
	private float width;
	private float height;
	
	// border, for resizing
	private MTPolygon topLeft;
	private MTPolygon top;
	private MTPolygon topRight;
	private MTPolygon right;
	private MTPolygon bottomRight;
	private MTPolygon bottom;
	private MTPolygon bottomLeft;
	private MTPolygon left;
	
	// conflict listeners
	private DragConflictListener dragConflictListener;
	

	public CETWindow(String title, float x, float y, float z, float width, float height, MTApplication applet) {
		this(x, y, z, width, height, applet);
		this.setTitle(title);
	}
	
	public CETWindow(float x, float y, float z, float width, float height, MTApplication applet) {
		super(x, y, z, width, height, applet);
		this.app = applet;
		this.width = width;
		this.height = height;
		
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
		
		// set the title
		this.setTitle(this.title);
		
		// remove default gesture listeners
		this.removeAllGestureEventListeners();
		
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
	
	public void processConflict(CETConflictEvent event) {
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
	
	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.shapes.AbstractShape#setStrokeColor(org.mt4j.util.MTColor)
	 */
	@Override
	public void setStrokeColor(MTColor strokeColor) {
		super.setStrokeColor(strokeColor);
		this.clip.getClipShape().setStrokeColor(strokeColor); //FIXME wtf? not needed!?
	}

	public void setTitle(String title) {
		this.title = title;
		if ( this.titleTextArea == null ) {
			MTColor white = new MTColor(0,0,0, 200);
			IFont fontArial = FontManager.getInstance().createFont(this.app, "arial.ttf", 
					titleFontSize, 	//Font size
					white,  //Font fill color
					white);	//Font outline color
			//Create a textfield
			this.titleTextArea = new MTTextArea(titleMarginLeft, titleMarginTop, this.width, titleBarHeight - (2*titleMarginTop), fontArial, this.app);
			this.titleTextArea.setNoStroke(true);
			this.titleTextArea.setNoFill(true);
			this.titleTextArea.removeAllGestureEventListeners();
			
			this.dragConflictListener = new DragConflictListener(this, this, app);
			this.titleTextArea.addInputListener(dragConflictListener);
			this.addChild(this.titleTextArea);
		}
		
		this.titleTextArea.setText(this.title);
		
	}
	
	public String getTitle() {
		return this.title;
	}
}
