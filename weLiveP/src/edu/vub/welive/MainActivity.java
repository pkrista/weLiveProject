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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
	    	//To exit the application
	    	android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
	        break;
	    };
    }

}