package edu.pitt.mobilesecurity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_setup);
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
