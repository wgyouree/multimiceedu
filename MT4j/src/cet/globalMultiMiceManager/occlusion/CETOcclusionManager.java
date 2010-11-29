package cet.globalMultiMiceManager.occlusion;

import java.util.HashMap;
import java.util.Map;

import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.util.math.Vector3D;

import cet.components.visibleComponents.widgets.CETWindow;

public class CETOcclusionManager {

	private Map<CETWindow, OcclusionPolicy> windows =
		new HashMap<CETWindow, OcclusionPolicy>();
	
	// singleton pattern
	private static CETOcclusionManager instance;
	private CETOcclusionManager() { }
	public static CETOcclusionManager getInstance() {
		if ( instance == null ) {
			instance = new CETOcclusionManager();
		}
		return instance;
	}
	
	public void registerWindow(CETWindow window, OcclusionPolicy policy) {
		windows.put(window, policy);
	}
	
	public boolean canMove(CETWindow window) {

		IBoundingShape b1 = window.getBounds();
		
		// find overlapping windows and check policies
		for ( CETWindow aWindow : windows.keySet() ) {
			if ( !(window.equals(aWindow)) ) {
				IBoundingShape b2 = aWindow.getBounds();
				Vector3D[] b1Vectors = b1.getVectorsGlobal();
				Vector3D[] b2Vectors = b2.getVectorsGlobal();
				Vector3D[] overlap = getOverlap(b1Vectors, b2Vectors);
				if ( overlap != null ) {
					// we have overlap, determine policy
					OcclusionPolicy policy = determinePolicy(window, aWindow);
					if ( policy == OcclusionPolicy.PREVENT ) {
						System.out.println("Prevented from moving");
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public Vector3D[] getOverlap(Vector3D[] v1, Vector3D[] v2) {
		
		// get individual points
		Vector3D tl1 = v1[0];
		Vector3D tr1 = v1[1];
		Vector3D br1 = v1[2];
		Vector3D bl1 = v1[3];
		
		System.out.println("Box 1: TL: " + tl1.x + "," + tl1.y + " TR: " + tr1.x + "," + tr1.y + " BL: " + bl1.x + "," + bl1.y + " BR: " + br1.x + "," + br1.y);
		
		Vector3D tl2 = v2[0];
		Vector3D tr2 = v2[1];
		Vector3D br2 = v2[2];
		Vector3D bl2 = v2[3];
		
		System.out.println("Box 2: TL: " + tl2.x + "," + tl2.y + " TR: " + tr2.x + "," + tr2.y + " BL: " + bl2.x + "," + bl2.y + " BR: " + br2.x + "," + br2.y);
		
		boolean tlInside = false;
		boolean trInside = false;
		boolean brInside = false;
		boolean blInside = false;
		
		int containingBox = 1;
		
		// determine if any of the corners are inside
		if ( tl1.x <= tl2.x && tl1.y <= tl2.y && br1.x >= tl2.x && br1.y >= tl2.y ) {
			System.out.println("Situation 1");
			tlInside = true;
		}
		if ( bl2.x <= bl1.x && bl2.y >= bl1.y && tr2.x >= bl1.x && tr2.y <= br1.y ) {
			System.out.println("Situation 2");
			trInside = true;
		}
		if ( tl2.x <= tl1.x && tl2.y <= tl1.y && br2.x >= tl1.x && br2.y >= tl1.y ) {
			System.out.println("Situation 3");
			brInside = true;
		}
		if ( bl1.x <= bl2.x && bl1.y >= bl2.y && tr1.x >= bl2.x && tr1.y <= bl2.y ) {
			System.out.println("Situation 4");
			blInside = true;
		}
		
		if ( tlInside || trInside || brInside || blInside ) {
			return new Vector3D[] {};
		}
		
		return null;
	}
	
	private OcclusionPolicy determinePolicy(CETWindow w1, CETWindow w2) {
		OcclusionPolicy p1 = windows.get(w1);
		OcclusionPolicy p2 = windows.get(w2);
		if ( p1 == OcclusionPolicy.PREVENT || p2 == OcclusionPolicy.PREVENT ) {
			return OcclusionPolicy.PREVENT;
		}
		else if ( p1 == OcclusionPolicy.TRANSPARENCY || p2 == OcclusionPolicy.TRANSPARENCY ) {
			return OcclusionPolicy.TRANSPARENCY;
		}
		return OcclusionPolicy.NONE;
	}
}
