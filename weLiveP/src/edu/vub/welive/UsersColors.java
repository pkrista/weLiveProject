package edu.vub.welive;

import java.io.Serializable;

public class UsersColors implements Serializable{
	private int userID;
	private int color;
	private boolean isColored;
	
	public UsersColors(int userID, int color, boolean isColored){
		this.setUserID(userID);
		this.setColor(color);
		this.setisColored(isColored);
	}

	public boolean getisColored(){
		return isColored;
	}
	
	public void setisColored(boolean isColored) {
		this.isColored = isColored;
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
