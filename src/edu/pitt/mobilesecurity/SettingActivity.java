package edu.pitt.mobilesecurity;

import edu.pitt.mobilesecurity.ui.SettingView;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	private SettingView sv_setting_update;
	private SharedPreferences sPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
		
		// Initial autoUpdate data
		sPreferences = getSharedPreferences("config", MODE_PRIVATE);
		sv_setting_update.setChecked(sPreferences.getBoolean("autoUpdate", false));
		
		sv_setting_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sPreferences.edit();
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
	}

}
