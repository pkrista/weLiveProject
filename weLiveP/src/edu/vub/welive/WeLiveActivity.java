package edu.vub.welive;

import java.io.IOException;
import java.util.ArrayList;

import edu.vub.at.IAT;
import edu.vub.at.android.util.IATAndroid;
import edu.vub.at.android.util.IATSettings;
import edu.vub.at.android.util.IATSettings.IATOptions;
import edu.vub.at.exceptions.InterpreterException;
import edu.vub.welive.R;
import edu.vub.welive.interfaces.ATWeLive;
import edu.vub.welive.interfaces.JWeLive;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



public class WeLiveActivity extends ActionBarActivity
implements JWeLive{
	public GridView 	grid;
	private IAT 		iat;
	
	
	// Handle UI and AT in asynchronous way
	public static Handler mHandler;

	//create object ATWeLive
	private static ATWeLive atWLobject;
	public static int myDevID;

	public static final int _MSG_TOUCH_TOUCH_ = 0;
	private static final int _MSG_NEW_GRID_ = 1;
	private static final int _MSG_USERS_COLORS_ = 2;
	private static final int _MSG_GRID_SIZE_ = 3;

	//This is array where are stored userId and index of x and y (touch)
	public static ArrayList<UsersPoints> UsersPointsArray = new ArrayList<UsersPoints>();
//	//This array store User ID and his color
//	public static ArrayList<UsersColors> UsersColorsArray = new ArrayList<UsersColors>();
	public Colors colors = new Colors();
	
	public ArrayList<UsersPoints> NewPointsArray = new ArrayList<UsersPoints>();
	public CalculateNextGen generation = new CalculateNextGen();

	//Start the game progress time and progress bar
	private ProgressDialog progress;
	public int jumpTime; 

	//Coordinator ID
	public int coordinatorId = 0;

	//Grid Height and Width -> for future perspectives to allow coordinator to setup the gird
	public int gridHeight = 10;
	public int gridWidth = 7;

	//Count the users generation
	public int countGeneration = 0;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progress = new ProgressDialog(this);
		open("Starting game weLive");

		//Test new things
		LooperThread lt = new LooperThread();
		lt.start();
		mHandler = lt.mHandler;
		
		//Start up the AmbientTalk code and eval weLive.at file
		new StartIATTask().execute((Void)null);

		//Generate random ID for the device
		Integer random = (int )(Math.random() * 1000 + 1);
		myDevID = random; 

		//set to myself a color
		newUserID(myDevID);

		//When the game starts give user 4 cells
		GridView.bankCell = 4;

		//Grid object
		grid = new GridView(this);
		setContentView(grid);
		
		//Paint the grid
//		grid = new GridView(getApplicationContext(), gridHeight, gridWidth);
//		grid.setBackgroundColor(Color.WHITE);
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
		getMenuInflater().inflate(R.menu.actionbar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu){

		MenuItem generation = menu.findItem(R.id.action_generation);      
		generation.setVisible(false);

		if(coordinatorId == myDevID){           
			generation.setVisible(true);

			//Show short message, to notify user
			Toast.makeText(getApplicationContext(), "You are the Coordinator!", 
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

				//Create the private network
				IATOptions iatOptions = IATSettings.getIATOptions(WeLiveActivity.this);
				iatOptions.networkName_ = "Krista"; //Your network name
				iat = IATAndroid.create(WeLiveActivity.this, iatOptions); 
				iat.evalAndPrint("import /.weLive.weLive.makeWeLive()", System.err);
			} 
			catch (IOException e) {   			
				e.printStackTrace();
			} 
			catch (InterpreterException e) {
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
		this.atWLobject = atWLobject; //AmbientTalk  weLive
		return this;	
	}


	/*
	 * Function get from AT other user placed cells and store them into list
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
		refreshGrid();
	}


	/*
	 * When user appears in game - the color schema is set to him
	 * @see edu.vub.welive.interfaces.JWeLive#newUserID(int)
	 */
	@Override
	public void newUserID(int userId) {
		Colors.UsersColorsArray.add(new UsersColors(userId, colors.getColor()));
		
		int discoveredUsers = Colors.UsersColorsArray.size();
		
		if(discoveredUsers > 2){
			grid.mHeight++;
			grid.mWidth++;
			
			//if I am coordinator then send grid size on changes to all peers
			if(coordinatorId == myDevID){
				sendGridSize();
			}
			
			//refresh the grid
			refreshGrid();
		}
		
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

		invalidateOptionsMenu();
		
//		sendGridArray();
		
		//send Grid and All Users
		sendGridAllUsersColors();	
	}	


	/*
	 * Calculates next generations
	 * after calculations send new grid to AT -> AT further send to other peers
	 */
	public void calculateNextGeneration(){

		this.NewPointsArray = this.generation.nextGeneration(grid.mHeight, grid.mWidth); //CHCHCH //gridHeight, gridWidth
		WeLiveActivity.UsersPointsArray = this.NewPointsArray;

		//To count generation and give user extra cells each 5 generations
		countGeneration();

		//refresh the grid
		refreshGrid();
		
		//send Grid and All Users
		sendGridAllUsersColors();
		
	}
	
	//send Grid and All Users
	public void sendGridAllUsersColors(){
		
		//If I become as coordinator, send my grid to all users
		if(coordinatorId == myDevID){
			//Send all users and it color
			//function to send all stored users and it color to other peers
			sendAllUserColors();
			
			//Send back to AT coordinators Grid
			//and send it to other peers -> everybody will have the same grid as coordinator
			sendGridArray();
		}
	}

	//function to send all stored users and it color to other peers
	public void sendAllUserColors(){
		System.out.println("Send all colors");
		getFPHandler().sendMessage(Message.obtain(getFPHandler(), WeLiveActivity._MSG_USERS_COLORS_, Colors.UsersColorsArray));
	}

	//Send users current Grid (array with user id and touched cells) to other peers
	public void sendGridArray(){
		System.out.println("send grid");
		getFPHandler().sendMessage(Message.obtain(getFPHandler(), WeLiveActivity._MSG_NEW_GRID_, WeLiveActivity.UsersPointsArray));
	}
	
	//Send grid size
	public void sendGridSize(){
		System.out.println("send grid size");
		int [] gridSizeHW = {grid.mHeight, grid.mWidth};
		getFPHandler().sendMessage(Message.obtain(getFPHandler(), WeLiveActivity._MSG_GRID_SIZE_, gridSizeHW));
	}
	
	
	/*
	 * Get the new generation from the coordinator and refresh the grid
	 */
	@Override
	public void newGenerationArray(ArrayList<UsersPoints> usersPointsArray) {

		//Check if the sent grid is not the same as already sent
		//if it is different then count as new generation
		if(!WeLiveActivity.UsersPointsArray.equals(usersPointsArray)){
			//To count generation and give user extra cells each 5 generations
			countGeneration();
		}
		WeLiveActivity.UsersPointsArray = usersPointsArray;

		refreshGrid();
	}

	//Set users color array to new one (sent from coordinator)
	public void newUsersColorArray(ArrayList<UsersColors> NewUsersColorsArray) {
		//Change user color array to the one that coordinator has
		Colors.UsersColorsArray = NewUsersColorsArray;
		
		refreshGrid();
	}
	
	/*
	 * Count generations, every 5 generations give user +4 cells
	 */
	public void countGeneration(){
		//Set that generation is +1
		countGeneration ++;

		//every 5 generation add bank cell + 4 cells
		if((countGeneration % 5) == 0){
			GridView.bankCell = GridView.bankCell + 4;
		}
	}



	

	/*
	 * When start the game run thread that in background will setup
	 * user ID and discover peers and will search for coordinator
	 */
	public void open(String message){
		progress.setMessage(message);
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

						e.printStackTrace();
					}
				}
			}
		};
		t.start();
	}





	/*
	 * Give user color grey if he disconnects
	 * @see edu.vub.welive.interfaces.JWeLive#grayOut(int)
	 */
	@Override
	public void grayOut(int userId) {
		changeUserColor(userId, Color.GRAY);
	}

	/*
	 * Give random color to user if he reconnects back
	 * @see edu.vub.welive.interfaces.JWeLive#colorOn(int)
	 */
	@Override
	public void colorOn(int userId) {
		changeUserColor(userId, colors.getColor());
	}
	
	//Change users color
	public void changeUserColor(int userID, int newColor){
		
		for(UsersColors c: Colors.UsersColorsArray){
			if(c.getUserID() == userID){
				c.setColor(newColor);
			}
		}
		refreshGrid();
	}
	
	/*
	 * Got message from AT that everything is good -> start the game and dismiss the thread
	 * @see edu.vub.welive.interfaces.JWeLive#startGame(int)
	 */
	@Override
	public void startGame() {
		jumpTime = 100;
		progress.dismiss();
	}
	
	
	
	// Call AT methods in a separate thread to not block the UI
	class LooperThread extends Thread {

		public Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				//If the object is null do nothing
				if (null == atWLobject)
					return;
				switch (msg.what) {
					case _MSG_TOUCH_TOUCH_: {
						int[] touchPoint = (int[]) msg.obj;
						atWLobject.touchDetected(touchPoint[0], touchPoint[1]);
						break;
					}
					case _MSG_NEW_GRID_: {
						System.out.println("GEN NEW GRID");
						atWLobject.sendNewGenGrid((ArrayList<UsersPoints>) msg.obj);
						break;
					}
					case _MSG_USERS_COLORS_: {
						System.out.println("All colors");
						atWLobject.sendUsersColors((ArrayList<UsersColors>) msg.obj);
						break;
					}
					case _MSG_GRID_SIZE_: {
						System.out.println("Grid Size");
						int [] gridSizeHW = (int[]) msg.obj;
						atWLobject.sendGridSize(gridSizeHW[0], gridSizeHW[1]);
						break;
					}
				}
			}
		};
		
		public void run() {
			Looper.prepare();
			Looper.loop();
		}
	}
	

    private Handler getFPHandler() {
    	return WeLiveActivity.mHandler;
    }


	@Override
	public void newGridSize(int h, int w) {
		//set new size
		grid.mHeight = h;
		grid.mWidth = w;
		
		refreshGrid();	
	}
	
	//Refresh grid
	public void refreshGrid(){
		grid.postInvalidate();
	}
	
} //end if class
