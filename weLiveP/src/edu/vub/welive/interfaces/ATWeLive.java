package edu.vub.welive.interfaces;

import java.util.ArrayList;

import edu.vub.at.objects.coercion.Async;
import edu.vub.welive.UsersColors;
import edu.vub.welive.UsersPoints;

public interface ATWeLive {
	
	//Method called when the user starts the game
	//My new id send to AT
	void myId(int id);
	
	// Method called when the user touches down on the canvas.
	@Async
	void sendPlacedCell(int x, int y);

	//Method called after the new generation's calculation
	//Send to AT new array with userId and user points(x, y)
	@Async
	void sendNewGenGrid(ArrayList<UsersPoints> usersPointsArray);

	//method called when coordinator discovers new player and adds it to userColorArray
	//Send to all users inf (USER and IT Color)
	@Async
	void sendUsersColors(ArrayList<UsersColors> usersColorsArray);
	
	//Method called after grid size changes
	//Send grid height and width
	@Async
	void sendGridSize(int h, int w);
}
