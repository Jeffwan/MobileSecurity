package edu.pitt.mobilesecurity;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	protected static final String TAG = "BaseSetupActivity";
	private GestureDetector mGestureDetector;
	protected SharedPreferences mSharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		
		// 1. Setup a gesture listener
		mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				
				if (Math.abs(velocityX) < 200 ) {
					Log.i(TAG,"so slow...");
					return false;
				}
				
				if (Math.abs(e1.getRawY() - e2.getRawY()) > 200) {
					Log.i(TAG,"invalid move");
					return false;
				}
				
				if (e1.getRawX() - e2.getRawX() > 200) {
					showNext();
					return false;
				}
				
				if (e1.getRawX() - e2.getRawX() < 200) {
					showPre();
					return false;
				}
				
				
				// TODO Auto-generated method stub
				return super.onFling(e1, e2, velocityX, velocityY);
			}
			
		});
	}

		
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 2. Bring touch info effect -- override onTouchEvent of Activity
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void next(View view){
		showNext();
	}
	
	public void pre(View view){
		showPre();
	}
	
	public abstract void showNext();
	
	public abstract void showPre();
}
