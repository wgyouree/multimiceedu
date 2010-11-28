package cet.globalMultiMiceManager.animation;

import java.util.Timer;
import java.util.TimerTask;

import org.mt4j.components.MTComponent;
import org.mt4j.util.math.Vector3D;

public class Animator extends TimerTask {

	private MTComponent component;
	private Integer timeouts[];
	private Vector3D vectors[];
	
	private Timer timer = new Timer();
	private int pos = 0;
	
	public Animator(MTComponent component, Integer timeouts[], Vector3D vectors[]) {
		this.component = component;
		this.timeouts = timeouts;
		this.vectors = vectors;
	}
	
	public void start() {
		timer.schedule(this, 0);
	}
	
	public void run() {
		if ( pos < timeouts.length && pos < vectors.length ) {
			component.translateGlobal(vectors[pos]);
			timer.schedule(this, pos++);
		}
		else {
			this.destroy();
		}
	}
	
	public void destroy() {
		this.timer = null;
		this.component = null;
		this.timeouts = null;
		this.vectors = null;
	}
}
