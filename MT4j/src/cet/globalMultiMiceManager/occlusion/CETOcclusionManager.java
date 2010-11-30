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
					else if ( policy == OcclusionPolicy.DEFER ) {
						System.out.println("Deferring to windows");
						window.addOverlap(aWindow, overlap);
						aWindow.addOverlap(window, overlap);
					}
				}
				else {
					window.removeOverlap(aWindow);
					aWindow.removeOverlap(window);
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
		
		// is box 1 inside box 2?
		if ( tl2.x <= tl1.x && tl2.y <= tl1.y &&
			 br2.x >= br1.x && br2.y >= br1.y ) {
			return v1;
		}
		
		// is box 2 inside box 1?
		if ( tl1.x <= tl2.x && tl1.y <= tl2.y &&
			 br1.x >= br2.x && br1.y >= br2.y ) {
			return v2;
		}
		
		// 1
		if ( tl2.x <= tl1.x && tl2.y <= tl1.y && tr2.x >= tr1.x && tr2.y <= tr1.y && br2.y >= tr1.y ) {
			System.out.println("Situation 5 - 2 contains 1");
			return new Vector3D[] {
				tl1,
				tr1,
				new Vector3D(tr1.x, br2.y),
				new Vector3D(tl1.x, br2.y)
			};
		}
		
		// 2
		if ( tl1.x <= tl2.x && tl1.y <= tl2.y && tr1.x >= tr2.x && tr1.y <= tr2.y && br1.y >= tr2.y ) {
			System.out.println("Situation 5 - 1 contains 2");
			return new Vector3D[] {
				tl2,
				tr2,
				new Vector3D(tr2.x, bl1.y),
				new Vector3D(tl2.x, bl1.y)
			};
		}
		
		// 3
		if ( tl1.x <= tr2.x && tl1.y <= tr2.y && bl1.x <= br2.x && bl1.y >= br2.y && bl2.x <= bl1.x ) {
			System.out.println("Situation 6 - 1 contains 2");
			return new Vector3D[] {
				new Vector3D(tl1.x, tl2.y),
				tr2,
				br2,
				new Vector3D(tl1.x, bl2.y)
			};
		}
		
		// 4
		if ( tl2.x <= tr1.x && tl2.y <= tr1.y && bl2.x <= br1.x && bl2.y >= br1.y && bl1.x <= bl2.x ) {
			System.out.println("Situation 6 - 2 contains 1");
			return new Vector3D[] {
				new Vector3D(tl2.x, tl1.y),
				tr1,
				br1,
				new Vector3D(tl2.x, bl1.y)
			};
		}
		
		// 5
		if ( tl1.x <= bl2.x && tl1.y <= bl2.y && tr1.x >= br2.x && tr1.y <= br2.y && tr1.y >= tr2.y ) {
			System.out.println("Situation 7 - 1 contains 2");
			return new Vector3D[] {
				new Vector3D(tl2.x, tl1.y),
				new Vector3D(tr2.x, tl1.y),
				br2,
				bl2
			};
		}
		
		// 6
		if ( tl2.x <= bl1.x && tl2.y <= bl1.y && tr2.x >= br1.x && tr2.y <= br2.y && tr1.y <= tr2.y ) {
			System.out.println("Situation 7 - 2 contains 1");
			return new Vector3D[] {
				new Vector3D(bl1.x, tl2.y),
				new Vector3D(br1.x, tl2.y),
				br1,
				bl1
			};
		}
		
		// 7
		if ( tl2.x <= tl1.x && tl2.y <= tl1.y && bl2.x <= bl1.x && bl2.y >= bl1.y && br2.x >= bl1.x ) {
			System.out.println("Situation 8 - 2 contains 1");
			return new Vector3D[] {
				tl1,
				new Vector3D(tr2.x, tl1.y),
				new Vector3D(tr2.x, bl1.y),
				bl1
			};
		}
		
		// 8
		if ( tl2.x >= tl1.x && tr1.y <= tl2.y && bl2.x <= br1.x && bl2.y <= br1.y && br2.x >= br1.x ) {
			System.out.println("Situation 8 - 1 contains 2");
			return new Vector3D[] {
				tl2,
				new Vector3D(tr1.x, tl2.y),
				new Vector3D(tr1.x, bl2.y),
				bl2
			};
		}
		
		
		boolean tlInside = false;
		boolean trInside = false;
		boolean brInside = false;
		boolean blInside = false;
		
		int count = 0;
		
		// determine if any of the corners are inside
		if ( tl1.x <= tl2.x && tl1.y <= tl2.y && br1.x >= tl2.x && br1.y >= tl2.y ) {
			System.out.println("Situation 1");
			tlInside = true;
			count++;
		}
		if ( bl2.x <= bl1.x && bl2.y >= bl1.y && tr2.x >= bl1.x && tr2.y <= br1.y ) {
			System.out.println("Situation 2");
			trInside = true;
			count++;
		}
		if ( tl2.x <= tl1.x && tl2.y <= tl1.y && br2.x >= tl1.x && br2.y >= tl1.y ) {
			System.out.println("Situation 3");
			brInside = true;
			count++;
		}
		if ( bl1.x <= bl2.x && bl1.y >= bl2.y && tr1.x >= bl2.x && tr1.y <= bl2.y ) {
			System.out.println("Situation 4");
			blInside = true;
			count++;
		}
		
		if ( count == 0 ) {
			return null;
		}
		
		else if ( count == 1 ) {
			
			if ( tlInside ) {
				return new Vector3D[] {
					tl2,
					new Vector3D(tr1.x, tl2.y),
					br1,
					new Vector3D(tl2.x, br1.y)
				};
			}
			else if ( brInside ) {
				return new Vector3D[] {
					tl1,
					new Vector3D(tr2.x, tl1.y),
					br2,
					new Vector3D(tl1.x, br2.y)
				};
			}
			else if ( trInside ) {
				return new Vector3D[] {
					new Vector3D(bl1.x, tr2.y),
					tr2,
					new Vector3D(tr2.x, br1.y),
					bl1
				};
			}
			else if ( blInside ) {
				return new Vector3D[] {
					new Vector3D(bl2.x, tr1.y),
					tr1,
					new Vector3D(tr1.x, br2.y),
					bl2
				};
			}
			
		}
		/*
		else if ( count == 2 ) {
			
			if ( tlInside && trInside ) {
				if ( tl1.x >= tl2.x ) {
					// 1 contains 2
					System.out.println("1 contains 2");
					return new Vector3D[] {
						tl2,
						tr2,
						new Vector3D(tr2.x, bl1.y),
						new Vector3D(tl2.x, bl1.y)
					};
				}
				else {
					// 2 contains 1
					System.out.println("2 contains 1");
					return new Vector3D[] {
						new Vector3D(bl1.x, tl2.y),
						new Vector3D(br1.x, tl2.y),
						br1,
						bl1
					};
				}
			}
			else if ( trInside && brInside ) {
				if ( tl1.y <= tr2.y ) {
					// 1 contains 2
					return new Vector3D[] {
						new Vector3D(tl1.x, tl2.y),
						tr2,
						br2,
						new Vector3D(tl1.x, bl2.y)
					};
				}
				else {
					// 2 contains 1
					return new Vector3D[] {
						tl1,
						new Vector3D(tr2.x, tl1.y),
						new Vector3D(tr2.x, bl1.y),
						bl1
					};
				}
			}
			else if ( brInside && blInside ) {
				if ( tl2.x <= tl1.x ) {
					// 1 contains 2
					return new Vector3D[] {
						new Vector3D(tl2.x, tl1.y),
						new Vector3D(tr2.x, tl1.y),
						br2,
						bl2
					};
				}
				else {
					// 2 contains 1
					return new Vector3D[] {
						tl1,
						tr1,
						new Vector3D(tr1.x, br2.y),
						new Vector3D(tl1.x, br2.y)
					};
				}
			}
			else if ( blInside && tlInside ) {
				if ( tl2.x >= tl1.x ) {
					// 1 contains 2
					return new Vector3D[] {
						tl2,
						new Vector3D(tr1.x, tl2.y),
						new Vector3D(tr1.x, bl2.y),
						bl2
					};
				}
				else {
					// 2 contains 1
					return new Vector3D[] {
						new Vector3D(tl2.x, tl1.y),
						tr1,
						br1,
						new Vector3D(tl2.x, bl1.y)
					};
				}
			}
			
		}
		*/
		
		if ( count == 3 ) {
			System.err.println("Windows are rectangular, cannot have exactly 3 corners contained by another window.");
		}
		
		System.err.println("Error, window corner count should be between 0 and 4 inclusive.");
		return null;
	}
	
	private OcclusionPolicy determinePolicy(CETWindow w1, CETWindow w2) {
		OcclusionPolicy p1 = windows.get(w1);
		OcclusionPolicy p2 = windows.get(w2);
		if ( p1 == OcclusionPolicy.PREVENT || p2 == OcclusionPolicy.PREVENT ) {
			return OcclusionPolicy.PREVENT;
		}
		else if ( p1 == OcclusionPolicy.DEFER || p2 == OcclusionPolicy.DEFER ) {
			return OcclusionPolicy.DEFER;
		}
		return OcclusionPolicy.NONE;
	}
}
