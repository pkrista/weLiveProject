package edu.vub.welive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.text.InputFilter.LengthFilter;

public class CalculateNextGen {

	public CalculateNextGen(){
	}

	public ArrayList<UsersPoints> nextGeneration(int gridHeight, int gridWidth) {
		//clear list of all values
		//in calculations add necessary values again back
		ArrayList<UsersPoints> OldUsersPointsArray = WeLiveActivity.UsersPointsArray;
		ArrayList<UsersPoints> NewUsersPointsArray = new ArrayList<UsersPoints>();


		int x;
		int y;
		int userId = 0;
		int neighbor = 0;



		/*
		 * For cells to continue to be live or become day
		 */
		for(UsersPoints p : OldUsersPointsArray){
			x = p.getX();
			y = p.getY();
			userId = p.getUserID();
			neighbor = 0;


			for(UsersPoints t : OldUsersPointsArray){
				if((t.getX() == x-1 && (t.getY() == y-1 || t.getY() == y || t.getY() == y+1)) ||
						(t.getX() == x+1 && (t.getY() == y-1 || t.getY() == y || t.getY() == y+1)) ||
						(t.getX()== x) && (t.getY() == y-1|| t.getY() == y+1)||
						(t.getY() == y-1 && (t.getX() == x-1 || t.getX() == x || t.getX() == x+1)) ||
						(t.getY() == y+1 && (t.getX() == x-1 || t.getX() == x || t.getX() == x+1)) ||
						(t.getY() == y && (t.getX() == x-1 || t.getX() == x+1))
						){
					neighbor++;
				}
			}

			/*
			 *  If there is 2 or 3 neighbor, cell stays live
			 */
			if(neighbor == 2 || neighbor ==3){
				Boolean inList = false;

				for(UsersPoints d : NewUsersPointsArray){
					if(x == d.getX() && y == d.getY()){
						inList = true;
					}
				}

				if(inList == false){
					NewUsersPointsArray.add(new UsersPoints(userId, x, y));
				}
			} 
		}// ends loop for searching which cell will stay live



		//Store neighbor userID in the Hashmap
		HashMap<Integer, Integer> neighborIDs = new HashMap<Integer, Integer>();

		/*
		 * For cells to get live, cause of 3 neighbor
		 */
		for(int yi = 0; yi < gridHeight; yi++){
			for(int xj = 0; xj < gridWidth; xj++){
				//At the beginning the neighbors are set to 0 
				neighbor = 0;
				neighborIDs.clear();

				for(UsersPoints d : OldUsersPointsArray){
					int dx = d.getX();
					int dy = d.getY();

					if(
							((dx == xj-1) && (dy == yi-1 || dy == yi || dy == yi+1))||
							((dx == xj+1) && (dy == yi-1 || dy == yi || dy == yi+1))||
							((dx == xj) && (dy == yi-1 || dy == yi+1))||
							((dy == yi-1) && (dx == xj-1 || dx == xj || dx == xj+1))||
							((dy == yi-1) && (dx == xj-1 || dx == xj || dx == xj+1))||
							((dy == yi) && (dx == xj-1 || dx == xj+1))
							){
						userId = d.getUserID();
						neighbor++;

						/*
						 * Check the neighbor hashmap if user is in the map, set value++
						 * if user is not in the list put him in and set value to 1
						 * The owner of the new cell will be the one with higher value
						 * OR with bigger ID
						 */
						if(neighborIDs.containsKey(d.getUserID())){
							int countNeighbor = neighborIDs.get(d.getUserID());
							countNeighbor++;
							neighborIDs.put(userId, countNeighbor);
						}
						else{
							neighborIDs.put(userId, 1);
						}

					} // ends big if
				}// ends for UsersPoints

				
				/*
				 * If there is 3 neighbors dead cell becomes live
				 * - if 1 user own 3 neighbors -> he owns new cell
				 * - if 2 players own 3 neighbors -> owns the user who owns 2 cells
				 * - if 3 players own 3 neighbors -> wins the one with biggest id
				 */
				if(neighbor == 3){

					Boolean inList = false;

					//If the point is already in the list dont put it again
					for(UsersPoints d : NewUsersPointsArray){
						if(xj == d.getX() && yi == d.getY()){
							inList = true;
						}
					}

					if(inList == false){
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
}
