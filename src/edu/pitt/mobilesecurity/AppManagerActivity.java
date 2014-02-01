package edu.pitt.mobilesecurity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import edu.pitt.mobilesecurity.domain.AppInfo;
import edu.pitt.mobilesecurity.engine.AppInfoProvider;
import edu.pitt.mobilesecurity.utils.MyAsyncTask;
import android.R.integer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.app.Activity;
import android.graphics.Color;
import android.text.format.Formatter;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppManagerActivity extends Activity {

	protected static final String TAG = "AppManagerActivity";
	private TextView tv_appmanager_rom;
	private TextView tv_appmanager_sd;
	private ListView lv_appmanager;
	private TextView tv_status;
	private View loading;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	private AppInfoAdapter appInfoAdapter;
	
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
				if(loadDataFinish) {
					if (firstVisibleItem <= (userAppInfos.size())) {
						tv_status.setText("User Apps: " + userAppInfos.size());
					} else {
						tv_status.setText("System Apps: " + systemAppInfos.size());
					}
				}
				
			}
		});
		
		loadAppData();
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
			// TODO Auto-generated method stub
			return null;
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
	
}
