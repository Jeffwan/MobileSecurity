package edu.pitt.mobilesecurity;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CleanCacheActivity extends Activity {

	private static final String TAG = "CleanCacheActivity";
	private View loading;
	private PackageManager pm;
	private ProgressBar pb;
	private TextView tv_scan_status;
	private Map<String, Long> cacheInfo;
	private LinearLayout ll_cache;
	private InnerReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		
		receiver = new InnerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("edu.pitt.mobilesecurity.publish");
		registerReceiver(receiver, filter);
		loading = findViewById(R.id.loading);
		pb = (ProgressBar) findViewById(R.id.pb);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_cache = (LinearLayout) findViewById(R.id.ll_cache);
		pm = getPackageManager();
		cacheInfo = new HashMap<String, Long>();
		
		// load async data
		new AsyncTask<Void, Object, Void>() {

			@Override
			protected void onPreExecute() {
				loading.setVisibility(View.VISIBLE);
				tv_scan_status.setText("Scanning....");
				super.onPreExecute();
			}
			
			@Override
			protected void onPostExecute(Void result) {
				loading.setVisibility(View.INVISIBLE);
				tv_scan_status.setText("Finish Scanning...");
				
				// here we can also load data through map collection, but we want load whenever we scan it.
//				if (cacheInfo.size() > 0) {
//
//					for (Entry<String, Long> entry : cacheInfo.entrySet()) {
//						String packname = entry.getKey();
//						String size = Formatter.formatFileSize(
//								getApplicationContext(), entry.getValue());
//						TextView tv = new TextView(getApplicationContext());
//						tv.setTextSize(20);
//						tv.setTextColor(Color.BLACK);
//						tv.setText(packname + ":" + size);
//						ll_cache.addView(tv);
//					}
//				}
				super.onPostExecute(result);
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
				pb.setMax(packageInfos.size());
				int progress = 0;
				for (PackageInfo packageinfo : packageInfos) {
					String packname = packageinfo.packageName;
					try {
						Method method = PackageManager.class.getMethod("getPackageSizeInfo", 
								new Class[] {String.class, IPackageStatsObserver.class });
						method.invoke(pm, new Object[] { packname, new MyPackObserver(packname) });
					} catch (Exception e) {
						e.printStackTrace();
					}
					progress++;
					// sleep to make it clear
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					publishProgress(packageinfo);
					pb.setProgress(progress);
				}
				
				
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Object... values) {
				super.onProgressUpdate(values);
				PackageInfo packinInfo = (PackageInfo) values[0];
				tv_scan_status.setText("Scanning: " + packinInfo.applicationInfo.loadLabel(pm));
			}
		}.execute();
		
	}

	private class MyPackObserver extends IPackageStatsObserver.Stub {
		private String packageName;
		
		public MyPackObserver (String packageName) {
			this.packageName = packageName;
		}
		
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cache = pStats.cacheSize;
			
			//send broadcast to notify update so that textview could be added item by item
			if (cache > 0) {
				cacheInfo.put(packageName, cache);
				Intent intent = new Intent();
				intent.setAction("edu.pitt.mobilesecurity.publish");
				intent.putExtra("packageName", packageName);
				intent.putExtra("cache", cache);
				sendBroadcast(intent);
			}
		}
		
	}
	
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			final String packname = intent.getStringExtra("packageName");
			String size = Formatter.formatFileSize(context, intent.getLongExtra("cache", 0));
			// create the tv which will be inserted
			TextView tv = new TextView(getApplicationContext());
			tv.setTextSize(16);
			tv.setTextColor(Color.BLACK);
			tv.setText(packname + ":" + size);
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Go to clear cache system activity
					Intent intent = new Intent();
					// Version optimization here!
					if (Build.VERSION.SDK_INT >= 9) {
						intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						intent.setData(Uri.parse("package:" + packname));
						startActivity(intent);
					} else {
						intent.setAction(Intent.ACTION_VIEW);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						intent.addCategory("android.intent.category.VOICE_LAUNCH");
						intent.putExtra("pkg", packname);
						startActivity(intent);
					}
				}
			});
			
			ll_cache.addView(tv);
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}	
	
	public void cleanAll(View view) {
		// Reflection call system method to get cache
		Method deleteAllMethod = null;

		// Get the method 
		Method[]  methods = PackageManager.class.getMethods();
		for(Method method: methods){
			if("freeStorageAndNotify".equals(method.getName())){
				deleteAllMethod = method;
			}
		}
		
		// invoke, first parameter should be large enough
		try {
			deleteAllMethod.invoke(pm, new Object[]{Long.MAX_VALUE,new ClearCacheObserver()});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Toast.makeText(getApplicationContext(), "Cleanup!", 1).show();
		Intent intent = new Intent(this, CleanCacheActivity.class);
		startActivity(intent);
		finish();
	}
	
	
	private class ClearCacheObserver extends IPackageDataObserver.Stub{

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			Log.i(TAG, "result:"+succeeded);
		}
	}
	
}
