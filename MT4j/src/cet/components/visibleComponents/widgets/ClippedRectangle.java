package cet.components.visibleComponents.widgets;

import javax.media.opengl.GL;

import org.mt4j.MTApplication;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.util.MTColor;

import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

public class ClippedRectangle extends MTRectangle {

	/** The clip. */
	private Clip clip;
	
	/** The draw inner border. */
	private boolean drawInnerBorder;
	
	/** The saved no stroke setting. */
	private boolean savedNoStrokeSetting;
	
	public ClippedRectangle(float x, float y, float z, float width, float height, MTApplication applet) {
		super(x, y, z, width, height, applet);
		
		GL gl = ((PGraphicsOpenGL)applet.g).gl;
		MTRectangle clipRect =  new MTRectangle(x, y, z, width, height, applet);
		clipRect.setDrawSmooth(true);
		clipRect.setNoStroke(true);
		clipRect.setBoundsBehaviour(MTRectangle.BOUNDS_ONLY_CHECK);
		this.clip = new Clip(gl, clipRect);
		this.setChildClip(this.clip);
		this.drawInnerBorder = true;
		
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

	/* (non-Javadoc)
	 * @see org.mt4j.components.visibleComponents.AbstractVisibleComponent#preDraw(processing.core.PGraphics)
	 */
	@Override
	public void preDraw(PGraphics graphics) {
		this.savedNoStrokeSetting = this.isNoStroke();
		super.preDraw(graphics);
	}
}
