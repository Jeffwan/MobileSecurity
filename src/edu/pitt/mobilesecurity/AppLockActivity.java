package edu.pitt.mobilesecurity;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.mobilesecurity.db.dao.AppLockDao;
import edu.pitt.mobilesecurity.domain.AppInfo;
import edu.pitt.mobilesecurity.engine.AppInfoProvider;
import edu.pitt.mobilesecurity.utils.MyAsyncTask;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppLockActivity extends Activity implements OnClickListener {
	private TextView tv_unlock, tv_locked;
	private TextView tv_unlock_count ,tv_locked_count;
	private LinearLayout ll_unlock, ll_locked;

	private ListView lv_unlock, lv_locked;

	private View loading;

	private List<AppInfo> allAppInfos;
	

	private List<AppInfo> unlockAppInfos;
	

	private List<AppInfo> lockedAppInfos;
	
	private AppLockDao dao;
	
	private AppLockAdapter unlockAdapter;
	private AppLockAdapter lockedAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_locked = (TextView) findViewById(R.id.tv_locked);
		tv_unlock_count = (TextView) findViewById(R.id.tv_unlock_count);
		tv_locked_count = (TextView) findViewById(R.id.tv_locked_count);
		ll_locked = (LinearLayout) findViewById(R.id.ll_locked);
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		tv_locked.setOnClickListener(this);
		tv_unlock.setOnClickListener(this);
		lv_locked = (ListView) findViewById(R.id.lv_locked);
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		loading = findViewById(R.id.loading);
		dao = new AppLockDao(this);
		
		lv_unlock.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				final AppInfo appInfo = (AppInfo) lv_unlock.getItemAtPosition(position);
				TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				ta.setDuration(200);
				view.startAnimation(ta);
				
				new MyAsyncTask() {
	
					@Override
					protected void onPreExectue() {
						String packname = appInfo.getPackName();
						dao.add(packname);
						
					}
	
					@Override
					protected void onPostExectue() {
						// update 
						unlockAdapter.removeAppInfo(appInfo);
						lockedAdapter.addAppInfo(appInfo);
					}
					
					@Override
					protected void doInBackground() {
						try {
							Thread.sleep(210);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}.execute();
			}
		});
		
		lv_locked.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				final AppInfo appInfo = (AppInfo) lv_locked.getItemAtPosition(position);
				TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				ta.setDuration(500);
				view.startAnimation(ta);
				new MyAsyncTask() {

					@Override
					protected void onPreExectue() {
						String packname = appInfo.getPackName();
						dao.delete(packname);
					}

					@Override
					protected void onPostExectue() {
						// update current list
						lockedAdapter.removeAppInfo(appInfo);
						unlockAdapter.addAppInfo(appInfo);
					}
					
					@Override
					protected void doInBackground() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}

				}.execute();
				
			}
		});
		
		
		new MyAsyncTask() {
			
			@Override
			protected void onPreExectue() {
				loading.setVisibility(View.VISIBLE);
				
			}
			
			@Override
			protected void onPostExectue() {
				loading.setVisibility(View.INVISIBLE);
				unlockAdapter = new AppLockAdapter(true, unlockAppInfos);
				lv_unlock.setAdapter(unlockAdapter);
				
				lockedAdapter = new AppLockAdapter(false, lockedAppInfos);
				lv_locked.setAdapter(lockedAdapter);
				
			}
			
			@Override
			protected void doInBackground() {
				allAppInfos = AppInfoProvider
						.getAppInfos(getApplicationContext());
				unlockAppInfos = new ArrayList<AppInfo>();
				lockedAppInfos = new ArrayList<AppInfo>();
				for(AppInfo appinfo: allAppInfos){
					if(dao.find(appinfo.getPackName())){
						lockedAppInfos.add(appinfo);
					}else{
						unlockAppInfos.add(appinfo);
					}
				}
				
			}
		}.execute();
		
		
	}

	
	private class AppLockAdapter extends BaseAdapter {
		private boolean unlock;
		private List<AppInfo> appInfos;
		
		public void removeAppInfo(AppInfo appinfo){
			this.appInfos.remove(appinfo);
			this.notifyDataSetChanged();
		}
		
		public void addAppInfo(AppInfo appinfo){
			this.appInfos.add(appinfo);
			this.notifyDataSetChanged();
		}

		// we use same adapter for lock and unlocked apps, so we need unlock info of data in.
		public AppLockAdapter(boolean unlock, List<AppInfo> appInfos) {
			this.unlock = unlock;
			this.appInfos = appInfos;
		}
		
		
		@Override
		public int getCount() {
			if(unlock){
				tv_unlock_count.setText("UnLocked Apps: " + appInfos.size());
			}else{
				tv_locked_count.setText("Locked Apps: " + appInfos.size());
			}
			return appInfos.size();
		}
		
		@Override
		public Object getItem(int position) {
			return appInfos.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appinfo = appInfos.get(position);
			ViewHolder holder;
			View view;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_applock_unlock, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_applock_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_applock_name);
				holder.iv_status = (ImageView) view.findViewById(R.id.iv_applock_status);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(appinfo.getAppIcon());
			holder.tv_name.setText(appinfo.getAppName());
			if (unlock) {
				holder.iv_status.setImageResource(R.drawable.lock);
			}else{
				holder.iv_status.setImageResource(R.drawable.unlock);
			}
			return view;
		}
	}
	

	static class ViewHolder {
		TextView tv_name;
		ImageView iv_icon;
		ImageView iv_status;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_locked:
			tv_locked.setBackgroundResource(R.drawable.tab_right_pressed);
			tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
			ll_locked.setVisibility(View.VISIBLE);
			ll_unlock.setVisibility(View.INVISIBLE);

			break;

		case R.id.tv_unlock:
			tv_locked.setBackgroundResource(R.drawable.tab_right_default);
			tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
			ll_locked.setVisibility(View.INVISIBLE);
			ll_unlock.setVisibility(View.VISIBLE);

			break;
		}
	}

	
	
}
