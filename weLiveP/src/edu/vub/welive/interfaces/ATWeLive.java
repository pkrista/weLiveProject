package edu.vub.welive.interfaces;

import android.graphics.Point;
import edu.vub.at.objects.coercion.Async;

public interface ATWeLive {
	// Method called when the user touches down on the canvas.

	@Async
		void touchDetected(int x, int y);
	
		//void touchDetected(Point touchPoint);	
		//My new id
	
		void myId(int id);	
	
}
