package cet.globalMultiMiceManager.conflictHandlers;

import org.mt4j.util.math.Vector3D;

import cet.globalMultiMiceManager.CETConflictEvent;
import cet.globalMultiMiceManager.CETConflictType;
import cet.globalMultiMiceManager.ICETConflictHandler;
import cet.globalMultiMiceManager.animation.Animator;

public class ShakeDragConflictHandler implements ICETConflictHandler {

	@Override
	public boolean handleConflict(CETConflictEvent event) {
		if ( event.getType() == CETConflictType.DRAG ) {
			System.out.println("ShakeDragConflictHandler handling conflict");
			Animator animator = new Animator(
				event.getComponent(),
				new Integer[] { 500, 500, 500, 500, 500, 500 },
				new Vector3D[] {
					new Vector3D(-10, 0),
					new Vector3D(20, 0),
					new Vector3D(-20, 0),
					new Vector3D(20, 0),
					new Vector3D(-20, 0),
					new Vector3D(10, 0)
				}
			);
			animator.start();
		}
		return false;
	}
}
