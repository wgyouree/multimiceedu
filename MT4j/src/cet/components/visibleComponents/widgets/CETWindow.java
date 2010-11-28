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
import org.mt4j.util.math.Vertex;

import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

import cet.componentMultiMiceManager.CETcomponentMultiMiceControl;
import cet.globalMultiMiceManager.CETConflictEvent;
import cet.globalMultiMiceManager.CETConflictType;
import cet.globalMultiMiceManager.ICETConflictHandler;
import cet.globalMultiMiceManager.ICETConflictListener;
import cet.globalMultiMiceManager.listeners.DragConflictListener;

public class CETWindow extends MTRectangle implements ICETConflictListener {
	
	private MTRectangle contentArea;
	
	private static final float titleBarHeight = 30;
	private static final float titleMarginLeft = 5;
	private static final float titleMarginTop = 5;
	private static final int titleFontSize = 12;
	private static final float resizeMargin = 15;
	private static final float resizeWidth = 1;
	
	private String title = "Window";
	private MTTextArea titleTextArea;
	
	private List<ICETConflictHandler> conflictHandlers = new ArrayList<ICETConflictHandler>();
	
	private MTApplication app;
	
	private CETcomponentMultiMiceControl floorControl;
	
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
		
		// Register Floor Controls for this component
		this.floorControl = new CETcomponentMultiMiceControl(app.getCETMultiMiceManager());
		
		//Add window background
		float border = resizeWidth;
		this.contentArea = new ClippedRectangle(border, titleBarHeight+border, z, width-(2*border), height-titleBarHeight-(2*border), applet);
		this.contentArea.setFillColor(new MTColor(200,200,200,255));
		this.contentArea.setNoStroke(true);
		this.contentArea.setPickable(false);
		super.addChild(this.contentArea);
		
		// add title bar
		final MTRectangle titleBar = new MTRectangle(0+border, 0+border, width-(2*border), titleBarHeight, applet);
		titleBar.setFillColor(new MTColor(100,100,100,255));
		titleBar.setNoStroke(true);
		titleBar.setPickable(false);
		super.addChild(titleBar);
		
		// draw resize border
		MTColor gray = new MTColor(139, 137, 137);
		topLeft = new MTPolygon(new Vertex[] {
			new Vertex( x, y+resizeMargin ),
			new Vertex( x, y ),
			new Vertex( x+resizeMargin, y )
		}, app);
		topLeft.setNoFill(true);
		topLeft.setStrokeColor(gray);
		topLeft.setStrokeWeight(border);
		super.addChild(topLeft);
		
		top = new MTPolygon(new Vertex[] {
			new Vertex( x+resizeMargin, y ),
			new Vertex( x+width-resizeMargin, y )
		}, app);
		top.setNoFill(true);
		top.setStrokeWeight(border);
		top.setStrokeColor(gray);
		super.addChild(top);
		
		topRight = new MTPolygon(new Vertex[] {
			new Vertex( x+width-resizeMargin, y ),
			new Vertex( x+width, y ),
			new Vertex( x+width, y+resizeMargin )
		}, app);
		topRight.setNoFill(true);
		topRight.setStrokeColor(gray);
		topRight.setStrokeWeight(border);
		super.addChild(topRight);
		
		right = new MTPolygon(new Vertex[] {
			new Vertex( x+width, y+resizeMargin ),
			new Vertex( x+width, y+height-resizeMargin )
		}, app);
		right.setNoFill(true);
		right.setStrokeWeight(border);
		right.setStrokeColor(gray);
		super.addChild(right);
		
		bottomRight = new MTPolygon(new Vertex[] {
			new Vertex( x+width, y+height-resizeMargin ),
			new Vertex( x+width, y+height-border ),
			new Vertex( x+width-resizeMargin, y+height-border )
		}, app);
		bottomRight.setNoFill(true);
		bottomRight.setStrokeColor(gray);
		bottomRight.setStrokeWeight(border);
		super.addChild(bottomRight);
		
		bottom = new MTPolygon(new Vertex[] {
			new Vertex( x+resizeMargin, y+height-border ),
			new Vertex( x+width-resizeMargin, y+height-border )
		}, app);
		bottom.setNoFill(true);
		bottom.setStrokeWeight(border);
		bottom.setStrokeColor(gray);
		super.addChild(bottom);
		
		bottomLeft = new MTPolygon(new Vertex[] {
			new Vertex( x+resizeMargin, y+height-border ),
			new Vertex( x, y+height-border ),
			new Vertex( x, y+height-border-resizeMargin )
		}, app);
		bottomLeft.setNoFill(true);
		bottomLeft.setStrokeColor(gray);
		bottomLeft.setStrokeWeight(border);
		super.addChild(bottomLeft);
		
		left = new MTPolygon(new Vertex[] {
			new Vertex( x, y+resizeMargin ),
			new Vertex( x, y+height-resizeMargin )
		}, app);
		left.setNoFill(true);
		left.setStrokeWeight(border);
		left.setStrokeColor(gray);
		super.addChild(left);
		
		// set the title
		this.setTitle(this.title);
		
		// remove default gesture listeners
		this.removeAllGestureEventListeners();
		
		//Draw this component and its children above 
		//everything previously drawn and avoid z-fighting //FIXME but we cant use 3D stuff in there then..
		this.setDepthBufferDisabled(true);
	}
	
	public void addChild(MTComponent child) {
		this.contentArea.addChild(child);
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
			super.addChild(this.titleTextArea);
		}
		
		this.titleTextArea.setText(this.title);
		
	}
	
	public String getTitle() {
		return this.title;
	}
}
