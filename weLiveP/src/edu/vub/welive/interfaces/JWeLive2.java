/**
 * This is interface (these functions will use main activity or other JAVA. 
 * These functions also can be used (called) from AmbientTalk)
 */
package edu.vub.welive.interfaces;

import java.util.HashMap;

import android.graphics.Point;

//This interface is used by MainActivity
public interface JWeLive2 {
	/**
	 * Registers an AmbientTalk application to listen for GUI events
	 * which trigger the method calls declared in ATWeLive.
	 */

	public JWeLive2 registerATApp(ATWeLive2 weLive);	
	
	
}
