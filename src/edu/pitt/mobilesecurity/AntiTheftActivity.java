package edu.pitt.mobilesecurity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AntiTheftActivity extends Activity {

	private static final String TAG = "AntiTheftActivity";
	private SharedPreferences mSharedPreferences;
	private TextView tv_number;
	private ImageView iv_status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		
		if(isSetup()) {
			Log.i(TAG, "Already Setup, Go into AntiTheft");
			setContentView(R.layout.activity_anti_theft);
			tv_number = (TextView) findViewById(R.id.tv_antitheft_number);
			tv_number.setText(mSharedPreferences.getString("safenumber", ""));
			iv_status = (ImageView) findViewById(R.id.iv_antitheft_status);
			
			boolean protectStatus = mSharedPreferences.getBoolean("protecting", false);
			
			if (protectStatus) {
				iv_status.setImageResource(R.drawable.lock);
			} else {
				iv_status.setImageResource(R.drawable.unlock);
			}

			
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

	public void reEnterSetup(View view) {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}
	

}
