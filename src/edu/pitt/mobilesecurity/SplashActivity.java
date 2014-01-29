package edu.pitt.mobilesecurity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpConnection;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import edu.pitt.mobilesecurity.domain.UpdateInfo;
import edu.pitt.mobilesecurity.engine.UpdateInfoParser;
import edu.pitt.mobilesecurity.utils.DownloadManager;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	
	private static final int PARSE_SUCCESS = 0;
	private static final int PARSE_ERROR = 1;
	private static final int SERVER_ERROR = 2;
	private static final int URL_ERROR = 3;
	private static final int NETWORK_ERROR = 4;
	private static final int DOWNLOAD_SUCCESS = 5;
	private static final int DOWNLOAD_FAIL = 6;
	private static final int SDCARD_ERROR = 7;

	
	
	protected static final String TAG = "SplashActivity";
	
	private TextView tv_splash_activity;

	
	private PackageManager pManager;
	private UpdateInfo updateInfo;
	private ProgressDialog progressDialog;
	
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PARSE_SUCCESS:
				Toast.makeText(getApplicationContext(), "parse successfully", 1).show();
				// check version 
				if (getAppVersion().equals(updateInfo.getVersion())) {
					// go to Home Activity
					loadHomeUI();
					Log.i(TAG, "same, go to Home");
				} else {
					// download and update
					showUpdateDialog();
					Log.i(TAG, "same, go to Home");
				}
				break;
			case PARSE_ERROR:
				Toast.makeText(getApplicationContext(), "parse fails", 1).show();
				loadHomeUI();
				break;
			case SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "server error", 1).show();
				loadHomeUI();
				break;
			case URL_ERROR:
				Toast.makeText(getApplicationContext(), "url error", 1).show();
				loadHomeUI();
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "network error", 1).show();
				loadHomeUI();
				break;
			case DOWNLOAD_FAIL:
				Toast.makeText(getApplicationContext(), "download error", 1).show();
				loadHomeUI();
				break;
			case SDCARD_ERROR:
				Toast.makeText(getApplicationContext(), "SDCARD error", 1).show();
				loadHomeUI();
				break;
			case DOWNLOAD_SUCCESS:
				File file = (File) msg.obj;
				installApk(file);
				break;
			}
			
		}
	
	};
	
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		// Set AlphaAnimation of SplashActivity -- should move to resource file!
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(2000);
		findViewById(R.id.rl_splash).setAnimation(aa);
		
		
		pManager = this.getPackageManager();
		tv_splash_activity = (TextView) findViewById(R.id.tv_splash_version);
		
		// Set version of local app to splash
		tv_splash_activity.setText("Version: "+ getAppVersion());
		
		// Check version info on Server
		new Thread(new CheckVersionTask()).start();
		
	}
	
	
	private void installApk(File file) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivity(intent);
		
	}
	
	private void showUpdateDialog(){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Update Hint");
		builder.setMessage(updateInfo.getDescription());
		builder.setPositiveButton("OK", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// progress bar configuration
				progressDialog = new ProgressDialog(SplashActivity.this);  // notice here's this
				progressDialog.setTitle("Update Hint");
				progressDialog.setMessage("Latest Version Downloading ....");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // you jin du tiao xianshi
				progressDialog.show();
				
				// new Thread for downloading latest version 
				new Thread(){
					public void run() {
						Message msg = Message.obtain();	
						try {
							File file = DownloadManager.download(updateInfo.getPath(), "/sdcard/new.apk",progressDialog);
							if(file!=null) {
								msg.what = DOWNLOAD_SUCCESS;
								msg.obj = file;
							} else {
								msg.what = DOWNLOAD_FAIL;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							msg.what = SDCARD_ERROR;
						} finally {
							handler.sendMessage(msg);
							progressDialog.dismiss();
						}
					}					
				}.start();				
				
			}
			
		});
		
		builder.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				loadHomeUI();
				
			}
		});
		
		builder.show();
	}
	
	private class CheckVersionTask implements Runnable{
		@Override
		public void run() {
			
			// Check if user open automatic update
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean update = sp.getBoolean("autoUpdate", false);
			if (!update) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadHomeUI();
				return;
			}

			
			// sleep for 2000ms to prevent splash screen (network well)
			long startTime = System.currentTimeMillis();
			
			Message msg = Message.obtain();
			
			try {
				URL url = new URL(getApplicationContext().getResources().getString(R.string.serverurl));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(3000);
				int responseCode = conn.getResponseCode();
				if (responseCode == 200) {
					// http request successful
					InputStream is = conn.getInputStream();
					updateInfo = UpdateInfoParser.getUpdateInfo(is);
					if (updateInfo!=null) {
						msg.what = PARSE_SUCCESS;
					} else {
						msg.what = PARSE_ERROR;
					}
				} else {
					// http request fail
					msg.what = SERVER_ERROR;
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
				msg.what = URL_ERROR;
			} catch (NotFoundException e) {
				e.printStackTrace();
				msg.what = URL_ERROR;
			} catch (IOException e) {
				e.printStackTrace();
				msg.what = NETWORK_ERROR;
			} finally {
				// check the time used for update
				long endTime = System.currentTimeMillis();
				long duration = endTime - startTime;
				if (duration < 2000) {
					try {
						Thread.sleep(2000 - duration);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} 
				handler.sendMessage(msg);
			}
			
		}
		
	}
	
	
	private String getAppVersion() {
		try {
			PackageInfo packageInfo = pManager.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";  //can't reach
		}
	}
	
	public void loadHomeUI(){
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		
		// remove current activity from task stack so that user will not see this when they click return 
		finish(); 
	}

	

}
