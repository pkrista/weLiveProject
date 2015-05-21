package edu.vub.welive;

import edu.vub.at.android.util.AssetInstaller;

public class weLiveAssetInstaller extends AssetInstaller {
	// Overrides the default constructor to always copy the assets to the sdcard.
	public weLiveAssetInstaller(){
		super();
	    development = true;
	}
}
