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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;



public class WeLiveActivity extends ActionBarActivity
implements JWeLive{
	public GridView 	grid;
	private IAT 		iat;

	// Create object ATWeLive
	private static ATWeLive atWLobject;
	
	//DevceId, Cell bank, user score and color
	public static int myDevID;
	public static int myCellBank;
	public static int myScore;
	public static int myColor;
	
	
	// Handle UI and AT in asynchronous way
	public static Handler mHandler;
	// Messages
	public static final int _MSG_TOUCH_TOUCH_ = 0;
	private static final int _MSG_NEW_GRID_ = 1;
	private static final int _MSG_USER_INFO_ = 2;
	private static final int _MSG_GRID_SIZE_ = 3;

	public Colors colors = new Colors();
	public Board board = new Board();

	// Start the game progress time and progress bar
	private ProgressDialog progress;
	public int jumpTime; 
	

	// Coordinator ID
	public int coordinatorId = 0;

	// Grid Height and Width default size
	public int gridHeight = 10;
	public int gridWidth = 7;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Grid object
		grid = new GridView(this);
		setContentView(grid);

		progress = new ProgressDialog(this);
		open("Starting game weLive");

		LooperThread lt = new LooperThread();
		lt.start();
		mHandler = lt.mHandler;

		//Start up the AmbientTalk code and eval weLive.at file
		new StartIATTask().execute((Void)null);

		//Generate random ID for the device
		Integer random = (int )(Math.random() * 1000 + 1);
		myDevID = random; 
		//store myself into the Users and Colors array
		storeUser(myDevID);

		//When the game starts give user 4 cells, set color and score
		myCellBank = 4;
		myColor = colors.findColor(WeLiveActivity.myDevID); //set the user color
		myScore = 0;
		
	}


	/*
	 * Action Bar things 
	 * 
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

		showUserScore(menu);
		showUserCellBank(menu);
		showUserColor();


		MenuItem generation = menu.findItem(R.id.action_generation);      
		generation.setVisible(false);

		if(coordinatorId == myDevID){           
			generation.setVisible(true);
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
			board.calculateNextGeneration();
			//refresh the grid
			refreshGrid();
			//send Grid and All Users
			sendGridAllUsersColors();
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

	public void showUserColor(){

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.lucky_color, null);
		ImageView iv = (ImageView) v.findViewById(R.id.lucky_color_view);
		//Set background as user color
		iv.setBackgroundColor(myColor);
		actionBar.setCustomView(v);
	}

	public void showUserScore(Menu menu){
		MenuItem score = menu.findItem(R.id.action_score); 
		score.setTitle("Score:"+myScore);
	}

	public void showUserCellBank(Menu menu){
		MenuItem bank = menu.findItem(R.id.action_bank); 
		bank.setTitle("Cells:"+myCellBank);
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
	 * Function dismiss the thread "open"
	 * @see edu.vub.welive.interfaces.JWeLive#startGame(int)
	 */
	@Override
	public void startGame() {
		jumpTime = 100;
		progress.dismiss();

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



	/* From AT
	 * Function get from AT other user placed cells and store them into list
	 * @see edu.vub.welive.interfaces.JWeLive#funcNewPutValues(int, int, int)
	 */
	@Override
	public void storePlacedCell(int userId, int touchPointX, int touchPointY) {
		if(!board.cellExists(touchPointX, touchPointY)){
			//store the cell into the list UsersPointsArray
			board.storeCell(userId,touchPointX,touchPointY);
			//refresh grid
			refreshGrid();
		}
	}


	/* From AT
	 * When user appears in game - the color schema is set to him
	 * @see edu.vub.welive.interfaces.JWeLive#newUserID(int)
	 */
	@Override
	public void storeUser(int userId) {
		//Add user to Users information array (id, color, isGrayOut)
		Colors.UsersArray.add(new UserInfo(userId, colors.getColor(), false));

		//Resize grid
		resizeGrid();
	}


	/* From AT
	 * Function set the Coordinator
	 * and send current coordinators grid and userInfo to other peers
	 * @see edu.vub.welive.interfaces.JWeLive#setCoordinatorId(int)
	 */
	@Override
	public void setCoordinatorId(int coorId) {
		coordinatorId = coorId;		

		refreshActionBar();

		//send Grid and All Users
		sendGridAllUsersColors();
		
	}	

	/* From AT
	 * Get the new generation from the coordinator and refresh the grid
	 * @see edu.vub.welive.interfaces.JWeLive#setGenerationArray(java.util.ArrayList)
	 */
	@Override
	public void setGenerationArray(ArrayList<UsersPoints> usersPointsArray) {

		//Check if the sent grid is not the same as already sent
		//if it is different then count as new generation
		if(!Board.UsersPointsArray.equals(usersPointsArray)){
			//To count generation and give user extra cells each 5 generations
			board.countGeneration();

			Board.UsersPointsArray = usersPointsArray;

			refreshGrid();
		}
	}

	/* From AT
	 * Set users color array to new one (sent from coordinator)
	 * @see edu.vub.welive.interfaces.JWeLive#setUsersColorArray(java.util.ArrayList)
	 */
	public void setUsersColorArray(ArrayList<UserInfo> NewUsersColorsArray) {
		if(!Colors.UsersArray.equals(NewUsersColorsArray)){
			//Change user color array to the one that coordinator has
			Colors.UsersArray = NewUsersColorsArray;

			//When colors are changed change my users color
			myColor = colors.findColor(myDevID);

			//Refresh the grid
			refreshGrid();
		}
	}

	/* From AT
	 * Function receives new grid size from the coordinator
	 * @see edu.vub.welive.interfaces.JWeLive#setGridSize(int, int)
	 */
	@Override
	public void setGridSize(int h, int w) {
		//set new size
		GridView.setmHeight(h);
		GridView.setmWidth(w);

		refreshGrid();	
	}

	/*
	 * Function resize the grid
	 * if there are 2 players, then the grid is default
	 * every new player the grid becomes higher and wither
	 */
	public void resizeGrid(){
		//Count of all discovered users
		int discoveredUsers = Colors.UsersArray.size();

		if(discoveredUsers > 2){

			setGridSize(gridHeight+(discoveredUsers-2), gridWidth+(discoveredUsers-2));

			//if I am coordinator then send grid size on changes to all peers
			if(coordinatorId == myDevID){
				sendGridSize();
			}
			refreshGrid();
		}
	}

	/* From AT
	 * isGrayOut = true if user is disconnected
	 * isGrayOut = false if user reconnects (is online)
	 * @see edu.vub.welive.interfaces.JWeLive#grayOut(int, boolean)
	 */
	@Override
	public void grayOut(int userId, boolean isGrayOut) {	
		//Find user info
		UserInfo userInfo = colors.findUserInfo(userId);
		userInfo.setisGrayOut(isGrayOut);

		//Refresh the grid
		refreshGrid();
	}


	// Function refreshes the grid
	public void refreshGrid(){
		grid.postInvalidate();
	}

	// Function refreshes the action bar
	public void refreshActionBar(){
		invalidateOptionsMenu();
	}


	/*
	 * To AT
	 * 
	 */

	//send Grid and All Users
	public void sendGridAllUsersColors(){

		//If I become as coordinator, send my grid to all users
		if(coordinatorId == myDevID){
			//Send all users and it color
			//function to send all stored users and it color to other peers
			sendAllUserColors();

			//Send back to AT coordinators current Grid
			sendGridArray();
		}
	}

	//function to send all stored users and it color to other peers
	public void sendAllUserColors(){
		getFPHandler().sendMessage(Message.obtain(getFPHandler(), WeLiveActivity._MSG_USER_INFO_, Colors.UsersArray));
	}

	//Send users current Grid (array with user id and touched cells) to other peers
	public void sendGridArray(){
		getFPHandler().sendMessage(Message.obtain(getFPHandler(), WeLiveActivity._MSG_NEW_GRID_, Board.UsersPointsArray));
	}

	//Send grid size
	public void sendGridSize(){
		int [] gridSizeHW = {GridView.mHeight, GridView.mWidth};
		getFPHandler().sendMessage(Message.obtain(getFPHandler(), WeLiveActivity._MSG_GRID_SIZE_, gridSizeHW));
	}


	/*
	 *  Call AT methods in a separate thread to not block the UI
	 */
	class LooperThread extends Thread {

		public Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				//If the object is null do nothing
				if (null == atWLobject)
					return;
				switch (msg.what) {
				case _MSG_TOUCH_TOUCH_: {
					int[] touchPoint = (int[]) msg.obj;
					atWLobject.sendPlacedCell(touchPoint[0], touchPoint[1]);
					break;
				}
				case _MSG_NEW_GRID_: {
					System.out.println("send Grid");
					atWLobject.sendNewGenGrid((ArrayList<UsersPoints>) msg.obj);
					break;
				}
				case _MSG_USER_INFO_: {
					System.out.println("send colors");
					atWLobject.sendUserInfo((ArrayList<UserInfo>) msg.obj);
					break;
				}
				case _MSG_GRID_SIZE_: {
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



	//	public void storePlacedCell(int userId, int touchPointX, int touchPointY){
	//	//Store placed cell in UsersPointsArray
	//	board.UsersPointsArray.add(new UsersPoints(userId, touchPointX, touchPointY));
	//}

	//public boolean cellExists(int touchPointX, int touchPointY){
	//	//Check if the cell is already live
	//	for(UsersPoints p:  board.UsersPointsArray){
	//		if(p.getX() == touchPointX && p.getY() == touchPointY){
	//			//if cell is already live return true
	//			return true;
	//			
	//		}
	//	}
	//	//If cell is not live return false
	//	return false;
	//}

	//	/*
	//	 * Count generations, every 5 generations give user +4 cells
	//	 */
	//	public void countGeneration(){
	//		//Set that generation is +1
	//		countGeneration ++;
	//		
	//		//every 5 generation add bank cell + 4 cells
	//		if((countGeneration % 5) == 0){
	//			GridView.bankCell = GridView.bankCell + 4;
	//		}
	//	}

	//	/*
	//	 * 
	//	 * Calculates next generations
	//	 * after calculations send new grid to AT -> AT further send to other peers
	//	 * 
	//	 */
	//	public void calculateNextGeneration(){
	//
	//		this.NewPointsArray = this.generation.nextGeneration(grid.mHeight, grid.mWidth); //CHCHCH //gridHeight, gridWidth
	//		Board.UsersPointsArray = this.NewPointsArray;
	//
	//		//To count generation and give user extra cells each 5 generations
	//		board.countGeneration();
	//
	//		//refresh the grid
	//		refreshGrid();
	//		
	//		//send Grid and All Users
	//		sendGridAllUsersColors();
	//	
	//	}
	/*
	 * If user reconnects back change idColored to true
	 * @see edu.vub.welive.interfaces.JWeLive#colorOn(int)
	 */
	//	@Override
	//	public void colorOn(int userId) {
	//		//change isColored to true
	//		changeisColored(userId,true);
	//	}
	//	
	//	public void changeisColored(int userID, boolean isColored){
	//		//Find user info
	//		UserInfo userInfo = colors.findUserInfo(userID);
	//		//set new boolean isColored
	//		userInfo.setisGrayOut(isColored);
	//		//Refresh the grid
	//		refreshGrid();
	//	}


} //end if class
