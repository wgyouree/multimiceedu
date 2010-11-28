package cet.globalMultiMiceManager.cursors;

import org.mt4j.components.visibleComponents.shapes.MTComplexPolygon;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

public class ArrowCursor extends MTComplexPolygon {

	public ArrowCursor(PApplet app, Vector3D pos) {
		/*
		super(app, new Vertex[] {
			new Vertex(pos.x,pos.y),
			new Vertex(pos.x+10,pos.y),
			new Vertex(pos.x+7,pos.y+3),
			new Vertex(pos.x+16,pos.y+6),
			new Vertex(pos.x+15,pos.y+11),
			new Vertex(pos.x+6,pos.y+8),
			new Vertex(pos.x+3,pos.y+5),
			new Vertex(pos.x,pos.y)
		});
		*/
		super(app, new Vertex[] {
			new Vertex(pos.x, pos.y+5),
			new Vertex(pos.x+10, pos.y),
			new Vertex(pos.x+10, pos.y+3),
			new Vertex(pos.x+20, pos.y+3),
			new Vertex(pos.x+20, pos.y+7),
			new Vertex(pos.x+10, pos.y+7),
			new Vertex(pos.x+10, pos.y+10),
			new Vertex(pos.x, pos.y+5)
		});
		//double angle = 0.785398163 ;
		//float angle = 45;
		//this.rotateYGlobal(new Vector3D(pos.x, pos.y), angle);
		//this.rotateX(new Vector3D(pos.x, pos.y), angle);
		/*
		this.transform(new Matrix(new float[]{
			(new Double(Math.cos(angle))).floatValue(), (new Double(-1*Math.sin(angle))).floatValue(), 0,
			(new Double(Math.sin(angle))).floatValue(), (new Double(Math.cos(angle))).floatValue(),    0,
			0, 				 							0,					 						   1
		}));
		*/
	}
}
