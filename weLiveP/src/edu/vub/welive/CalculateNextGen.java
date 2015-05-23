package edu.vub.welive;

import java.util.ArrayList;

public class CalculateNextGen {
	
	//This is array where are stored userId and index of x and y (touch)
	public static ArrayList<UsersPoints> NewUsersPointsArray = new ArrayList<UsersPoints>();
	public static ArrayList<UsersPoints> OldUsersPointsArray;
	
	public CalculateNextGen(ArrayList<UsersPoints> UsersPointsArray){
		this.OldUsersPointsArray = UsersPointsArray;
		
		nextGeneration();
	}

	public void nextGeneration() {
		
		int liveCels = OldUsersPointsArray.size();
		
		int x;
		int y;
		int userId;
		int neighbor;
		
		System.out.println("Esmu te = syze is = " + liveCels);
		
		for(UsersPoints p : OldUsersPointsArray){
			 x = p.getX();
			 y = p.getY();
			 userId = p.getUserID();
			 neighbor = 0;
			 
			 for(UsersPoints t : OldUsersPointsArray){
				 if((t.getX() == x-1 && (t.getY() == y-1 || t.getY() == y || t.getY() == y+1)) ||
					(t.getX() == x+1 && (t.getY() == y-1 || t.getY() == y || t.getY() == y+1)) ||
					(t.getY() == y-1 && (t.getX() == x-1 || t.getX() == x || t.getX() == x+1)) ||
					(t.getY() == y+1 && (t.getX() == x-1 || t.getX() == x || t.getX() == x+1))){
					 neighbor++;
					 
				 }
			 }
			 
			 // If there is 2 or 3 neighbor, cell stays live
			 if(neighbor == 2 || neighbor ==3){
				 NewUsersPointsArray.add(new UsersPoints(userId, x, y));
			 }
	 
		}
		
		//WeLiveActivity.UsersPointsArray = (ArrayList<UsersPoints>)NewUsersPointsArray.clone();
		WeLiveActivity.UsersPointsArray.clear();
		
		for(UsersPoints a : NewUsersPointsArray){
			WeLiveActivity.UsersPointsArray.add(a);
		}
		
		System.out.println("New lenght = " + WeLiveActivity.UsersPointsArray.size());
		System.out.println("OR New lenght = " + NewUsersPointsArray.size());
		//WeLiveActivity.UsersPointsArray.addAll(NewUsersPointsArray);
		//WeLiveActivity.UsersPointsArray = new ArrayList<UsersPoints>(NewUsersPointsArray);

	}

}
