package cet.componentMultiMiceManager;

import java.util.WeakHashMap;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.widgets.MTOverlayContainer;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputSources.MultipleMiceInputSource;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import cet.globalMultiMiceManager.CETMultipleMiceManager;
import cet.globalMultiMiceManager.MouseInfo;

import processing.core.PApplet;

public class CETcomponentMultiMiceControlUI extends MTComponent implements IMTInputEventListener{
	private PApplet papplet;
	private CETMultipleMiceManager globalMiceManager;
	private CETcomponentMultiMiceControl compMiceControl;
	private int iconRadius = 5;
	private WeakHashMap<Integer, MTEllipse> iconDetail = new WeakHashMap<Integer, MTEllipse>();
	private MTEllipse iconCollab;
	private int ctrlRadius = 50;
	private int ctrlPieRadius = ctrlRadius - 5;
	private int ctrlCollabRadius = 20;
	private WeakHashMap<MTEllipse, Integer> ctrlDetail = new WeakHashMap<MTEllipse, Integer>();
	private MTEllipse floorControl;
	private final static float dimFactor = 0.4f;
	private boolean isPopUp = false;
	
	/**
	 * Center of the icon
	 */
	private int cx, cy;
	
	public CETcomponentMultiMiceControlUI( int x, int y, CETcomponentMultiMiceControl c, PApplet p){
		super(p);
		papplet = p;
		globalMiceManager = ((MTApplication)p).getCETMultiMiceManager();
		compMiceControl = c;
		cx = x;
		cy = y;
		
		iconCollab = new MTEllipse( papplet, new Vector3D(cx, cy), iconRadius*2-6, iconRadius*2-6 );
		iconCollab.setFillColor( new MTColor(0, 0, 0) );
		iconCollab.setNoStroke(true);

		update();
	}
	
	public boolean processInputEvent( MTInputEvent event ){
		if( event instanceof AbstractCursorInputEvt
				&& event.getSource() instanceof MultipleMiceInputSource 
				&& ((MultipleMiceInputSource)event.getSource()).getEventSourceDevice() == globalMiceManager.getInstructorDevice() )	{
			AbstractCursorInputEvt cursorEvt = (AbstractCursorInputEvt)event;
			if( cursorEvt.getId() == AbstractCursorInputEvt.INPUT_ENDED )
				if( isPopUp )
					hideControl();
				else
					popUpControl( cursorEvt.getPosition() );
		}
		return true;
	}
	
	public void update(){
		for( MTEllipse ellipse : iconDetail.values() )
			this.removeChild( ellipse );
		if( globalMiceManager.getMiceNumber() > 1 ) {
			float eachDegree = (float)Math.toRadians( 360/ (globalMiceManager.getMiceNumber() - 1) );
			float curDegree = 0;

			for( MouseInfo mouse : globalMiceManager.getDeviceToMouseInfo().values()) {
				if( mouse.getDeviceNum() !=  globalMiceManager.getInstructorDevice() ){
					MTEllipse curEllipse = new MTEllipse( papplet, new Vector3D(cx, cy), iconRadius*2, iconRadius*2, curDegree, eachDegree, 10, MTEllipse.pie);
					MTColor color = mouse.getCursorIcon().getFillColor();
					if( compMiceControl.isDeviceAccessible(mouse.getDeviceNum()) ) {
						curEllipse.setFillColor( color );
						curEllipse.setStrokeColor( color );
					}
					else {
						curEllipse.setFillColor( new MTColor((int)(color.getR()*dimFactor), (int)(color.getG()*dimFactor), (int)(color.getB()*dimFactor), color.getAlpha()) );
						curEllipse.setStrokeColor( new MTColor((int)(color.getR()*dimFactor), (int)(color.getG()*dimFactor), (int)(color.getB()*dimFactor), color.getAlpha()) );
					}
					curEllipse.setStrokeWeight(1);
					curEllipse.removeAllGestureEventListeners();
					curEllipse.addInputListener(this);
					iconDetail.put( mouse.getDeviceNum(), curEllipse );
					this.addChild(curEllipse);
					curDegree += eachDegree;
				}
			}
			this.removeChild( iconCollab );
			if( !compMiceControl.isCollaborative() ) { 
				this.addChild( iconCollab );
			}
		}
	}
	
	public void popUpControl( Vector3D position ){
		float x = position.getX();
		float y = position.getY();
		if( x - ctrlRadius < 0 )
			x = ctrlRadius;
		if( x + ctrlRadius > papplet.width )
			x = papplet.width - ctrlRadius;
		if( y - ctrlRadius < 0 )
			y = ctrlRadius;
		if( y + ctrlRadius > papplet.height )
			y = papplet.height - ctrlRadius;
		
		if( globalMiceManager.getMiceNumber() > 1 ) {
			float eachDegree = (float)Math.toRadians( 360/ (globalMiceManager.getMiceNumber() - 1) );
			float curDegree = 0;
			
			floorControl = new MTEllipse( papplet, new Vector3D(x, y), ctrlRadius*2, ctrlRadius*2 );
			floorControl.setFillColor( new MTColor(255, 255, 255, 100) );
			floorControl.setNoStroke(true);
			floorControl.addInputListener( new IMTInputEventListener(){
				public boolean processInputEvent(MTInputEvent inEvt) {
					hideControl();
					return true;
				}				
			}); 
			this.addChild(floorControl);

			for( MouseInfo mouse : globalMiceManager.getDeviceToMouseInfo().values()) {
				if( mouse.getDeviceNum() !=  globalMiceManager.getInstructorDevice() ){
					final MTEllipse curEllipse = new MTEllipse( papplet, new Vector3D(x, y), ctrlPieRadius*2, ctrlPieRadius*2, curDegree, eachDegree, 45, MTEllipse.pie);
					final MTColor color = mouse.getCursorIcon().getFillColor();
					if( compMiceControl.isDeviceAccessible(mouse.getDeviceNum()) ) {
						curEllipse.setFillColor( color );
						curEllipse.setStrokeColor( color );
					}
					else {
						curEllipse.setFillColor( new MTColor((int)(color.getR()*dimFactor), (int)(color.getG()*dimFactor), (int)(color.getB()*dimFactor), color.getAlpha()) );
						curEllipse.setStrokeColor( new MTColor((int)(color.getR()*dimFactor), (int)(color.getG()*dimFactor), (int)(color.getB()*dimFactor), color.getAlpha()) );
					}
					curEllipse.setStrokeWeight(1);
					curEllipse.removeAllGestureEventListeners();
					curEllipse.addInputListener( new IMTInputEventListener() {
						public boolean processInputEvent(MTInputEvent event) {
							if( event instanceof AbstractCursorInputEvt
									&& event.getSource() instanceof MultipleMiceInputSource 
									&& ((MultipleMiceInputSource)event.getSource()).getEventSourceDevice() == globalMiceManager.getInstructorDevice()
									&& ((AbstractCursorInputEvt)event).getId() == AbstractCursorInputEvt.INPUT_ENDED ){
								int device = ctrlDetail.get(curEllipse);
								if( compMiceControl.isDeviceAccessible(device) ) {
									compMiceControl.removeStudentDevice(device);
									curEllipse.setFillColor( new MTColor((int)(color.getR()*dimFactor), (int)(color.getG()*dimFactor), (int)(color.getB()*dimFactor), color.getAlpha()) );
									curEllipse.setStrokeColor( new MTColor((int)(color.getR()*dimFactor), (int)(color.getG()*dimFactor), (int)(color.getB()*dimFactor), color.getAlpha()) );
								}
								else {
									compMiceControl.addStudentDevice(device);
									curEllipse.setFillColor( color );
									curEllipse.setStrokeColor( color );
								}
								update();
								return true;
							}
							return false;
						}
					});
					ctrlDetail.put( curEllipse, mouse.getDeviceNum() );
					floorControl.addChild(curEllipse);
					curDegree += eachDegree;
				}
			}
			
			final MTEllipse collab = new MTEllipse(  papplet, new Vector3D(x, y), ctrlCollabRadius*2, ctrlCollabRadius*2 );
			final MTTextArea collabLabel = new MTTextArea( papplet );
			collab.removeAllGestureEventListeners();
			collabLabel.setPickable(false);
			collab.addChild( collabLabel );
			if( compMiceControl.isCollaborative() ){
				collab.setNoFill(true);
				collab.setNoStroke(true);
				collabLabel.setText("Collaborative");
			}
			else{
				collab.setNoFill(false);
				collab.setFillColor( new MTColor( 200, 200, 200, 255 ) );
				collab.setNoStroke(true);
				collabLabel.setText("Not Collaborative");
			}
			collabLabel.setNoFill(true);
			collabLabel.setNoStroke(true);
			collabLabel.setPositionRelativeToParent( new Vector3D(x, y) );
			collab.addInputListener( new IMTInputEventListener(){
				public boolean processInputEvent(MTInputEvent event) {
					if( event instanceof AbstractCursorInputEvt
						&& event.getSource() instanceof MultipleMiceInputSource 
						&& ((MultipleMiceInputSource)event.getSource()).getEventSourceDevice() == globalMiceManager.getInstructorDevice()
						&& ((AbstractCursorInputEvt)event).getId() == AbstractCursorInputEvt.INPUT_ENDED ){
						if( compMiceControl.isCollaborative() ){
							compMiceControl.setCollaborative(false);
							collab.setNoFill(false);
							collab.setFillColor( new MTColor( 200, 200, 200, 255 ) );
							collab.setNoStroke(true);
							collabLabel.setText("Not Collaborative");
						}
						else{
							compMiceControl.setCollaborative(true);
							collab.setNoFill(true);
							collab.setNoStroke(true);
							collabLabel.setText("Collaborative");
						}
						update();
						return true;
					}
					return false;
				}			
			});
			
			floorControl.addChild( collab );
			collab.sendToFront();
			
			isPopUp = true;
		}	
	}
	
	public void hideControl(){
		this.removeChild(floorControl);
		isPopUp = false;
	}
}
