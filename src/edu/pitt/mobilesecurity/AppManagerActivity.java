package edu.pitt.mobilesecurity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import edu.pitt.mobilesecurity.domain.AppInfo;
import edu.pitt.mobilesecurity.engine.AppInfoProvider;
import edu.pitt.mobilesecurity.utils.DensityUtil;
import edu.pitt.mobilesecurity.utils.MyAsyncTask;
import android.R.integer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.Formatter;
import android.util.Log;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements OnClickListener {

	protected static final String TAG = "AppManagerActivity";
	private TextView tv_appmanager_rom;
	private TextView tv_appmanager_sd;
	private ListView lv_appmanager;
	private TextView tv_status;
	private View loading;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	private AppInfoAdapter appInfoAdapter;
	
	private PopupWindow popupWindow;
	private AppInfo selectedAppInfo;
	
	private boolean loadDataFinish;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		
		tv_appmanager_rom = (TextView) findViewById(R.id.tv_appmanager_rom);
		tv_appmanager_sd = (TextView) findViewById(R.id.tv_appmanger_sd);
		tv_status = (TextView) findViewById(R.id.tv_status);
		lv_appmanager = (ListView) findViewById(R.id.lv_appmanager);
		loading = findViewById(R.id.loading);
		
		tv_appmanager_rom.setText("Rom: " + getAvaiRom());
		tv_appmanager_sd.setText("SD: " + getAvaiSD());
		
		lv_appmanager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopUpWindow();
				
				if(loadDataFinish) {
					if (firstVisibleItem <= (userAppInfos.size())) {
						tv_status.setText("User Apps: " + userAppInfos.size());
					} else {
						tv_status.setText("System Apps: " + systemAppInfos.size());
					}
				}
				
			}
		});
		
		lv_appmanager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// dismiss the previous if exists
				dismissPopUpWindow();
				
				Object obj = lv_appmanager.getItemAtPosition(position);
				if (obj != null && obj instanceof AppInfo) {
					selectedAppInfo = (AppInfo) obj;
					View contentView = View.inflate(getApplicationContext(), R.layout.popup_item, null);
					contentView.findViewById(R.id.ll_uninstall).setOnClickListener(AppManagerActivity.this);
					contentView.findViewById(R.id.ll_start).setOnClickListener(AppManagerActivity.this);
					contentView.findViewById(R.id.ll_share).setOnClickListener(AppManagerActivity.this);
					
					// -2 equals Wrap_Content
					popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, -2, false);
					popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					int[] location = new int[2];
					view.getLocationInWindow(location);
					popupWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, 
							location[0] + DensityUtil.dip2px(getApplicationContext(), 60), location[1]);
					
					// popUpWindow location
					ScaleAnimation sa = new ScaleAnimation(0.2f, 1.2f, 0.2f, 1.2f, 
							Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					sa.setDuration(200);
					contentView.startAnimation(sa);
					
				}
				
			}
			
		});
		
		
		loadAppData();
	}

	private void dismissPopUpWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}
	
	
	private void loadAppData() {
		new MyAsyncTask() {
			
			@Override
			protected void onPreExectue() {
				loadDataFinish = false;
				loading.setVisibility(View.VISIBLE);
				
			}
			
			@Override
			protected void onPostExectue() {
				loading.setVisibility(View.INVISIBLE);
				appInfoAdapter = new AppInfoAdapter();
				// Make sure data load success, and then set adapter
				lv_appmanager.setAdapter(appInfoAdapter);
				loadDataFinish = true;
			}
			
			@Override
			protected void doInBackground() {
				List<AppInfo> appInfos = AppInfoProvider.getAppInfos(getApplicationContext());
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isSystem()) {
						systemAppInfos.add(appInfo);
					} else {
						userAppInfos.add(appInfo);
					}
				}
			}
		}.execute();
		
	}

	private String getAvaiRom() {
		File file = Environment.getDataDirectory();
		StatFs statFs = new StatFs(file.getAbsolutePath());
		long size = statFs.getAvailableBlocks() * statFs.getBlockSize();
		return Formatter.formatFileSize(this, size);
	}

	private String getAvaiSD() {
		File file = Environment.getExternalStorageDirectory();
		StatFs statFs = new StatFs(file.getAbsolutePath());
		long size = statFs.getAvailableBlocks() * statFs.getBlockSize();
		return Formatter.formatFileSize(this, size);
	}

	
	private class AppInfoAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// two labels
			return userAppInfos.size() + systemAppInfos.size() + 2 ;
		}

		@Override
		public Object getItem(int position) {
			AppInfo appInfo = null;
			if (position == 0) {
				return null;
			} else if (position == (userAppInfos.size() +1)) {
				return null;
			} else if (position <= userAppInfos.size()) {
				int newPosition = position - 1;
				appInfo = userAppInfos.get(newPosition);
			} else {
				int newPosition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newPosition);
			}
			
			return appInfo;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo = null;
			
			// set data accoding to position
			if (position == 0) {
				// 1.User app label
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.BLACK);
				tv.setText("User Apps: " + userAppInfos.size());
				// first time I forget return value back
				return tv;
			} else if (position == userAppInfos.size() + 1){
				// 2. System app label
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.BLACK);
				tv.setText("System Apps: " + systemAppInfos.size());
				return tv;
			} else if(position <= userAppInfos.size()) {
				int newPosition = position - 1;
				appInfo = userAppInfos.get(newPosition);
			} else {
				int newPosition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newPosition);
			}
			
			// get components of view 
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(), R.layout.list_app_item, null);
				holder = new ViewHolder();
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_version = (TextView) view.findViewById(R.id.tv_app_version);
				holder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				view.setTag(holder);
			}
			
			// Set Data to View
			holder.tv_name.setText(appInfo.getAppName());
			holder.iv_icon.setImageDrawable(appInfo.getAppIcon());
			holder.tv_version.setText(appInfo.getVersion());
			if (appInfo.isRom()) {
				holder.tv_location.setText("Rom");	
			} else {
				holder.tv_location.setText("SDCard");
			}
			
			return view;
		}
		
	}
	
	static class ViewHolder {
		TextView tv_name;
		TextView tv_version;
		TextView tv_location;
		ImageView iv_icon;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_uninstall:
			dismissPopUpWindow();
			if (selectedAppInfo.isSystem()) {
				Toast.makeText(getApplicationContext(), "Can't uninstall System App!", 1).show();
			} else {
				unInstallApp(selectedAppInfo.getPackName());
			}
			break;
			
		case R.id.ll_start:
			dismissPopUpWindow();
			startApplication(selectedAppInfo.getPackName());
			break;
			
		case R.id.ll_share:
			dismissPopUpWindow();
			shareApplicaiton(selectedAppInfo.getPackName());
			break;
			
		default:
			break;
		}
		
	}
	
	private void shareApplicaiton(String packName) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "Come on, download this good app https://play.google.com/store/apps/details?id="+packName);
		startActivity(intent);
	}

	private void startApplication(String packName) {
		// we must add get activities here
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(packName, PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityInfos =  packageInfo.activities;
			if (activityInfos != null && activityInfos.length >0) {
				ActivityInfo activityInfo = activityInfos[0];
				String className = activityInfo.name;
				String packname = activityInfo.packageName;
				Intent intent = new Intent();
				intent.setClassName(packname, className);
				startActivity(intent);
			} else {
				Toast.makeText(getApplicationContext(), "Can't start app!", 1).show();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Can't start app!", 1).show();
		}
		
	}

	private void unInstallApp(String packName) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + packName));
		startActivityForResult(intent, 0);
		
	}

	@Override
	protected void onDestroy() {
		dismissPopUpWindow();
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// here's problem, even if I cancel uninstall, it still reload data -- not efficient
		loadAppData();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			dismissPopUpWindow();
			// prevent the normal event
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
