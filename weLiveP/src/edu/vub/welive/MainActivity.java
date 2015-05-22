package edu.vub.welive;

import java.io.IOException;

import edu.vub.at.IAT;
import edu.vub.at.android.util.IATAndroid;
import edu.vub.at.android.util.IATSettings;
import edu.vub.at.android.util.IATSettings.IATOptions;
import edu.vub.at.exceptions.InterpreterException;
import edu.vub.welive.R;
import edu.vub.welive.WeLiveActivity.StartIATTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData.Item;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    	
    	
        View newButton = findViewById(R.id.new_button);
        newButton.setOnClickListener(this);

        
        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
       
        View aboutButton = findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
        
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        //Change title of action bar
        //getActionBar().setTitle("Score");

        
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
            System.exit(1);
	        break;
	    case R.id.about_button:
	    	aboutMessage();
	        break;
	    };
    
	    
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {    	
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);

        return super.onCreateOptionsMenu(menu);
        
        
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_score:
                //openSearch();
                return true;
            case R.id.action_cells:
            	//Change score
            	item.setTitle("Test");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
    
	public void aboutMessage(){
        alert.setTitle("About");
        alert.setMessage("weLive game is awesome game.");
        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog, int id) {
                //Toast.makeText (MainActivity.this, "Success", Toast.LENGTH_SHORT) .show();
            }
        });
        alert.show();
	}
    
}