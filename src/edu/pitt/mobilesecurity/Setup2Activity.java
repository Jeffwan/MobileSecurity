package edu.pitt.mobilesecurity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {

	private TextView tv_setup2_sim_click;
	private TextView tv_setup2_sim_number;
	private TelephonyManager mTelephonyManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		tv_setup2_sim_click = (TextView) findViewById(R.id.tv_setup2_sim_click);
		tv_setup2_sim_number = (TextView) findViewById(R.id.tv_setup2_sim_number);
		
		String simNumber = mSharedPreferences.getString("sim", "");
		if (TextUtils.isEmpty(simNumber)) {
			tv_setup2_sim_number.setText("SimCard Not Bind");
			tv_setup2_sim_click.setText("Click Me to Bind SimCard");
		} else {
			tv_setup2_sim_number.setText("SimCard Number: " + simNumber);
			tv_setup2_sim_click.setText("Click Me to unBind SimCard");
		}
		
	}

	@Override
	public void showNext() {
		String simNumber = mSharedPreferences.getString("sim", "");
		if (TextUtils.isEmpty(simNumber)) {
			Toast.makeText(getApplicationContext(), "You Need to Bind SimCard!", 1).show();
			return;
		}
		
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_next_in, R.anim.trans_next_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}
	
	public void bindSimCard(View view) {
		String savedSim = mSharedPreferences.getString("sim", "");
		Editor editor = mSharedPreferences.edit();
		if (TextUtils.isEmpty(savedSim)) {
			String simNumber = mTelephonyManager.getSimSerialNumber();

			editor.putString("sim", simNumber);
			editor.commit();
			tv_setup2_sim_number.setText("SimCard Number: " + simNumber);
			tv_setup2_sim_click.setText("Click Me to unBind SimCard");
		} else {
			editor.putString("sim", "");
			editor.commit();
			tv_setup2_sim_number.setText("SimCard Not Bind");
			tv_setup2_sim_click.setText("Click Me to Bind SimCard");
		}	
		
	}

}
