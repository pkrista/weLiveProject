package edu.vub.welive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.text.InputFilter.LengthFilter;

public class CalculateNextGen {
		
	public CalculateNextGen(){
		
	}

	//Store neighbor userID in the HashMap
	private HashMap<Integer, Integer> neighborIDs = new HashMap<Integer, Integer>();
	private int userId = 0;
	
	public ArrayList<UsersPoints> nextGeneration() {
		//clear list of all values
		//in calculations add necessary values again back
		ArrayList<UsersPoints> OldUsersPointsArray = Board.UsersPointsArray;
		ArrayList<UsersPoints> NewUsersPointsArray = new ArrayList<UsersPoints>();
		
	
		int neighbor = 0;

		int gridHeight = GridView.getmHeight();
		int gridWidth = GridView.getmWidth();

		/*
		 * Function returns a list with all cells that will stay live
		 */
		NewUsersPointsArray = checkWhichCellsStaysLive(OldUsersPointsArray, NewUsersPointsArray);


		/*
		 * For cells to get live, cause of 3 neighbor
		 */
		for(int yi = 0; yi < gridHeight; yi++){
			for(int xj = 0; xj < gridWidth; xj++){
				//At the beginning the neighbors are set to 0 
				neighbor = 0;
				neighborIDs.clear();

				neighbor = countNeighbors(OldUsersPointsArray, xj, yi, true);

				
				/*
				 * If there is 3 neighbors dead cell becomes live
				 * - if 1 user own 3 neighbors -> he owns new cell
				 * - if 2 players own 3 neighbors -> owns the user who owns 2 cells
				 * - if 3 players own 3 neighbors -> wins the one with biggest id
				 */
				if(neighbor == 3){

					if(!cellExists(xj, yi, NewUsersPointsArray)){
						int countUsers = neighborIDs.size();

						//To store biggest value form the hashmap
						int biggerID = 0;
						int biggerVal = 0;

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
						} //ends map.Entry

						//Add dead cell as live into the list
						NewUsersPointsArray.add(new UsersPoints(userId, xj, yi));
					}
				} //ends neighbor == 3
			}
		}// ends loop for dead cells t become live
		
		return NewUsersPointsArray;
	}
	
	
	/*
	 * Function return a list with cells that will stay live
	 * caused by 2 or 3 neighbors
	 */
	private ArrayList<UsersPoints> checkWhichCellsStaysLive(ArrayList<UsersPoints> OldUsersPointsArray, ArrayList<UsersPoints> NewUsersPointsArray){
		int neighbor = 0;
		
		for(UsersPoints p : OldUsersPointsArray){
			int cellX = p.getX();
			int cellY = p.getY();
			userId = p.getUserID();
			
			//Count neighbors
			neighbor = countNeighbors(OldUsersPointsArray, cellX, cellY, false);


			/*
			 *  If there is 2 or 3 neighbor, cell stays live
			 */
			if(neighbor == 2 || neighbor ==3){
				
				if(!cellExists(cellX, cellY, NewUsersPointsArray)){
					NewUsersPointsArray.add(new UsersPoints(userId, cellX, cellY));
				}
			} 
		}
		
		return NewUsersPointsArray;
	}
	
	private int countNeighbors(ArrayList<UsersPoints> OldUsersPointsArray, int cellX, int cellY, boolean storeInHashMap){
		int neighbor = 0;
		for(UsersPoints t : OldUsersPointsArray){
			
			userId = t.getUserID();
			
			if(hasNeighbor(cellX, cellY, t.getX(), t.getY())){
				neighbor++;
				
				if(storeInHashMap){
					storeUserAndNeighborCount();
				}
			}
		}
		return neighbor;
	}

	
	/*
	 * Function stores userId and count of cells that are
	 * neighbors to current cell
	 * For : to decide with user will own new live cell
	 */
	private void storeUserAndNeighborCount(){
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
		
	/*
	 * Function checks if the cell is already exists
	 * cellExists = true if cell exist
	 * cellExists = false if cell not exist
	 */
	public boolean cellExists(int PointX, int PointY, ArrayList<UsersPoints> NewUsersPointsArray){
		//Check if the cell is stored in NewUsersPointsArray
		for(UsersPoints p:  NewUsersPointsArray){
			if(p.getX() == PointX && p.getY() == PointY){
				//if cell exist return true
				return true;
			}
		}
		//If cell not exist
		return false;
	}
}
