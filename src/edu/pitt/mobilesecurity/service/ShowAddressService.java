package edu.pitt.mobilesecurity.service;

import edu.pitt.mobilesecurity.R;
import edu.pitt.mobilesecurity.db.dao.AddressDao;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ShowAddressService extends Service {

	public static final String TAG = "ShowAddressService";
	private TelephonyManager mTelephonyManager;
	private MyListener listener;
	private OutCallBroadcastReceiver outCallBroadcastReceiver;
	private WindowManager mWindowManager;
	private View view;
	
	private WindowManager.LayoutParams params;
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class OutCallBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "You make a outgoing call");
			String number = getResultData();
			String address = AddressDao.getAddress(number);
			showAddressInWindow(address);
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		// initial WindowsManger
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		// 1. Listen Incoming calls
		mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		// listen on call state ---> listener
		mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		// 2. Listen Outgoing calls
		outCallBroadcastReceiver = new OutCallBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(outCallBroadcastReceiver, filter);
	}
	

	private class MyListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String address = AddressDao.getAddress(incomingNumber);
				showAddressInWindow(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE: // hangup 
				if (view != null) {
					mWindowManager.removeView(view);
					view = null;
				}
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	@Override
	public void onDestroy() {
		// 1. Unregister incoming listener when service is destroyed
		mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
		
		// 2. Unregister outgoing listener
		unregisterReceiver(outCallBroadcastReceiver);
		outCallBroadcastReceiver = null;
	}
	

	public void showAddressInWindow(String address) {
		view = View.inflate(getApplicationContext(), R.layout.toast_show_address, null);
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		int which = sp.getInt("addressBG", 0);
		int[] icons = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_green, R.drawable.call_locate_gray };
		view.setBackgroundResource(icons[which]);
		
		TextView tv = (TextView) view.findViewById(R.id.tv_toast_address);
		tv.setText(address);
		params = new LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		mWindowManager.addView(view, params);
	}
	
}
