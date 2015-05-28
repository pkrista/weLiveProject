/**
 * This is interface (these functions will use main activity or other JAVA. 
 * These functions also can be used (called) from AmbientTalk)
 */
package edu.vub.welive.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import edu.vub.welive.UsersPoints;
import android.graphics.Point;

//This interface is used by WeLiveActivity
public interface JWeLive {
	/**
	 * Registers an AmbientTalk application to listen for GUI events
	 * which trigger the method calls declared in ATWeLive.
	 */

	public JWeLive registerATApp(ATWeLive weLive);

	//Functions that might be called by ambient talk
	//implemented by java class
	//
	//
	//start the game -> paint the grid
	public void startGame();
	
	//Get from AT all user IDs and store them in one list with colors
	public void newUserID(int userId);
	
	//Get fromAT coordinator ID
	public void sendCoordinatorId(int coorId);
	
	//get values from AT and put them into list 
	public void funcNewPutValues(int userId, int touchPointX, int touchPointY);

	
	//get from the coordinator new generation
	public void newGenerationArray(ArrayList<UsersPoints> usersPointsArray);
	
	
	//GreyOut user if he disconnects
	public void grayOut(int userId);
	
	//Color on user if he reconnects to game
	public void colorOn(int userId);
	
}
