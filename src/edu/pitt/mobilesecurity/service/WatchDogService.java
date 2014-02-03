package edu.pitt.mobilesecurity.service;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.mobilesecurity.EnterPasswordActivity;
import edu.pitt.mobilesecurity.db.dao.AppLockDao;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class WatchDogService extends Service {
	private ActivityManager am;
	private boolean flag;
	private AppLockDao dao;
	private Intent passWordIntent;
	private List<String> tempStopProtectPacknames;
	private InnerReceiver receiver;
	private LockScreenReceiver lockScreenReceiver;
	
	private UnLockScreenReceiver unlockScreenReceiver;

	private List<String> lockedPacknames;

	private ApplockDBObserver observer;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		tempStopProtectPacknames = new ArrayList<String>();
		receiver = new InnerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("edu.pitt.mobilesecurity.stopdog");
		registerReceiver(receiver, filter);

		lockScreenReceiver = new LockScreenReceiver();
		IntentFilter lockFilter = new IntentFilter();
		lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(lockScreenReceiver, lockFilter);
		
		unlockScreenReceiver = new UnLockScreenReceiver();
		IntentFilter unlockFilter = new IntentFilter();
		unlockFilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(unlockScreenReceiver, unlockFilter);
		

		observer = new ApplockDBObserver(new Handler());
		getContentResolver().registerContentObserver(AppLockDao.uri, true,
				observer);


		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(this);
		lockedPacknames = dao.findAll();
		passWordIntent = new Intent(this, EnterPasswordActivity.class);
		passWordIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startWatchDog();

	}
	

	
	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String packname = intent.getStringExtra("packname");
			tempStopProtectPacknames.add(packname);
			
		}
		
	}
	
	private class LockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectPacknames.clear();
			flag = false;
			
		}
	}
	
	private class UnLockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(!flag) {
				startWatchDog();
			}
			
		}
	}

	private void startWatchDog() {
		new Thread() {
			public void run() {
				flag = true;
				while (flag) {
					RunningTaskInfo runnintTaskInfo = am.getRunningTasks(1).get(0);
					String packname = runnintTaskInfo.topActivity.getPackageName();
					System.out.println(packname);
					
					if (lockedPacknames.contains(packname)) {
						if (tempStopProtectPacknames.contains(packname)) {

						} else {
							passWordIntent.putExtra("packname", packname);
							startActivity(passWordIntent);
						}
					}

					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				
			};
		}.start();
	}
		
	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(receiver);
		receiver = null;
		unregisterReceiver(lockScreenReceiver);
		lockScreenReceiver = null;
		
		unregisterReceiver(unlockScreenReceiver);
		unlockScreenReceiver = null;

		getContentResolver().unregisterContentObserver(observer);
		observer = null;
		
		super.onDestroy();
	}
	
	private class ApplockDBObserver extends ContentObserver {

		public ApplockDBObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			Log.i("ApplockDBObserver", "locked apps changed!");
			lockedPacknames = dao.findAll();
			super.onChange(selfChange);
		}
	}
	
}
