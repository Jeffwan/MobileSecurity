package edu.pitt.mobilesecurity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class AntiTheftActivity extends Activity {

	private static final String TAG = "AntiTheftActivity";
	private SharedPreferences mSharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		
		if(isSetup()) {
			Log.i(TAG, "Already Setup, Go into AntiTheft");
			setContentView(R.layout.activity_anti_theft);
			
			
		} else {
			Log.i(TAG, "Go to Setup Page");
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish(); // prevent user click return 
		}
		super.onCreate(savedInstanceState);
	}

	private boolean isSetup() {
		return mSharedPreferences.getBoolean("setup", false);
	}

	private void reEnterSetup() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}
	

}
