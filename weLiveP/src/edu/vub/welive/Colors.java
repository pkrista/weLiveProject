package edu.vub.welive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import android.graphics.Color;

public class Colors implements Serializable{

	//This array store User ID and his color
	public static ArrayList<UsersColors> UsersColorsArray = new ArrayList<UsersColors>();
	
	public int getColor() {
		
		//Give every user different color
		int userColor = 0;
		boolean  colorSetToOther = true;

		while(colorSetToOther == true){
			colorSetToOther = false;

			int randomInt = Math.abs(new Random().nextInt()) % color.length;
			userColor = color[randomInt];
			for(UsersColors c: UsersColorsArray){
				if(c.getColor() == color[randomInt]){
					colorSetToOther = true;
					//Call function again
				}
			}
		}

		return userColor;

	}

	int[] color = {
			//http://www.rapidtables.com/web/color/RGB_Color.htm
			Color.rgb(0,0,255),  	//Blue
			Color.rgb(0,128,0), 	//Green
			Color.rgb(0,255,255),  	//Cyan
			Color.rgb(255,0,0),  	//Red
			Color.rgb(255,255,0),	//Yellow
			Color.rgb(255,0,255),	//Magenta
			
			Color.rgb(128,0,0), 	//Maroon
			Color.rgb(0,255,0), 	//Lime
			Color.rgb(128,128,0),	//Olive
			Color.rgb(128,0,128), 	//Purple
			Color.rgb(0,128,128),	//Teal
			Color.rgb(0,0,128), 	//Navy
			Color.rgb(220,20,60),	//Crimson
			Color.rgb(255,127,80),	//Coral
			Color.rgb(184,134,11),	//Dark golden rod
			Color.rgb(144,238,144),	//Light green
			Color.rgb(46,139,87), 	//Sea green
			Color.rgb(32,178,170),	//Light sea green
			Color.rgb(138,43,226),	//Blue violet
			Color.rgb(139,69,19)	//Saddle brown
	};
	
	
}

