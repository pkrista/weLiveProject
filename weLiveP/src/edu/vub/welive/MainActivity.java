package edu.vub.welive;

import edu.vub.welive.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;


public class MainActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private AlertDialog.Builder alert;

	public View button ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		alert = new AlertDialog.Builder(MainActivity.this);

		//Copy AmbientTalk files to the SD card
		Intent i = new Intent(this, weLiveAssetInstaller.class);
		startActivityForResult(i,0);


		// click-handlers for buttons
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
		
		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		
		View newButton = findViewById(R.id.new_button);
		newButton.setOnClickListener(this);
	}


	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_button:

			//To start the game
			Intent i_new = new Intent(this, WeLiveActivity.class);
			startActivity(i_new);

			break;
		case R.id.exit_button:
			//To exit the application
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
			break;
			
		case R.id.about_button:
			aboutMessage();
			break;
		};
	}


	public void aboutMessage(){
		alert.setTitle("About");
		alert.setMessage("Multi player game weLive \n\nDistributed and Mobile Programming Paradigms \n\nAuthor: Krista Puíe");
		alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int id) {
				//Toast.makeText (MainActivity.this, "Success", Toast.LENGTH_SHORT) .show();
			}
		});
		alert.show();
	}

}