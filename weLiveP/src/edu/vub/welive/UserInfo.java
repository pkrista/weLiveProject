package edu.vub.welive;

import java.io.Serializable;

public class UserInfo implements Serializable{
	private int userID;
	private int color;
	private boolean isGrayOut;
	
	public UserInfo(int userID, int color, boolean isColored){
		this.setUserID(userID);
		this.setColor(color);
		this.setisGrayOut(isColored);
	}
	
	public UserInfo(){
		
	}

	public boolean getisGrayOut(){
		return isGrayOut;
	}
	
	public void setisGrayOut(boolean isColored) {
		this.isGrayOut = isColored;
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
