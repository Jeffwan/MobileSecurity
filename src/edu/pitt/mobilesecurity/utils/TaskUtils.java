package edu.pitt.mobilesecurity.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

public class TaskUtils {

	public static int getRunningProcessCount(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return mActivityManager.getRunningAppProcesses().size();
	}
	
	public static long getAvailableRam(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo info = new MemoryInfo();
		mActivityManager.getMemoryInfo(info);
		return info.availMem; 
	}
	
	public static long getTotalRam(Context context) {
		// we can also use the same way getAvailableRam like info.totalMemo but lower Android version doesn't support 
		try {
			File file = new File("/proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String memStr = reader.readLine();
			
			char[] arr = memStr.toCharArray();
			StringBuffer sb = new StringBuffer();
			for (char c: arr) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			return Integer.parseInt(sb.toString()) * 1024;
			
		} catch (Exception e) {
			return 0;
		}
		
	}
	
}
