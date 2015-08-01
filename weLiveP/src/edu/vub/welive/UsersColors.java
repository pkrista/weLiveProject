package edu.vub.welive;

import java.io.Serializable;

public class UsersColors implements Serializable{
	private int userID;
	private int color;
	
	public UsersColors(int userID, int color){
		this.setUserID(userID);
		this.setColor(color);
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
