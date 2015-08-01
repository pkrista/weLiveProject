package edu.vub.welive.interfaces;

import java.util.ArrayList;

import edu.vub.at.objects.coercion.Async;
import edu.vub.welive.UsersColors;
import edu.vub.welive.UsersPoints;

public interface ATWeLive {
	// Method called when the user touches down on the canvas.
	@Async
	void touchDetected(int x, int y);

	//Method called when the user starts the game
	//My new id send to AT
	void myId(int id);

	//Method called after the new generation's calculation
	//Send to AT new array with userId and user points(x, y)
	//Next generation to other players
	void sendNewGenGrid(ArrayList<UsersPoints> usersPointsArray);

	//Send to all users USERS and IT Color
	void sendUsersColors(ArrayList<UsersColors> usersColorsArray);
	
	//Send grid height and width
	void sendGridSize(int h, int w);
}
