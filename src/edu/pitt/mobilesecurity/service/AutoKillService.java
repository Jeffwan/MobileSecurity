package edu.pitt.mobilesecurity.service;

import java.util.List;

import javax.crypto.Mac;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.view.View.OnCreateContextMenuListener;

public class AutoKillService extends Service {
	private ScreenLockReceiver receiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		receiver = new ScreenLockReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}
	
	private class ScreenLockReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> runningAppProcesses = mActivityManager.getRunningAppProcesses();
			for (RunningAppProcessInfo info : runningAppProcesses) {
				mActivityManager.killBackgroundProcesses(info.processName);
			}
		}
	}
	
}
