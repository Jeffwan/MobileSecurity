package edu.pitt.mobilesecurity;

import edu.pitt.mobilesecurity.utils.SmsUtils;
import edu.pitt.mobilesecurity.utils.SmsUtils.BackUpStatusListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AtoolsActivity extends Activity implements OnClickListener {

	private LinearLayout ll_atools_number_query;
	private LinearLayout ll_atools_sms_backup;
	private LinearLayout ll_atools_sms_restore;
	private TextView tv_progress;
	private ProgressDialog mProgressDialog;
	
	private LinearLayout ll_atools_applock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		
		ll_atools_number_query = (LinearLayout) findViewById(R.id.ll_atools_number_query);
		ll_atools_sms_backup = (LinearLayout) findViewById(R.id.ll_atools_sms_backup);
		ll_atools_sms_restore = (LinearLayout) findViewById(R.id.ll_atools_sms_restore);
		tv_progress = (TextView) findViewById(R.id.tv_progress);
		ll_atools_number_query.setOnClickListener(this);
		ll_atools_sms_backup.setOnClickListener(this);
		ll_atools_sms_restore.setOnClickListener(this);
		
		
		ll_atools_applock = (LinearLayout) findViewById(R.id.ll_atools_applock);
		ll_atools_applock.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.ll_atools_number_query:
			Intent intent = new Intent(this, NumberQueryActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_atools_sms_backup:
			new AsyncTask<Void, Integer, Boolean>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					mProgressDialog = new ProgressDialog(AtoolsActivity.this);
					mProgressDialog.setTitle("Hint");
					mProgressDialog.setMessage("Msg Backing...");
					mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					mProgressDialog.show();
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					mProgressDialog.dismiss();
					if (result) {
						Toast.makeText(getApplicationContext(), "Back Success!", 1).show();
					} else {
						Toast.makeText(getApplicationContext(), "Back Fail!", 1).show();
					}
				}

				@Override
				protected void onProgressUpdate(Integer... values) {
					tv_progress.setText("Backuping: "+values[0]);
					super.onProgressUpdate(values);
				}

				@Override
				protected Boolean doInBackground(Void... params) {
					try {
						SmsUtils.backupSms(AtoolsActivity.this, new BackUpStatusListener() {
							
							@Override
							public void onProgress(int progress) {
								mProgressDialog.setProgress(progress);
								publishProgress(progress);
								
							}
							
							@Override
							public void beforeBackup(int max) {
								mProgressDialog.setMax(max);
								
							}
						});
						
						return true;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}					
				}
			}.execute();
			break;
			
		case R.id.ll_atools_sms_restore:
			AlertDialog.Builder builder = new Builder(AtoolsActivity.this);
			builder.setTitle("Restore Message");
			builder.setMessage("Would you like to remove old message?");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SmsUtils.deleteAllsms(getApplicationContext());
					Toast.makeText(getApplicationContext(), "All old msg deleted", 0).show();
					try {
						SmsUtils.restoreSms(getApplicationContext());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					try {
						SmsUtils.restoreSms(getApplicationContext());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
			});
			builder.show();
			break;
			
		case R.id.ll_atools_applock:
			intent = new Intent(this,AppLockActivity.class);
			startActivity(intent);
			break;
			
		}
	}


}
