package edu.pitt.mobilesecurity;

import edu.pitt.mobilesecurity.receiver.SuperAdminReceiver;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cb_setup4_status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		cb_setup4_status = (CheckBox) findViewById(R.id.cb_setup4_status);
		cb_setup4_status.setChecked(mSharedPreferences.getBoolean("protecting", false));
		cb_setup4_status.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Editor editor = mSharedPreferences.edit();
				editor.putBoolean("protecting", isChecked);
				editor.commit();
			}
		});	
	}

	@Override
	public void showNext() {
		if(cb_setup4_status.isChecked()) {
			// Go to AntiTheft Page
			Intent intent = new Intent(this, AntiTheftActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.trans_next_in, R.anim.trans_next_out);
		} else {
			// Show AlertDialog
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("Friendly Hint");
			builder.setMessage("We suggest you to open the AntiTheft ");
			builder.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Editor editor = mSharedPreferences.edit();
					editor.putBoolean("setup", true);
					editor.putBoolean("protecting", true);
					editor.commit();
					finish();
				}
			});
			builder.setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();  // difference with intent go to?
					// protecting not be false 
					
				}
			});
			builder.show();
		}
		
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}

	
	public void activieAdmin(View view) {
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

		ComponentName  mDeviceAdminSample = new ComponentName(this, SuperAdminReceiver.class);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"Active Me, I Can Protect You");
		startActivity(intent);
	}
	

}
