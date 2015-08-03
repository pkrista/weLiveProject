package edu.vub.welive;

import java.util.ArrayList;

public class Board {

	public Board(){
		
	}
	
	// UsersPoints array to store userId and index of x and y (touch)
	public static ArrayList<UsersPoints> UsersPointsArray = new ArrayList<UsersPoints>();
	
	// MyUser game generation
	public int countGeneration = 0;
	
	public ArrayList<UsersPoints> NewPointsArray = new ArrayList<UsersPoints>();
	public CalculateNextGen generation = new CalculateNextGen();
	
	
	/*
	 * Function stored placed cell
	 */
	public void storeCell(int userId, int touchPointX, int touchPointY){
		//Store placed cell in UsersPointsArray
		UsersPointsArray.add(new UsersPoints(userId, touchPointX, touchPointY));
	}
	
	/*
	 * Function checks if the cell is already places
	 * cellExists = true if cell is already live
	 * cellExists = false if cell is not set as live yet
	 */
	public boolean cellExists(int touchPointX, int touchPointY){
		//Check if the cell is already live
		for(UsersPoints p:  UsersPointsArray){
			if(p.getX() == touchPointX && p.getY() == touchPointY){
				//if cell is already live return true
				return true;
				
				//send back user his cell
				//TODO
			}
		}
		//If cell is not live return false
		return false;
	}
	
	/*
	 * Functions returns id of the user that owns the cell
	 */
	public int findCellOwner(int i, int j){
		int owner = 0;
		for(UsersPoints p : UsersPointsArray){
			if( p.getX() == i && p.getY() == j){					
				owner = p.getUserID();
			}
		}
		return owner;
	}
	
	/*
	 * Calculate how many cells user owns on the grid
	 */
	public void calculateScore(){
		GridView.userScore = 0;
		for(UsersPoints p : UsersPointsArray){
			if(p.getUserID() == WeLiveActivity.myDevID){
				GridView.userScore++;
			}
		}
	}
	
	/*
	 * Count generations, every 5 generations give user +4 cells
	 */
	public void countGeneration(){
		//Set that generation is +1
		countGeneration ++;
		
		//every 5 generation add to cell bank + 4 cells
		if((countGeneration % 5) == 0){
			GridView.bankCell = GridView.bankCell + 4;
		}
	}
	
	/*
	 * Calculates next generations
	 * after calculations send new grid to AT -> AT further send to other peers
	 * 
	 */
	public void calculateNextGeneration(){

		this.NewPointsArray = this.generation.nextGeneration();
		UsersPointsArray = this.NewPointsArray;

		//To count generation and give user extra cells each 5 generations
		countGeneration();
	}
}
