package edu.vub.welive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;


public class GridView extends View {
	
	private final WeLiveActivity weLiveActivity;
	public Colors colors = new Colors();
	
	private Context mContext;
	private Paint mPaint;
	
	public int mHeight;
	public int mWidth;
	
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
	
	public static int userColor;

	public GridView(WeLiveActivity weLiveActivity){
		super(weLiveActivity);
		this.weLiveActivity = weLiveActivity;
		
		mPaint 			= new Paint(Color.BLACK);
		mHeight			= 10; //height;
		mWidth			= 7; //width;
		mSize 			= 10 * 10; //mHeight * mHeight;

//		downBound = mSize * mHeight + (mHeight * 5);
//		rightBound = mSize * mWidth + (mWidth * 5);

	}


	/*
	 * Function that draws the grid with colored cells
	 * User color, cell bank and score
	 */
	@Override
	public void onDraw(Canvas mCanvas){
		
		//refresh the action bar
		weLiveActivity.refreshActionBar();
		
		downBound = mSize * mHeight + (mHeight * 5);
		rightBound = mSize * mWidth + (mWidth * 5);
		
		for(int i = 0; i < mWidth; i++) { //mWidth
			for(int j = 0; j < mHeight; j++) { //mHeight		    	
				int left = i * (mSize + 5);
				int top = j * (mSize + 5);
				int right = left + mSize;
				int bottom = top + mSize;

				//Set color back to black
				mPaint.setColor(Color.BLACK);

//				for(UsersPoints p : WeLiveActivity.UsersPointsArray){
//					if( p.getX() == i && p.getY() == j){
//
//						boolean userHaveColour = false;
//
//						for(UsersColors c : Colors.UsersColorsArray){
//							if(c.getUserID() == p.getUserID()){
//								mPaint.setColor(c.getColor());
//								userHaveColour = true;
//								break;
//							}
//						}
//
//						if(userHaveColour == false){
//							//user do not have color
//							mPaint.setColor(Color.GRAY);
//						}
//					}
//				}
				
				for(UsersPoints p : WeLiveActivity.UsersPointsArray){
					if( p.getX() == i && p.getY() == j){
						for(UsersColors c : Colors.UsersColorsArray){
							
							if(c.getUserID() == p.getUserID()){
								
								if(c.getisColored()){
									mPaint.setColor(c.getColor());
								}
								else{
									mPaint.setColor(Color.GRAY);
								}
							}
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
//		String userInfo = "Cells: " + GridView.bankCell + " | Score: " + GridView.userScore;
//		mCanvas.drawText(userInfo, 50 , yy , mPaint);

		//Draw rectangle with user color
		int left = 10;
		int top = yy - 30;
		int right = left + 30;
		int bottom = top + 30;

		for(UsersColors c : colors.UsersColorsArray){
			if(c.getUserID() == WeLiveActivity.myDevID){
				mPaint.setColor(c.getColor());
				userColor = c.getColor();
			}
		}
//		mCanvas.drawRect(new Rect(left, top, right, bottom), mPaint);
	}


	/*
	 * On touch event
	 * get touch coordinates send to AT and set them into UserPoints List
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

			//Calculate the index of the placed cell
			int rowIndex = (int) Math.floor(motionX / (mSize +5));
			int columnIndex = (int) Math.floor(motionY/ (mSize +5));

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
//				WeLiveActivity.atWLobject.touchDetected(rowIndex, columnIndex);
				
				
				//New asynchronous way
				// send start point to ambientTalk layer.
		        int [] touchPoint = {rowIndex, columnIndex};
		        getFPHandler().sendMessage(Message.obtain(getFPHandler(), weLiveActivity._MSG_TOUCH_TOUCH_, touchPoint));
				//Calculate how many cells he can put on the grid
				calculateCellBank();
			}

			//refresh the view
			postInvalidate();

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


    private Handler getFPHandler() {
    	return weLiveActivity.mHandler;
    }
    
}
