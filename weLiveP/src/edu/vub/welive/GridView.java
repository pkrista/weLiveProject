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
	public Board board = new Board();
	
	private Context mContext;
	private Paint mPaint;
	
	public static int mHeight;
	public static int mWidth;
	
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
		mHeight			= 10; //height default;
		mWidth			= 7; //width default;
		mSize 			= 10 * 10; 
	}


	/*
	 * Function that draws the grid with colored cells
	 * User color, cell bank and score
	 */
	@Override
	public void onDraw(Canvas mCanvas){
			
		userColor = colors.findColor(WeLiveActivity.myDevID); //set the user color
		
		downBound = mSize * mHeight + (mHeight * 5);
		rightBound = mSize * mWidth + (mWidth * 5);
		
		for(int i = 0; i < mWidth; i++) { //mWidth
			for(int j = 0; j < mHeight; j++) { //mHeight		    	
				int left = i * (mSize + 5);
				int top = j * (mSize + 5);
				int right = left + mSize;
				int bottom = top + mSize;

				//Set color back to black
//				mPaint.setColor(Color.BLACK);
				
				//Check if cell is live
				//if cell is live give the right color for it
				if(board.cellExists(i, j)){
					//Find owner
					int owner = board.findCellOwner(i, j);
					//Find color
					mPaint.setColor(colors.findColor(owner));
				}
				
				mCanvas.drawRect(new Rect(left, top, right, bottom), mPaint);	 
				//set color back to black
				mPaint.setColor(Color.BLACK);
			}
		}
		
		//Recalculate score
		board.calculateScore();
		
		//refresh the action bar
		weLiveActivity.refreshActionBar();
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
			
			//Check if the point did not already exists
			//put my touch point into UsersPointsArray
			if(!board.cellExists(rowIndex, columnIndex)){
				//Store and send new cell 
				storeAndSendCell(rowIndex, columnIndex);
			}

			//refresh the view
			postInvalidate();

			return true;
		}
		else{
			return false;
		}		
	}

	public void storeAndSendCell(int rowIndex, int columnIndex){
		//Store placed cell
		board.storeCell(WeLiveActivity.myDevID, rowIndex, columnIndex);
		
		//send placed cell to AT
		sendPlacedCell(rowIndex, columnIndex);
		
		//Calculate how many cells he can put on the grid
		calculateCellBank();
	}

	public void sendPlacedCell(int rowIndex, int columnIndex){
		//send x and y data to AT
        int [] touchPoint = {rowIndex, columnIndex};
        getFPHandler().sendMessage(Message.obtain(getFPHandler(), WeLiveActivity._MSG_TOUCH_TOUCH_, touchPoint));
	}
	


	/*
	 * Calculate how many cells user have in the bank
	 * 
	 */
	public static void calculateCellBank(){
		GridView.bankCell--;
	}


    private Handler getFPHandler() {
    	return WeLiveActivity.mHandler;
    }


	public static int getmHeight() {
		return mHeight;
	}


	public static void setmHeight(int mHeight) {
		GridView.mHeight = mHeight;
	}


	public static int getmWidth() {
		return mWidth;
	}


	public static void setmWidth(int mWidth) {
		GridView.mWidth = mWidth;
	}
    
    
    
    
//	public int findCellOwner(int i, int j){
//	int owner = 0;
//	for(UsersPoints p : Board.UsersPointsArray){
//		if( p.getX() == i && p.getY() == j){					
//			owner = p.getUserID();
//		}
//	}
//	return owner;
//}

////Find information about user (id,color,isColored)
//public UsersColors findUserInfo(int id){
//	
//	for(UsersColors c : Colors.UsersColorsArray){
//		if(c.getUserID() == id){
//			return c;
//		}
//	}
//	return null;
//}

//public int findColor(int id){
//	//find UserInfo
//	UserInfo userInfo = colors.findUserInfo(id);
//	
////	Colors.findUserInfo(id);
//	
//	//Return User color
//	if(userInfo.getisColored()){
//		return userInfo.getColor();
//	}
//	else{
//		return Color.GRAY;
//	}
//}
    
//	/*
//	 * Calculate how many cells user owns in the grid
//	 */
//	public static void calculateScore(){
//		GridView.userScore = 0;
//		for(UsersPoints p : Board.UsersPointsArray){
//			if(p.getUserID() == WeLiveActivity.myDevID){
//				GridView.userScore++;
//			}
//		}
//	}
    
}
