package edu.pitt.mobilesecurity;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.mobilesecurity.db.dao.AntiVirusDao;
import edu.pitt.mobilesecurity.utils.MD5Utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {

	private ImageView iv_scan;
	private ProgressBar progressBar1;
	private TextView scan_status;
	private PackageManager mPackageManager;
	private LinearLayout ll_container;
	private List<PackageInfo> virusInfos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_anti_virus);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.startAnimation(ra);
		
		mPackageManager = getPackageManager();

		new AsyncTask<Void, Object, Void>() {

			@Override
			protected void onPreExecute() {
				scan_status.setText("Initial Anti-Virus Engine...");
				virusInfos = new ArrayList<PackageInfo>();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				scan_status.setText("Finish Scanning");
				// stop twist after scanning
				iv_scan.clearAnimation();
				if (virusInfos.size() > 0) {
					AlertDialog.Builder builder = new Builder(AntiVirusActivity.this);
					builder.setTitle("Hint!");
					builder.setMessage("Do you like to kill these virus ?");
					builder.setPositiveButton("OK", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Go to uninstall activity
							for (PackageInfo info : virusInfos) {
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_DELETE);
								intent.setData(Uri.parse("package:" + info.packageName));
								startActivity(intent);
							}
						}
					});
					
					builder.setNegativeButton("Cancel", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					builder.show();
				}
				super.onPostExecute(result);
			}

			@Override
			protected Void doInBackground(Void... params) {
				
				try {
					Thread.sleep(1000);
					
					List<PackageInfo> infos = mPackageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
					progressBar1.setMax(infos.size());
					int progress = 0;
					for (PackageInfo info : infos) {
						String md5 = MD5Utils.encode(info.signatures[0].toCharsString());
						String result = AntiVirusDao.findVirus(md5);
						publishProgress(info, result);
						progress++;
						progressBar1.setProgress(progress);
						
						Thread.sleep(20);
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(Object... values) {
				PackageInfo packageInfo = (PackageInfo) values[0];
				Object obj = values[1];
				TextView tv = new TextView(getApplicationContext());
				if (obj != null) {
					// find virus
					tv.setText(packageInfo.applicationInfo.loadLabel(mPackageManager) + "  -- Find Virus!");
					tv.setTextColor(Color.RED);
					virusInfos.add(packageInfo);
				} else {
					// file safe
					tv.setText(packageInfo.applicationInfo.loadLabel(mPackageManager) + "  -- Safe");
					tv.setTextColor(Color.BLACK);
				}
				ll_container.addView(tv, 0);
				scan_status.setText("Scanning: " + packageInfo.applicationInfo.loadLabel(mPackageManager));
				super.onProgressUpdate(values);
			}
		}.execute();
		
	}

	
}
