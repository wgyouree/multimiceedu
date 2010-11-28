package cet.globalMultiMiceManager.cursors;

import org.mt4j.components.visibleComponents.shapes.MTComplexPolygon;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

public class RightCursor extends MTComplexPolygon {

	public RightCursor(PApplet app, Vector3D pos) {
		super(app, new Vertex[] {
			new Vertex(pos.x, pos.y),
			new Vertex(pos.x+20, pos.y+20)
		});
	}
}
