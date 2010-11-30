package cet.globalMultiMiceManager.occlusion;

import java.util.Map;

import org.mt4j.MTApplication;
import org.mt4j.util.math.Vector3D;

import processing.core.PGraphics;

import cet.components.visibleComponents.widgets.CETWindow;

public class CETOcclusionEvent {

	private Map<CETWindow,Vector3D[]> overlap;
	private CETWindow window;
	private PGraphics graphics;
	private MTApplication app;
	
	public CETOcclusionEvent(Map<CETWindow,Vector3D[]> overlap, CETWindow window, PGraphics graphics, MTApplication app) {
		this.overlap = overlap;
		this.window = window;
		this.graphics = graphics;
		this.app = app;
	}

	public Map<CETWindow,Vector3D[]> getOverlap() {
		return overlap;
	}

	public CETWindow getWindow() {
		return window;
	}

	public PGraphics getGraphics() {
		return graphics;
	}
	
	public MTApplication getApp() {
		return app;
	}
}
