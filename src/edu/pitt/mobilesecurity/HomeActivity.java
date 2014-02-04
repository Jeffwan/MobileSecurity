package edu.pitt.mobilesecurity;

import edu.pitt.mobilesecurity.adapter.HomeAdapter;
import edu.pitt.mobilesecurity.utils.MD5Utils;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	protected static final String TAG = "HomeActivity";
	private GridView gv;
	private SharedPreferences sPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sPreferences = getSharedPreferences("config", MODE_PRIVATE);
		
		gv = (GridView) findViewById(R.id.gv_home);
		gv.setAdapter(new HomeAdapter(this));
		
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent;
				switch (position) {
				case 0:
					if(isSetupPassword()) {
						// Show normal dialog
						showNormalDialog();
					} else {
						// Show setup dialog
						showSetupDialog();
					}
					break;
					
				case 1:
					intent = new Intent(HomeActivity.this, CallSmsSafeActivity.class);
					startActivity(intent);
					break;
					
				case 2:
					intent = new Intent(HomeActivity.this, AppManagerActivity.class);
					startActivity(intent);
					break;
					
				case 3:
					intent = new Intent(HomeActivity.this, TaskManagerActivity.class);
					startActivity(intent);
					break;
					
				case 4:
					intent = new Intent(HomeActivity.this, TrafficManagerActivity.class);
					startActivity(intent);
					break;
					
				case 5:
					intent = new Intent(HomeActivity.this, AntiVirusActivity.class);
					startActivity(intent);
					break;
					
				case 6:
					intent = new Intent(HomeActivity.this, SystemOptActivity.class);
					startActivity(intent);
					break;	
					
				case 7:
					intent = new Intent(HomeActivity.this, AtoolsActivity.class);
					startActivity(intent);
					break;
					
				case 8:
					intent = new Intent(HomeActivity.this,SettingActivity.class);
					startActivity(intent);
					break;
				}
				
			}

		});
		
	}
	
	
	private AlertDialog dialog;
	protected void showSetupDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Please Setup Your Password");
		View view = View.inflate(this, R.layout.dialog_setup_pwd, null);
		builder.setView(view);
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);
		final EditText et_password_confirm = (EditText) view.findViewById(R.id.et_password_confirm);
		Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		
		bt_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString().trim();
				String password_confirm = et_password_confirm.getText().toString().trim();
				if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password_confirm)) {
					Toast.makeText(getApplicationContext(), "cannot be empty", 1).show();
					return;
				}
				if (password.equals(password_confirm)) {
					Editor editor = sPreferences.edit();
					editor.putString("password", MD5Utils.encode(password));
					editor.commit();
					dialog.dismiss();
				} else {
					Toast.makeText(getApplicationContext(), "password not same", 1).show();
					return;
				}
			}
		});
		
		bt_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		
		dialog = builder.show();
	}


	protected void showNormalDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Please Enter Your Password");
		View view = View.inflate(this, R.layout.dialog_normal_pwd, null);
		builder.setView(view);
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);
		Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		
		bt_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString().trim();
				String savedPassword = sPreferences.getString("password", "");
				
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "cannot be empty", 1).show();
					return;
				}
				
				if (savedPassword.equals(MD5Utils.encode(password))) {
					// Password same, Go to UI
					Log.i(TAG, "Passwood same, Go to UI");
					Intent intent = new Intent(HomeActivity.this, AntiTheftActivity.class);
					startActivity(intent);
					dialog.dismiss();
				} else {
					Toast.makeText(getApplicationContext(), "password not same", 1).show();
					return;
				}
				
				
			}
		});
		
		bt_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		
		dialog = builder.show();
		
	}


	private boolean isSetupPassword() {
		String password = sPreferences.getString("password", "");
		if(TextUtils.isEmpty(password)){
			return false;
		} else {
			return true;
		}
	}

}
