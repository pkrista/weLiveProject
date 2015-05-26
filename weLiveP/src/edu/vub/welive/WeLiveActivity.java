package edu.vub.welive;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
//for getting devices Id
import android.telephony.TelephonyManager;                                           
import android.content.Context;   
import android.drm.DrmStore.Action;



public class WeLiveActivity extends ActionBarActivity
implements JWeLive{
	public GridView 	grid;
	private IAT 		iat;
	private Handler 	mHandler;

	public static ATWeLive atWLobject;
	public static int myDevID;

	private static final int _ASSET_INSTALLER_ = 0;
	public static final int _MSG_TOUCH_START_ = 0;

	//This is array where are stored userId and index of x and y (touch)
	public static ArrayList<UsersPoints> UsersPointsArray = new ArrayList<UsersPoints>();
	//
	public static ArrayList<UsersColors> UsersColorsArray = new ArrayList<UsersColors>();

	public ArrayList<UsersPoints> testList = new ArrayList<UsersPoints>();
	public CalculateNextGen generation = new CalculateNextGen();
	
	
	private ProgressDialog progress;

	public int jumpTime; 

	//Coordinator ID
	public int coordinatorId = 0;

	public Menu myMenu;

	//Grid Height and Width -> for future perspectives to allow coordinator to setup th gird
	public int gridHeight = 10;
	public int gridWidth = 7;

	
	//Count the users generation
	public int countGeneration = 0;
	
	static Menu mMenu;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progress = new ProgressDialog(this);
		open();

		//Start up the AmbientTalk code and eval weLive.at file
		new StartIATTask().execute((Void)null);
		//Spawn loop handling messages to AmbientTalk
		//		LooperThread lt = new LooperThread();
		//		lt.start();
		//		mHandler = lt.mHandler;

		//
		//get the ID for my device
		//
		Integer random = (int )(Math.random() * 1000 + 1);
		myDevID = random; 

		//set to myself a color
		newUserID(myDevID);
		
		//When the game starts give user 4 cells
		GridView.bankCell = 4;
		
		//Paint the grid
		grid = new GridView(getApplicationContext(), gridHeight, gridWidth);
		grid.setBackgroundColor(Color.WHITE);
		setContentView(grid);

		//set back to home arrow
		getActionBar().setDisplayHomeAsUpEnabled(true);

	
	}


	/*
	 * Action Bar things 
	 * onCreateOptionsMenu() - set up the original action bar
	 * onPrepareOptionsMenu() - check which buttons belong to user
	 * onOptionsItemSelected() - on click on the item in the action bar
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.mMenu = menu;
		getMenuInflater().inflate(R.menu.actionbar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu){
		
		MenuItem generation = menu.findItem(R.id.action_generation);      
		generation.setVisible(false);
		
		if(coordinatorId == myDevID){           
			generation.setVisible(true);
			
			//Show shoer message, to notify user
			Toast.makeText(getApplicationContext(), "You are the new Coordinator!", 
					Toast.LENGTH_LONG).show();
		}
		else {
			generation.setVisible(false);
		}

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_generation:
			//Calculate new generation
			calculateNextGeneration();
			return true;
		case R.id.action_stop:
			//To exit the application
			
			finish();
			
	    	android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}



	/*
	 * Starts up the AmbientTalk interpreter and interprets the code provided in 
	 * assets/atlib/weLive/weLive.at
	 */
	public class StartIATTask extends AsyncTask<Void,String,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {   			
				//
				//Create private network
				//
				IATOptions iatOptions = IATSettings.getIATOptions(WeLiveActivity.this);
				iatOptions.networkName_ = "KristaNet"; //Your network name
				iat = IATAndroid.create(WeLiveActivity.this, iatOptions); 
				iat.evalAndPrint("import /.weLive.weLive.makeWeLive()", System.err);
			} catch (IOException e) {   			
				e.printStackTrace();
			} catch (InterpreterException e) {
				Log.e("AmbientTalk","Could not start IAT",e);
			}
			return null;
		}
	}


	/*
	 * Function that allows AmbientTalk talk with Java(non-Javadoc)
	 * @see edu.vub.welive.interfaces.JWeLive#registerATApp(edu.vub.welive.interfaces.ATWeLive)
	 */
	public JWeLive registerATApp(ATWeLive atWLobject) {
		atWLobject.myId(myDevID);
		this.atWLobject = atWLobject; //AmbientTalk  we live
		return this;	
	}

	
	/*
	 * 
	 * (non-Javadoc)
	 * @see edu.vub.welive.interfaces.JWeLive#funcNewPutValues(int, int, int)
	 */
	@Override
	public void funcNewPutValues(int userId, int touchPointX, int touchPointY) {
		
		//Check if in the list already do not exist exactly the same point
	    boolean exists = false;
	    for(UsersPoints p:  WeLiveActivity.UsersPointsArray){
	    	
	    	int px = p.getX();
	    	int py = p.getY();
	    	
	    	if(px == touchPointX && py == touchPointY){
	    		exists = true;
	    		break;
	    	}
	    }
	    if(!exists){
	    	WeLiveActivity.UsersPointsArray.add(new UsersPoints(userId, touchPointX, touchPointY));
	    }
	    
	    //refresh the grid
		grid.postInvalidate();
	}



	/*
	 * When user appears in game - the color schema is set to him
	 * @see edu.vub.welive.interfaces.JWeLive#newUserID(int)
	 */
	@Override
	public void newUserID(int userId) {
		UsersColorsArray.add(new UsersColors(userId, getColor()));
	}


	/*
	 * List of colors and get random color for user
	 * 
	 */
	public int getColor(){	
		int[] color ={
				Color.BLUE,  	//-16776961, //blue
				Color.GREEN, 	//-16711936, //green
				Color.CYAN,  	//-16711681, //Cyan
				Color.RED,   	// -65536,   //red
				Color.YELLOW,	// -256      //yellow
				Color.MAGENTA	// -65281    //magenta
		};			
		int theColor = Math.abs(new Random().nextInt()) % color.length;
		return color[theColor];
	}


	/*
	 * Give button for coordinator to do next generation
	 * Get coordinator ID from AT:
	 * IF the coordinator ID is the same as myDeviceID then Give me the Next Generation button
	 * @see edu.vub.welive.interfaces.JWeLive#sendCoordinatorId(int)
	 */
	@Override
	public void sendCoordinatorId(int coorId) {
		coordinatorId = coorId;		

		System.out.println("Java get coordinatir ID " + coorId);
		
		invalidateOptionsMenu();
		
		if(coordinatorId == myDevID){
			
		}
//		
//		if (Build.VERSION.SDK_INT >= 11 && coordinatorId == myDevID){
//			invalidateOptionsMenu();
//		}
//		else{
//			myMenu.clear();
//			onCreateOptionsMenu(myMenu);
//		}
		
	}	

	/*
	 * 
	 */
	public void calculateNextGeneration(){
		System.out.println("Start calculatins");

		this.testList = this.generation.nextGeneration(gridHeight, gridWidth);
		WeLiveActivity.UsersPointsArray = this.testList;
		
		//To count generation and give user extra cells each 5 generations
		countGeneration();
		
		//refresh the grid
		grid.postInvalidate();
		
		//Send new grid to all peers
		//send new Generated list with all new calculated points
		WeLiveActivity.atWLobject.sendNewGenGrid(WeLiveActivity.UsersPointsArray);
		
	}
	


	/*
	 * Get the new generation from the coordinator and refresh the grid
	 * 
	 */
	@Override
	public void newGenerationArray(ArrayList<UsersPoints> usersPointsArray) {
		
		System.out.println("I get the list that coordinator sent PS JAVA");
		WeLiveActivity.UsersPointsArray = usersPointsArray;
		
		//To count generation and give user extra cells each 5 generations
		countGeneration();
				
		grid.postInvalidate();
	}
	
	public void countGeneration(){
		//Set that generation is +1
		countGeneration ++;
		
		//every 5 generation add bank cell + 4 cells
		if((countGeneration % 4) == 0){
			GridView.bankCell = GridView.bankCell +4;
		}
		
	}

	
	
	/*
	 * When start the game run thread that in background will setup
	 * user ID and discover peers and will search for coordinator
	 */
	public void open(){
		progress.setMessage("Starting game weLive nr 7");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.setCanceledOnTouchOutside(false);
		progress.show();

		final int totalProgressTime = 100;

		final Thread t = new Thread(){

			@Override
			public void run(){

				jumpTime = 0;
				while(jumpTime < totalProgressTime){
					try {
						sleep(200);
						jumpTime += 5;
						progress.setProgress(jumpTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
	}


	/*
	 * Got message from AT that everything is good -> start the game and dismiss the thread
	 * @see edu.vub.welive.interfaces.JWeLive#startGame(int)
	 */
	@Override
	public void startGame(int userId) {
		jumpTime = 100;
		progress.dismiss();
	}


	/*
	 * Give user color grey of he is disconnected
	 * @see edu.vub.welive.interfaces.JWeLive#grayOut(int)
	 */
	@Override
	public void grayOut(int userId) {

		for(UsersColors c: WeLiveActivity.UsersColorsArray){
			if(c.getUserID() == userId){
				c.setColor(Color.GRAY);
			}
		}
		grid.postInvalidate();
	}

	/*
	 * Give random color to user if he reconnect back
	 * @see edu.vub.welive.interfaces.JWeLive#colorOn(int)
	 */
	@Override
	public void colorOn(int userId) {

		for(UsersColors c: WeLiveActivity.UsersColorsArray){
			if(c.getUserID() == userId){
				c.setColor(getColor());
			}
		}
		grid.postInvalidate();
	}
	
	
	/*
	 * 
	 * 
	 */
	
//	public void redrawCanvas() {
//		System.out.println("WeLive " + " redrawCanvas ");
//		grid.postInvalidate();
//	}
//	public void redrawActionBar() {
//		System.out.println("WeLive " + " redraw ActionBar ");
//		invalidateOptionsMenu();
//	}
	
}
