/**
 * This is interface (these functions will use main activity or other JAVA. 
 * These functions also can be used (called) from AmbientTalk)
 */
package edu.vub.welive.interfaces;

import java.util.ArrayList;

import edu.vub.welive.UsersColors;
import edu.vub.welive.UsersPoints;


//This interface is used by WeLiveActivity
public interface JWeLive {
	/**
	 * Registers an AmbientTalk application to listen for GUI events
	 * which trigger the method calls declared in ATWeLive.
	 */

	public JWeLive registerATApp(ATWeLive weLive);


	//start the game
	public void startGame();
	

	//Get from AT all user IDs and store them in one list with colors
	public void newUserID(int userId);

	//Get from AT coordinator ID
	public void newCoordinatorId(int coorId);

	//get values from AT and put them into list (x and y)
	public void newPlacedCell(int userId, int touchPointX, int touchPointY);

	//get from AT the coordinator's new generation
	public void newGenerationArray(ArrayList<UsersPoints> usersPointsArray);
	
	//get all users and it color from coordinator (sent by coordinator)
	public void newUsersColorArray(ArrayList<UsersColors> NewUsersColorsArray);
	
	//Get new grid size from AT (sent by coordinator)
	public void newGridSize(int h, int w);
	
	

	//GreyOut user if he disconnects
	public void grayOut(int userId);

	//Color on user if he reconnects to game
	public void colorOn(int userId);
}
