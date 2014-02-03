package edu.pitt.mobilesecurity.domain;

import android.graphics.drawable.Drawable;

public class TaskInfo {
	private String appName;
	private String packName;
	private Drawable appIcon;
	private long memSize;   // long byte in system
	private boolean checked;
	private boolean userTask;
	
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public boolean isUserTask() {
		return userTask;
	}
	public void setUserTask(boolean userTask) {
		this.userTask = userTask;
	}
	
	
	@Override
	public String toString() {
		return "TaskInfo [appName=" + appName + ", packName=" + packName
				+ ", memSize=" + memSize + ", userTask=" + userTask + "]";
	}

	
}
