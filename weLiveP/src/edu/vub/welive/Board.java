package edu.vub.welive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Board {

	public Board(){
		
	}
	
	// UsersPoints array to store userId and index of x and y (touch)
	public static ArrayList<UsersPoints> UsersPointsArray = new ArrayList<UsersPoints>();
	
	// MyUser game generation
	public int countGeneration = 0;
	
	public ArrayList<UsersPoints> NewPointsArray = new ArrayList<UsersPoints>();
//	public CopyOfCalculateNextGenCOPY generation = new CopyOfCalculateNextGenCOPY();
	
	/*
	 * Function stores placed cell
	 */
	public void storeCell(int userId, int touchPointX, int touchPointY, boolean fromNextGen){
		//Store placed cell
		if(fromNextGen){
			NewUsersPointsArray.add(new UsersPoints(userId, touchPointX, touchPointY));
		}
		else{
			UsersPointsArray.add(new UsersPoints(userId, touchPointX, touchPointY));
		}
	}
	
	/*
	 * Function checks if the cell is already places
	 * cellExists = true if cell is already live
	 * cellExists = false if cell is not set as live yet
	 */
	public boolean cellExists(int touchPointX, int touchPointY, ArrayList<UsersPoints> PointsArray){
		//Check if the cell is already live
		for(UsersPoints p:  PointsArray){
			if(p.getX() == touchPointX && p.getY() == touchPointY){
				//if cell is already live return true
				return true;
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
		WeLiveActivity.myScore = 0;
		for(UsersPoints p : UsersPointsArray){
			if(p.getUserID() == WeLiveActivity.myDevID){
				WeLiveActivity.myScore++;
			}
		}
	}
	
	/*
	 * Calculate how many cells user have in the bank
	 * 
	 */
	public void calculateCellBank(){
		WeLiveActivity.myCellBank--;
	}
	
	
	/*
	 * Count generations, every 5 generations give user +4 cells
	 */
	public void countGeneration(){
		//Set that generation is +1
		countGeneration ++;
		
		//every 5 generation add to cell bank + 4 cells
		if((countGeneration % 5) == 0){
			WeLiveActivity.myCellBank = WeLiveActivity.myCellBank + 4;
		}
	}
	
	/*
	 * Calculates next generations
	 * after calculations send new grid to AT -> AT further send to other peers
	 */
	public void calculateNextGeneration(){

//		this.NewPointsArray = this.generation.nextGeneration();
//		UsersPointsArray = this.NewPointsArray;

		UsersPointsArray = nextGeneration();
		
		//To count generation and give user extra cells each 5 generations
		countGeneration();
	}
	
	
	/*
	 * Functions to calculate next generation
	 * 
	 */
	//Store neighbor userID in the HashMap
	private HashMap<Integer, Integer> neighborIDs = new HashMap<Integer, Integer>();
	
	private ArrayList<UsersPoints> OldUsersPointsArray = new ArrayList<UsersPoints>();
	private ArrayList<UsersPoints> NewUsersPointsArray = new ArrayList<UsersPoints>();
	
	public ArrayList<UsersPoints> nextGeneration() {
		
		OldUsersPointsArray = Board.UsersPointsArray;
		NewUsersPointsArray = new ArrayList<UsersPoints>();
		
		/*
		 * Function returns a list with all cells that will stay live
		 */
		checkWhichCellsStaysLive();
		
		/*
		 * Function returns a list with all cells that will stay live
		 */
		checkWhichCellsBecomesLive();
		
		return NewUsersPointsArray;
	}
	
	
	/*
	 * Function checks for the cells that will become live
	 * cause of 3 neighbor
	 */
	private void checkWhichCellsBecomesLive(){
		
		int gridHeight = GridView.getmHeight();
		int gridWidth = GridView.getmWidth();
		
		
		for(int cellY = 0; cellY < gridHeight; cellY++){
			for(int cellX = 0; cellX < gridWidth; cellX++){
				/*
				 * Clean the neighborIDs HashMap
				 * key - userID that has neighbor to current cell
				 * value - count of how many of neighbors own user
				 */
				neighborIDs.clear();
				
				//Count neighbors
				int neighbor = countNeighbors(cellX, cellY, true);
				
				/*
				 * If there is 3 neighbors dead cell becomes live
				 */
				if(neighbor == 3){
					//Get new cell owner
					int userId = getNewCellOwner();
					
					//Store cell
					if(!cellExists(cellX, cellY, NewUsersPointsArray)){
						storeCell(userId, cellX, cellY, true);
					}
				} //ends neighbor == 3
			}
		}
	}
	
	/*
	 * - if 1 user own 3 neighbors -> he owns new cell
	 * - if 2 players own 3 neighbors -> owns the user who owns 2 cells
	 * - if 3 players own 3 neighbors -> wins the one with biggest id
	 */
	private int getNewCellOwner(){
		
		int countUsers = neighborIDs.size();

		//To store biggest value form the HashMap
		int biggerID = 0;
		int biggerVal = 0;
		int userId = 0;
		
		for(Map.Entry m: neighborIDs.entrySet()){  
			//If in the list is just one user then he owns the new cell
			if(countUsers == 1){
				userId = (Integer) m.getKey();
			}
			//if there is 2 owners then 1 owns 1 cell and other owns 2 cells
			else if(countUsers == 2){ 
				if(biggerVal <= ((Integer) m.getValue())){
					biggerVal = (Integer) m.getValue();
					userId = (Integer) m.getKey();
				}
			}
			//3 users own one neighbor cell -> wins biggest ID
			else{ 
				if(biggerID <= (Integer) m.getKey()){
					biggerID = (Integer) m.getKey();
					userId = (Integer) m.getKey();
				}
			}
		}
		return userId;
	}
	
	/*
	 * Function return a list with cells that will stay live
	 * caused by 2 or 3 neighbors
	 */
	private void checkWhichCellsStaysLive(){		
		for(UsersPoints p : OldUsersPointsArray){
			int cellX = p.getX();
			int cellY = p.getY();
			
			//Count neighbors
			int neighbors = countNeighbors(cellX, cellY, false);

			//If there is 2 or 3 neighbor, cell stays live
			if(neighbors == 2 || neighbors ==3){
				
				if(!cellExists(cellX, cellY, NewUsersPointsArray)){
					storeCell(p.getUserID(), cellX, cellY, true);
				}
			} 
		}
	}
	
	private int countNeighbors(int cellX, int cellY, boolean storeInHashMap){
		int neighbors = 0;
		for(UsersPoints t : OldUsersPointsArray){
					
			if(hasNeighbor(cellX, cellY, t.getX(), t.getY())){
				neighbors++;
				
				if(storeInHashMap){
					storeUserAndNeighborCount(t.getUserID());
				}
			}
		}
		return neighbors;
	}

	
	/*
	 * Function stores userId and count of cells that are
	 * neighbors to current cell
	 * For : to decide with user will own new live cell
	 */
	private void storeUserAndNeighborCount(int userId){
		
		if(neighborIDs.containsKey(userId)){
			int countNeighbor = neighborIDs.get(userId);
			countNeighbor++;
			neighborIDs.put(userId, countNeighbor);
		}
		else{
			neighborIDs.put(userId, 1);
		}
	}
	
	/*
	 * 
	 */
	private boolean hasNeighbor(int cellX, int cellY, int neighborX, int neighborY){
		if(
				(neighborX == cellX-1 && (neighborY == cellY-1 || neighborY == cellY || neighborY == cellY+1)) ||
				(neighborX == cellX+1 && (neighborY == cellY-1 || neighborY == cellY || neighborY == cellY+1)) ||
				(neighborX == cellX) && (neighborY == cellY-1|| neighborY == cellY+1)||
				(neighborY == cellY-1 && (neighborX == cellX-1 || neighborX == cellX || neighborX == cellX+1)) ||
				(neighborY == cellY+1 && (neighborX == cellX-1 || neighborX == cellX || neighborX == cellX+1)) ||
				(neighborY == cellY && (neighborX == cellX-1 || neighborX == cellX+1))
				){
			return true;
		}
		return false;
	}	

}
