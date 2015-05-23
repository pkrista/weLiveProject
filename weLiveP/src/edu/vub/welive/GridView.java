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

public class GridView extends View {
	private Context mContext;
	private Paint mPaint;
	private int mHeight;
	private int mWidth;
	private int mSize;
	private int x = 10;
	private int y = 10;
	//
//	public HashMap<Integer, Point> hash;


	public GridView(Context mContext,int height,int width){
		super(mContext);
		this.mContext 	= mContext;
		mPaint 			= new Paint(Color.BLACK);
		mHeight			= height;
		mWidth			= width;
		mSize 			= mHeight * mWidth;
		
	}

	
	@Override
	public void onDraw(Canvas mCanvas){

		//System.out.println(WeLiveActivity.UsersColorsArray.toString());
		System.out.println(WeLiveActivity.UsersPointsArray.toString());

		
		for(int i = 0; i < mWidth; i++) {
		    for(int j = 0; j < mHeight; j++) {			    	
		    	int left = i * (mSize + 5);
		    	int top = j * (mSize + 5);
		    	int right = left + mSize;
		    	int bottom = top + mSize;
		    	
				for(UsersPoints p : WeLiveActivity.UsersPointsArray){
					if( p.getX() == i && p.getY() == j){
						
						for(UsersColors c : WeLiveActivity.UsersColorsArray){
							if(c.getUserID() == p.getUserID()){
								mPaint.setColor(c.getColor());
							}
						}
					}
				}
		    	
				mCanvas.drawRect(new Rect(left, top, right, bottom), mPaint);
				mPaint.setColor(Color.BLACK);
//		    	if(x >= left && x <= right && y <= bottom && y >= top){
//		    		mPaint.setColor(Color.YELLOW);
//		    		mCanvas.drawRect(new Rect(left, top, right, bottom), mPaint);
//		    		mPaint.setColor(Color.BLACK);
//		    		
//		    		//System.out.println("col me = " + i + " row me = " + j);
//		    	}
//		    	else{
//		    		mCanvas.drawRect(new Rect(left, top, right, bottom), mPaint);
//		    		
//		    		//System.out.println("col = " + i + " row = " + j);
//		    	}
//		    	
		    }
		}

	}
	
	//
	
	
	//On touch event
	//get touch coordinates and paint squere in colour
	public boolean onTouchEvent(MotionEvent event) {
		  int motionX = (int) event.getX();
		  int motionY = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        	//System.out.println(event.getAction());
        	
        	x = motionX;
        	y = motionY;

        	
//        	double rowIndex = (motionX / (mSize +5));  //motionY / mSize+5 % mHeight;
//        	double columnIndex = (motionY/ (mSize +5)); //motionX / mSize+5 % mWidth;
    	    
    	    int rowIndex = (int) Math.floor(motionX / (mSize +5));
    	    int columnIndex = (int) Math.floor(motionY/ (mSize +5));
    	    
//    	    //Make point out of this indexes
//    	    Point touchPoint = new Point(rowIndex, columnIndex);
    	    
    	    //System.out.println("Index: x =" + touchPoint.x + " y=" + touchPoint.y);
    	    //System.out.println("Coordinates: x =" + motionX + " y=" + motionY);
    	    
    	    
    	    //send x and y data to AT
    	    WeLiveActivity.atWLobject.touchDetected(rowIndex, columnIndex);
    	    //MainActivity.atWLobject.touchDetected(touchPoint);
    	    
    	    //put my touch point into UsersPointsArray
    	    WeLiveActivity.UsersPointsArray.add(new UsersPoints(WeLiveActivity.myDevID, rowIndex, columnIndex));
    	    
    	    postInvalidate();

        	return true;
        }
        else{
        	return false;
        }		
	}

	private void drawRect(Rect rect, Paint mPaint2) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
