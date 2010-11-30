package cet.globalMultiMiceManager.occlusion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;

import cet.components.visibleComponents.widgets.CETWindow;

public class RedBoxOcclusionHandler implements ICETOcclusionHandler {

	private List<MTRectangle> overlapRects = 
		new ArrayList<MTRectangle>();
	
	@Override
	public void handleOcclusion(CETOcclusionEvent event) {
		CETWindow window = event.getWindow();
		for ( MTRectangle r : overlapRects ) {
			window.superRemoveChild(r);
		}
		overlapRects = new ArrayList<MTRectangle>();
		Map<CETWindow,Vector3D[]> overlap = event.getOverlap();
		Matrix inverse = window.getGlobalInverseMatrix();
		MTApplication app = event.getApp();
		for ( Vector3D[] o : overlap.values() ) {
			Vector3D of = new Vector3D(o[0].x, o[0].y);
			Vector3D ol = inverse.mult(of);
			MTRectangle test = new MTRectangle(
				ol.x, ol.y,
				o[1].x - o[0].x,
				o[2].y - o[1].y,
				app
			);
			test.setFillColor(new MTColor(255, 0, 0));
			overlapRects.add(test);
			window.superAddChild(test);
		}
	}
	
	public void postOcclusion(CETOcclusionEvent event) {
		CETWindow window = event.getWindow();
		for ( MTRectangle r : overlapRects ) {
			window.superRemoveChild(r);
		}
		overlapRects = new ArrayList<MTRectangle>();
	}
}
