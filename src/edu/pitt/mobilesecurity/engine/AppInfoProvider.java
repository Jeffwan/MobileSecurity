package edu.pitt.mobilesecurity.engine;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.mobilesecurity.domain.AppInfo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppInfoProvider {

	public static List<AppInfo> getAppInfos(Context context) {
		
		PackageManager mPackageManager = context.getPackageManager();
		List<PackageInfo> packInfos = mPackageManager.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packInfos) {
			AppInfo appInfo = new AppInfo();
			appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(mPackageManager));
			appInfo.setAppName(packageInfo.applicationInfo.loadLabel(mPackageManager).toString());
			appInfo.setPackName(packageInfo.packageName);
			appInfo.setVersion(packageInfo.versionName);			
			
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appInfo.setSystem(false);
			} else {
				appInfo.setSystem(true);
			}
			
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				appInfo.setRom(true);
			} else {
				appInfo.setRom(false);
			}
			
			appInfos.add(appInfo);
		}
		
		return appInfos;
		
	}
	
}
