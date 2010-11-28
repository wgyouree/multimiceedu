package cet.globalMultiMiceManager.cursors;

import org.mt4j.components.visibleComponents.shapes.MTComplexPolygon;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

public class BottomLeftCursor extends MTComplexPolygon {

	public BottomLeftCursor(PApplet app, Vector3D pos) {
		super(app, new Vertex[] {
			new Vertex(pos.x-10, pos.y+10),
			new Vertex(pos.x+10, pos.y-10)
		});
	}
}
