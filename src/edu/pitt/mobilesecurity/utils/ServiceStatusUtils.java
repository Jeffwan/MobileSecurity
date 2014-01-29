package edu.pitt.mobilesecurity.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {
	
	public static boolean isServiceRunning (Context context, Class<?> cls) {
		
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServiceInfos = mActivityManager.getRunningServices(20);
		for (RunningServiceInfo info : runningServiceInfos) {
			if (cls.getName().equals(info.service.getClassName())) {
				return true;
			}
		}
		
		return false;
		
	}
}
