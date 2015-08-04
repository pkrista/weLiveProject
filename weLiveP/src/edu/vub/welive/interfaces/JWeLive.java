/**
 * This is interface (these functions will use main activity or other JAVA. 
 * These functions also can be used (called) from AmbientTalk)
 */
package edu.vub.welive.interfaces;

import java.util.ArrayList;

import edu.vub.welive.UserInfo;
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
	public void storeUser(int userId);

	//get values from AT and put them into list (x and y)
	public void storePlacedCell(int userId, int touchPointX, int touchPointY);
	
	//Get from AT coordinator ID
	public void setCoordinatorId(int coorId);

	//get from AT the coordinator's new generation
	public void setGenerationArray(ArrayList<UsersPoints> usersPointsArray);
	
	//get all users and it color from coordinator (sent by coordinator)
	public void setUsersColorArray(ArrayList<UserInfo> NewUsersColorsArray);
	
	//Get new grid size from AT (sent by coordinator)
	public void setGridSize(int h, int w);

	//GreyOut = true if user disconnects
	//GrayOut = false if user reconnects
	public void grayOut(int userId, boolean isGrayOut);

}
