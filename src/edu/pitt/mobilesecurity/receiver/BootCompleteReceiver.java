package edu.pitt.mobilesecurity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver{

	private static final String TAG = "BootCompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean protecting = mSharedPreferences.getBoolean("protecting", false);
		if (protecting) {
			String savedSim = mSharedPreferences.getString("sim", "");	
			
			TelephonyManager mtTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String realSim = mtTelephonyManager.getSimSerialNumber();
			
			if (!realSim.equals(savedSim)) {
				Log.i(TAG, "SimCard changed!");
				String safeNumber = mSharedPreferences.getString("safenumber", "");
				SmsManager mSmsManager = SmsManager.getDefault();
				mSmsManager.sendTextMessage(safeNumber, null, "SimCard Changed!", null, null);
			} 
			
		} else {
			Log.i(TAG, "Phone not in protecting");
		}
		
	}

}
