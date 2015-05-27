package edu.vub.welive.interfaces;

import java.util.ArrayList;

import android.graphics.Point;
import edu.vub.at.objects.coercion.Async;
import edu.vub.welive.UsersPoints;

public interface ATWeLive {
	// Method called when the user touches down on the canvas.

	@Async
		void touchDetected(int x, int y);
	
		//void touchDetected(Point touchPoint);	
		//My new id
	
		void myId(int id);
		
		
		//Send to AT new array with userId and points
		//Next generation
		void sendNewGenGrid(ArrayList<UsersPoints> usersPointsArray);

		//Send to AT new coordinator grid -> to all users have the same grid, after coordinator selection
		void newCoordinatorGrid(ArrayList<UsersPoints> usersPointsArray);	
	
}
