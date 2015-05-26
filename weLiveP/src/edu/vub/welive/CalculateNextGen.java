package edu.vub.welive;

import java.util.ArrayList;

import android.text.InputFilter.LengthFilter;

public class CalculateNextGen {
	
	public CalculateNextGen(){
	}

	public ArrayList<UsersPoints> nextGeneration(int gridHeight, int gridWidth) {
		//clear list of all values
		//in calculations add necessary values again back
		ArrayList<UsersPoints> OldUsersPointsArray = WeLiveActivity.UsersPointsArray;
		ArrayList<UsersPoints> NewUsersPointsArray = new ArrayList<UsersPoints>();
		
		int liveCels = OldUsersPointsArray.size();
		int count = 0;
		int x;
		int y;
		int userId = 0;
		int neighbor = 0;
		
		System.out.println("Esmu te = syze is = " + liveCels);
		
		//For cells to continue to be live or become day
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
			 
			 // If there is 2 or 3 neighbor, cell stays live
			 if(neighbor == 2 || neighbor ==3){
				 Boolean inList = false;
				 
				 for(UsersPoints d : NewUsersPointsArray){
					 if(x == d.getX() && y == d.getY()){
						 inList = true;
					 }
				 }
				 
				 if(inList == false){
					 NewUsersPointsArray.add(new UsersPoints(userId, x, y));
					 count ++;
				 }
				 
			 }

			 
		}
		System.out.println("First couent " + count);
		count = 0;

		//For cells to get live, cause of 3 neighbor
		
		for(int yi = 0; yi < gridHeight; yi++){
			for(int xj = 0; xj < gridWidth; xj++){
				neighbor = 0;
				
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
//							System.out.println("Kaimiòð nr " + neighbor);
//							System.out.println("Kaimiòð ir j=" +xj + " i= " +yi);
//							System.out.println("kam x=" + dx + " y= " + dy);
						}
						
				}

				 // If there is 2 or 3 neighbor, cell stays live
				 if(neighbor == 3){
					 //System.out.println("yes");
					 Boolean inList = false;
					 
					 for(UsersPoints d : NewUsersPointsArray){
						 if(xj == d.getX() && yi == d.getY()){
							 inList = true;
						 }
					 }
					 
					 if(inList == false){
						 NewUsersPointsArray.add(new UsersPoints(userId, xj, yi));
						 count ++;
					 }
					
				 }
			}
		}
		System.out.println("last couent " + count);
		
		System.out.println("OR New lenght = " + NewUsersPointsArray.size());

		return NewUsersPointsArray;

	}
	
}
