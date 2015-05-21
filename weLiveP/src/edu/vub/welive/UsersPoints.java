package edu.vub.welive;

public class UsersPoints {

	private int userID;
	private int x;
	private int y;
	

	public UsersPoints(int userID, int x, int y){
		this.setUserID(userID);
		this.setX(x);
		this.setY(y);
	}


	public int getUserID() {
		return userID;
	}


	public void setUserID(int userID) {
		this.userID = userID;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}
	
	
}
