package edu.pitt.mobilesecurity;

import edu.pitt.mobilesecurity.service.WatchDogService;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EnterPasswordActivity extends Activity {
	private EditText et_password;
	private String packname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_password);
		
		et_password = (EditText) findViewById(R.id.et_password);
		
		packname = getIntent().getStringExtra("packname");
		
		Intent service = new Intent(this,WatchDogService.class);
	}

	
	public void enter(View view) {
		String password = et_password.getText().toString().trim();
		if("123".equals(password)){
			Intent intent = new Intent();
			intent.setAction("edu.pitt.mobilesecurity.stopdog");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);
			finish();
			// TODO: XXX
		}else{
			Toast.makeText(this, "Password not correct", 0).show();
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addCategory("android.intent.category.MONKEY");
			startActivity(intent);
			//return super.onKeyDown(KeyEvent.KEYCODE_HOME, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME));
			//dispatchKeyEvent(event)
		}
		super.onKeyDown(KeyEvent.KEYCODE_HOME, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME));
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
	@Override
	protected void onDestroy() {
		//TODO: xxx
		super.onDestroy();
	}
}
