package edu.pitt.mobilesecurity.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	
	private Drawable appIcon;
	private String appName;
	private String version;
	private String packName;
	private boolean isRom;
	private boolean isSystem;
	
	public Drawable getAppIcon() {
		return appIcon;
	}
	
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getPackName() {
		return packName;
	}
	
	public void setPackName(String packName) {
		this.packName = packName;
	}
	
	public boolean isRom() {
		return isRom;
	}
	
	public void setRom(boolean isRom) {
		this.isRom = isRom;
	}
	
	public boolean isSystem() {
		return isSystem;
	}
	
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	
	@Override
	public String toString() {
		return "AppInfo [appName=" + appName + ", version=" + version
				+ ", packName=" + packName + ", isRom=" + isRom + ", isSystem="
				+ isSystem + "]";
	}
	
}
