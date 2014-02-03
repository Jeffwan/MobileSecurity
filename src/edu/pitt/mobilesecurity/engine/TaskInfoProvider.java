package edu.pitt.mobilesecurity.engine;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;

import edu.pitt.mobilesecurity.R;
import edu.pitt.mobilesecurity.domain.TaskInfo;

import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class TaskInfoProvider {

	
	public static List<TaskInfo> getTaskInfos(Context context) {
		PackageManager mPackageManager = context.getPackageManager();
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		List<RunningAppProcessInfo> runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
			TaskInfo taskInfo = new TaskInfo();
			// 1. 
			String packageName = runningAppProcessInfo.processName;
			taskInfo.setPackName(packageName);  // in Android, processName equals packageName;
			
			try {
				PackageInfo info = mPackageManager.getPackageInfo(packageName, 0);
				taskInfo.setAppIcon(info.applicationInfo.loadIcon(mPackageManager));
				taskInfo.setAppName(info.applicationInfo.loadLabel(mPackageManager).toString());	
				
				if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					taskInfo.setUserTask(true);
				} else {
					taskInfo.setUserTask(false);
				}
				
			} catch (NameNotFoundException e) {
				taskInfo.setAppIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
				taskInfo.setAppName(packageName);
				e.printStackTrace();
			}
					
			
			// how to get this
			long memInfo = mActivityManager
					.getProcessMemoryInfo(new int[] {runningAppProcessInfo.pid})[0]
					.getTotalPrivateDirty() * 1024;
			taskInfo.setMemSize(memInfo);
			taskInfos.add(taskInfo);
		}
		
		return taskInfos;
	}
	
}
