package edu.vub.welive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



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
	//This array store User ID and his color
	public static ArrayList<UsersColors> UsersColorsArray = new ArrayList<UsersColors>();

	public ArrayList<UsersPoints> NewPointsArray = new ArrayList<UsersPoints>();
	public CalculateNextGen generation = new CalculateNextGen();

	//Start the game progress time and progress bar
	private ProgressDialog progress;
	public int jumpTime; 

	//Coordinator ID
	public int coordinatorId = 0;

	//Grid Height and Width -> for future perspectives to allow coordinator to setup th gird
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

		//Start up the AmbientTalk code and eval weLive.at file
		new StartIATTask().execute((Void)null);

		//Generate random ID for the device
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
		this.atWLobject = atWLobject; //AmbientTalk  we live
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

		//If I become as coordinator, send my grid to all users
		if(coordinatorId == myDevID){
			//Send back to AT coordinators Grid
			atWLobject.sendNewGenGrid(WeLiveActivity.UsersPointsArray);
		}	
	}	


	/*
	 * Calculates next generations
	 * after calculations send new grid to AT -> AT further send to other peers
	 */
	public void calculateNextGeneration(){

		this.NewPointsArray = this.generation.nextGeneration(gridHeight, gridWidth);
		WeLiveActivity.UsersPointsArray = this.NewPointsArray;

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
	 */
	@Override
	public void newGenerationArray(ArrayList<UsersPoints> usersPointsArray) {

		//Check if the sent grid is not the same as already sent
		//if it is different then count as new generation
		if(WeLiveActivity.UsersPointsArray != usersPointsArray){
			//To count generation and give user extra cells each 5 generations
			countGeneration();
		}
		WeLiveActivity.UsersPointsArray = usersPointsArray;

		grid.postInvalidate();
	}

	/*
	 * Count generation, every 5 generation give user +4 cells
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
	 * Got message from AT that everything is good -> start the game and dismiss the thread
	 * @see edu.vub.welive.interfaces.JWeLive#startGame(int)
	 */
	@Override
	public void startGame() {
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

} //end if class
