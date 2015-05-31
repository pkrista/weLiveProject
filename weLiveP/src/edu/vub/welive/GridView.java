package edu.vub.welive;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GridView extends View {
	private Context mContext;
	private Paint mPaint;
	private int mHeight;
	private int mWidth;
	private int mSize;

	//For calculate cell index
	private int x = 10;
	private int y = 10;

	//To calculate bounds
	private int downBound;
	private int rightBound;

	//Cell bank and user score
	public static int bankCell;
	public static int userScore;


	public GridView(Context mContext,int height,int width){
		super(mContext);
		this.mContext 	= mContext;
		mPaint 			= new Paint(Color.BLACK);
		mHeight			= height;
		mWidth			= width;
		mSize 			= mHeight * mHeight;

		downBound = mSize * mHeight + (mHeight * 5);
		rightBound = mSize * mWidth + (mWidth * 5);
	
	}


	/*
	 * 
	 * 
	 * 
	 */
	@Override
	public void onDraw(Canvas mCanvas){

		for(int i = 0; i < mWidth; i++) { //mWidth
			for(int j = 0; j < mHeight; j++) { //mHeight		    	
				int left = i * (mSize + 5);
				int top = j * (mSize + 5);
				int right = left + mSize;
				int bottom = top + mSize;
				
				//Set colour back to black
				mPaint.setColor(Color.BLACK);
				
				for(UsersPoints p : WeLiveActivity.UsersPointsArray){
					if( p.getX() == i && p.getY() == j){
						
						boolean userHaveColour = false;
						
						for(UsersColors c : WeLiveActivity.UsersColorsArray){
							if(c.getUserID() == p.getUserID()){
								mPaint.setColor(c.getColor());
								userHaveColour = true;
								break;
							}
						}
						
						if(userHaveColour == false){
							//user do not have color
							mPaint.setColor(Color.GRAY);
						}
					}
				}

				mCanvas.drawRect(new Rect(left, top, right, bottom), mPaint);	    	
			}
		}
		
		//set color back to black
		mPaint.setColor(Color.BLACK);
		
		//Add text at the bottom of the grid
		//Score and cell bank
		int yy = mSize * mHeight  + mSize;
		mPaint.setTextSize(30);
		
		//Recalculate score
		calculateScore();
		
		//Make a string to print on the screen
		String userInfo = "Cells: " + GridView.bankCell + " | Score: " + GridView.userScore;
		mCanvas.drawText(userInfo, 50 , yy , mPaint);
		
		//Draw rectangle with user color
		int left = 10;
		int top = yy - 30;
		int right = left + 30;
		int bottom = top + 30;
		
		for(UsersColors c : WeLiveActivity.UsersColorsArray){
			if(c.getUserID() == WeLiveActivity.myDevID){
				mPaint.setColor(c.getColor());
			}
		}
		
		mCanvas.drawRect(new Rect(left, top, right, bottom), mPaint);
	
	}


	/*
	 * 
	 * On touch event
	 * get touch coordinates  send to AT and set them into UserPoints List
	 */
	public boolean onTouchEvent(MotionEvent event) {
		int motionX = (int) event.getX();
		int motionY = (int) event.getY();

		if ((event.getAction() == MotionEvent.ACTION_DOWN) &&
			((motionX < rightBound && motionY < downBound) 	&& 
			(GridView.bankCell > 0)))
		{

			x = motionX;
			y = motionY;

			int rowIndex = (int) Math.floor(motionX / (mSize +5));
			int columnIndex = (int) Math.floor(motionY/ (mSize +5));

//			if((motionX < rightBound && motionY < downBound)
//					&& (GridView.bankCell > 0)){


				//put my touch point into UsersPointsArray
				//Check if the pint did not already exists
				boolean exists = false;
				for(UsersPoints p:  WeLiveActivity.UsersPointsArray){

					int px = p.getX();
					int py = p.getY();

					if(px == rowIndex && py == columnIndex){
						exists = true;
					}
				}
				if(!exists){
					WeLiveActivity.UsersPointsArray.add(new UsersPoints(WeLiveActivity.myDevID, rowIndex, columnIndex));
					//send x and y data to AT
					WeLiveActivity.atWLobject.touchDetected(rowIndex, columnIndex);
				
					//Calculate how many cells he can put on the grid
					calculateCellBank();
				}

				//refresh the view
				postInvalidate();
//			}

			return true;
		}
		else{

			return false;
		}		
	}


	/*
	 * Calculate how many cells user owns in the grid
	 */
	public static void calculateScore(){
		int countMyScore = 0;
		int userID;
		for(UsersPoints p : WeLiveActivity.UsersPointsArray){
			userID = p.getUserID();
			if(userID == WeLiveActivity.myDevID){
				countMyScore++;
			}
		}
		GridView.userScore = countMyScore;
	}

	/*
	 * Calculate how many cells user have in the bank
	 */
	public static void calculateCellBank(){
		GridView.bankCell --;
	}


}
