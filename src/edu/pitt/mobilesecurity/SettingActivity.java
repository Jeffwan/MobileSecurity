package edu.pitt.mobilesecurity;

import edu.pitt.mobilesecurity.service.AutoKillService;
import edu.pitt.mobilesecurity.service.CallSmsFirewallService;
import edu.pitt.mobilesecurity.service.ShowAddressService;
import edu.pitt.mobilesecurity.ui.SettingView;
import edu.pitt.mobilesecurity.utils.ServiceStatusUtils;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends Activity {
	// 1. AutoUpdate 
	private SettingView sv_setting_update;
	
	// 2. Location Configuration
	private SettingView sv_setting_show_address;
	private Intent showAddressIntent;
	private SharedPreferences mSharedPreferences;
	
	// 3. Location window bg
	private RelativeLayout rl_setting_change_bg;
	private TextView tv_setting_address_bg;
	private static final String[] items = {"Transparent", "Orange", "Blue", "Green", "Grey"};
	
	// 4. CallSms Firewall
	private SettingView sv_setting_firewall;
	private Intent fireWallIntent;
	
	// 5. TaskManager
	private SettingView sv_setting_show_system_task;
	
	// 6. AutoKillTask
	private SettingView sv_setting_auto_kill;
	private Intent autoKillIntent;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
		sv_setting_show_address = (SettingView) findViewById(R.id.sv_setting_show_address);
		
		// 1. Initial autoUpdate data
		mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		sv_setting_update.setChecked(mSharedPreferences.getBoolean("autoUpdate", false));
		
		sv_setting_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = mSharedPreferences.edit();
				if (sv_setting_update.isChecked()) {
					sv_setting_update.setChecked(false);
					editor.putBoolean("autoUpdate", false);
				} else {
					sv_setting_update.setChecked(true);
					editor.putBoolean("autoUpdate", true);
				}
				editor.commit();
			}
		});
		
		// 2. Initial show address data
		showAddressIntent = new Intent(this, ShowAddressService.class);
		sv_setting_show_address.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				if (sv_setting_show_address.isChecked()) {
					sv_setting_show_address.setChecked(false);
					stopService(showAddressIntent);				//close service
				} else {
					sv_setting_show_address.setChecked(true);
					startService(showAddressIntent);			// open service
				}
				
			}
		});
		
		// 3. Initial location window background data
		rl_setting_change_bg = (RelativeLayout) findViewById(R.id.rl_setting_change_bg);
		tv_setting_address_bg = (TextView) findViewById(R.id.tv_setting_address_bg);
		tv_setting_address_bg.setText(items[mSharedPreferences.getInt("addressBG",0)]);
		
		rl_setting_change_bg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showChangeBgDialog();
				
			}
		});
		
		// 4. Initial CallSmsFireWall 
		sv_setting_firewall = (SettingView) findViewById(R.id.sv_setting_firewall);
		fireWallIntent = new Intent(this, CallSmsFirewallService.class);
		sv_setting_firewall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (sv_setting_firewall.isChecked()) {
					stopService(fireWallIntent);
					sv_setting_firewall.setChecked(false);
				} else {
					startService(fireWallIntent);
					sv_setting_firewall.setChecked(true);
				}
				
			}
		});
		
		
		// 5. Initial system tasks show
		sv_setting_show_system_task = (SettingView) findViewById(R.id.sv_setting_show_system_task);
		boolean showSystem = mSharedPreferences.getBoolean("showSystem", true);
		sv_setting_show_system_task.setChecked(showSystem);
		sv_setting_show_system_task.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = mSharedPreferences.edit();
				if (sv_setting_show_system_task.isChecked()) {
					editor.putBoolean("showSystem", false);
					sv_setting_show_system_task.setChecked(false);
				} else {
					editor.putBoolean("showSystem", true);
					sv_setting_show_system_task.setChecked(true);
				}
				editor.commit();
			}
		});
		
		
		// 6. Kill Task after lock screen
		sv_setting_auto_kill = (SettingView) findViewById(R.id.sv_setting_auto_kill);
		autoKillIntent = new Intent(this,AutoKillService.class);
		sv_setting_auto_kill.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(sv_setting_auto_kill.isChecked()){
					sv_setting_auto_kill.setChecked(false);
					stopService(autoKillIntent);
				}else{
					sv_setting_auto_kill.setChecked(true);
					startService(autoKillIntent);
				}
			}
		});
		
	}
	
	
	
	protected void showChangeBgDialog() {
		int which = mSharedPreferences.getInt("addressBG", 0);
		
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Change Address Background");
		builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = mSharedPreferences.edit();
				editor.putInt("addressBG", which);
				editor.commit();
				tv_setting_address_bg.setText(items[which]);
				dialog.dismiss();
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		
		builder.show();
	}


	@Override
	protected void onStart() {
		sv_setting_show_address.setChecked(ServiceStatusUtils.isServiceRunning(this, ShowAddressService.class));
		sv_setting_firewall.setChecked(ServiceStatusUtils.isServiceRunning(this, CallSmsFirewallService.class));
		sv_setting_auto_kill.setChecked(ServiceStatusUtils.isServiceRunning(this, AutoKillService.class));
		super.onStart();
	}

}
