package cet.globalMultiMiceManager;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTComplexPolygon;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.Icamera;
import org.mt4j.util.math.Vector3D;

import cet.globalMultiMiceManager.cursors.ArrowCursor;
import cet.globalMultiMiceManager.cursors.BottomCursor;
import cet.globalMultiMiceManager.cursors.BottomLeftCursor;
import cet.globalMultiMiceManager.cursors.BottomRightCursor;
import cet.globalMultiMiceManager.cursors.CursorType;
import cet.globalMultiMiceManager.cursors.LeftCursor;
import cet.globalMultiMiceManager.cursors.RightCursor;
import cet.globalMultiMiceManager.cursors.TopCursor;
import cet.globalMultiMiceManager.cursors.TopLeftCursor;
import cet.globalMultiMiceManager.cursors.TopRightCursor;

public class MouseInfo {
	public static final MTColor red = new MTColor(255, 0, 0, 180);
	public static final MTColor green = new MTColor(0, 255, 0, 180);
	public static final MTColor yellow = new MTColor(255, 255, 0, 180);
	public static final MTColor blue = new MTColor(0, 0, 255, 180);
	public static final MTColor pink = new MTColor(255, 192, 203, 180);
	public static final MTColor cyan = new MTColor(0, 255, 255, 180);
	public static final MTColor orange = new MTColor(255, 127, 0, 180);
	public static final MTColor brown = new MTColor(150, 75, 0, 180);
	public static final MTColor purple = new MTColor(128, 0, 128, 180);
	public static final MTColor gray = new MTColor(128, 128, 128, 180);
	public static final MTColor black = new MTColor(0, 0, 0, 180);
	public static final MTColor white = new MTColor(255, 255, 255, 180);
	public static final MTColor[] basicColorList = {red, green, yellow, blue, cyan, orange, brown, pink, purple, gray, black, white};
	
	/** The device. */
	private int device;
	
	/** Description of the device */
	private String deviceName;
	
	/** Name of the user of device */
	private String userName;
	
	/** The x. */
	private int x;
	
	/** The y. */
	private int y;
	
	/** The last x. */
	private int lastX;
	
	/** The last y. */
	private int lastY;
	
	/** The is button pressed. */
	private boolean isButtonPressed = false;
	
	/** The cursor icon */
	private AbstractShape cursorIcon;
	
	/** The rank of the device */
	private CETMouseRank rank = CETMouseRank.STUDENT;
		
	public MouseInfo( int d, String name ){
		device = d;
		deviceName = name;
		x = 0;
		y = 0;
		lastX = 0;
		lastY = 0;
	}
	
	public void useCursorIcon(CursorType type, MTApplication mtApp, Iscene currentScene, Icamera defaultCenterCam) {
		if (mtApp != null){
			float currentEllipseWidth = 6;
			if (currentScene != null){
				Vector3D v = new Vector3D(currentEllipseWidth,0,0);
				v.transformDirectionVector(currentScene.getCanvas().getGlobalInverseMatrix());
				currentEllipseWidth = v.length();
			}
			
			if ( cursorIcon != null ) {
				cursorIcon = null;
			}
			switch ( type ) {
			case ARROW:
				cursorIcon = new ArrowCursor(mtApp, new Vector3D(x,y));
				break;
			case TOP_LEFT:
				cursorIcon = new TopLeftCursor(mtApp, new Vector3D(x,y));
				break;
			case TOP:
				cursorIcon = new TopCursor(mtApp, new Vector3D(x,y));
				break;
			case TOP_RIGHT:
				cursorIcon = new TopRightCursor(mtApp, new Vector3D(x,y));
				break;
			case RIGHT:
				cursorIcon = new RightCursor(mtApp, new Vector3D(x,y));
				break;
			case BOTTOM_RIGHT:
				cursorIcon = new BottomRightCursor(mtApp, new Vector3D(x,y));
				break;
			case BOTTOM:
				cursorIcon = new BottomCursor(mtApp, new Vector3D(x,y));
				break;
			case BOTTOM_LEFT:
				cursorIcon = new BottomLeftCursor(mtApp, new Vector3D(x,y));
				break;
			case LEFT:
				cursorIcon = new LeftCursor(mtApp, new Vector3D(x,y));
				break;
			default:
				System.err.println("Unrecognized CursorType");
				break;
			}
			cursorIcon.setPickable(false);
			cursorIcon.attachCamera(defaultCenterCam);
			cursorIcon.setFillColor( basicColorList[ device % basicColorList.length ] );
			cursorIcon.setDrawSmooth(true);
			cursorIcon.setStrokeWeight(0);
			cursorIcon.setStrokeColor( new MTColor(255, 255, 255, 180) );
			cursorIcon.setDepthBufferDisabled(true);
		}
	}
	
	public void useDefaultCursorIcon(MTApplication mtApp, Iscene currentScene, Icamera defaultCenterCam){
		useCursorIcon(CursorType.ARROW, mtApp, currentScene, defaultCenterCam);
	}
	
	public void updateCursorIconOnSceneChange(Iscene currentScene){
		//float currentEllipseWidth = 6;
		//Vector3D v = new Vector3D(currentEllipseWidth,0,0);
		//v.transformDirectionVector(currentScene.getCanvas().getGlobalInverseMatrix());
		//float newEllipseWidth = currentEllipseWidth = v.length();
		//cursorIcon.setWidthXYGlobal(newEllipseWidth*2);
	}
	
	public AbstractShape getCursorIcon(){
		return cursorIcon;
	}
	
	public void setCursorIcon(MTPolygon icon){
		cursorIcon = icon;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setX( int xx ){
		x = xx;
	}
	
	public void setY( int yy ){
		y = yy;
	}
	
	public void setPosition( int xx, int yy ){
		x = xx;
		y = yy;
	}
	
	public int getLastX(){
		return lastX;
	}
	
	public int getLastY(){
		return lastY;
	}
	
	public void setLastX( int lx ){
		lastX = lx;
	}
	
	public void setLastY( int ly ){
		lastY = ly;
	}
	
	public void setLastPosition( int lx, int ly ){
		lastX = lx;
		lastY = ly;
	}
	
	public boolean isButtonPressed(){
		return isButtonPressed;
	}
	
	public void setButtonPressed( boolean is ){
		isButtonPressed = is;
	}
	
	public int getDeviceNum() {
		return device;
	}
	
	public String getDeviceName(){
		return deviceName;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public void setUserName(String name){
		userName = name;
	}
	
	public MTColor getCursorIconColor(){
		return cursorIcon.getFillColor();
	}
	
	public void setCursorIconColor(MTColor color){
		cursorIcon.setFillColor( color );
	}
	
	public void setMouseRank(CETMouseRank rank) {
		this.rank = rank;
	}
	
	public CETMouseRank getMouseRank() {
		return this.rank;
	}
}
