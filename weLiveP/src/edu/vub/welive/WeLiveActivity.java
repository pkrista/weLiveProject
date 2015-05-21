package edu.vub.welive;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import edu.vub.at.IAT;
import edu.vub.at.android.util.IATAndroid;
import edu.vub.at.android.util.IATSettings;
import edu.vub.at.android.util.IATSettings.IATOptions;
import edu.vub.at.exceptions.InterpreterException;
import edu.vub.welive.R;
import edu.vub.welive.interfaces.ATWeLive;
import edu.vub.welive.interfaces.JWeLive;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//for getting devices Id
import android.telephony.TelephonyManager;                                           
import android.content.Context;   



public class WeLiveActivity extends ActionBarActivity
							implements JWeLive{
	public GridView 	grid;
	private IAT 		iat;
	private Handler 	mHandler;

	public static ATWeLive atWLobject;
	public int devID;
	
	private static final int _ASSET_INSTALLER_ = 0;
	public static final int _MSG_TOUCH_START_ = 0;
	
	//This is array
	public ArrayList<UsersPoints> UsersPointsArray = new ArrayList<UsersPoints>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid = new GridView(getApplicationContext(),10,10);
        grid.setBackgroundColor(Color.WHITE);
        setContentView(grid);
//        //Copy AmbientTalk files to the SD card <-- moved to first activity
//        Intent i = new Intent(this, weLiveAssetInstaller.class);
//    	startActivityForResult(i,0);
        
    	//Start up the AmbientTalk code and eval weLive.at file
    	new StartIATTask().execute((Void)null);
    	//Spawn loop handling messages to AmbientTalk
//		LooperThread lt = new LooperThread();
//		lt.start();
//		mHandler = lt.mHandler;
		
		//
		//get devices unique id, if it is empty then set random number
		//
		TelephonyManager  tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		//if(tm.getDeviceId() != null){
		//	devID = tm.getDeviceId();
		//}
		//else{
			Integer random = (int )(Math.random() * 1000 + 1);
			devID =random; 
		//}  
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //
    public void touchDetected(int x, int y){
    	atWLobject.touchDetected(x, y);
    	//atWLobject.touchDetected(touchPointX, );
    }
    
    // Starts up the AmbientTalk interpreter and interprets the code provided in assets/atlib/weLive/weLive.at
    public class StartIATTask extends AsyncTask<Void,String,Void> {

    	@Override
    	protected Void doInBackground(Void... params) {
    		try {   			
    			//
    			//Create private network
    			//
    			IATOptions iatOptions = IATSettings.getIATOptions(WeLiveActivity.this);
    			iatOptions.networkName_ = "test"; //Your network name
    			iat = IATAndroid.create(WeLiveActivity.this, iatOptions); //iat = IATAndroid.create(MainActivity.this);
    			iat.evalAndPrint("import /.weLive.weLive.makeWeLive()", System.err);
    		} catch (IOException e) {   			
    			e.printStackTrace();
    		} catch (InterpreterException e) {
    			Log.e("AmbientTalk","Could not start IAT",e);
    		}
    		return null;
    	}
    }

	public void redrawCanvas() {
		System.out.println("WeLive" + "redrawCanvas");
		grid.postInvalidate();
	}
		
		
		//Function that allows AmbientTalk talk with Java
		public JWeLive registerATApp(ATWeLive atWLobject) {
			atWLobject.myId(devID);
			this.atWLobject = atWLobject; //AmbientTalk  we live
			return this;	
		}

		@Override
		public void funcNewPutValues(int userId, int touchPointX, int touchPointY) {
			System.out.println("Putting userID and points into array");
			System.out.println("useerID" + userId + " points  x= " + touchPointX + "  y = " + touchPointY);
			
			UsersPointsArray.add(new UsersPoints(userId, touchPointX, touchPointY));
			System.out.println(UsersPointsArray.toString());
			
		}
		
}
