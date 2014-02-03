package edu.pitt.mobilesecurity;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.mobilesecurity.domain.TaskInfo;
import edu.pitt.mobilesecurity.engine.TaskInfoProvider;
import edu.pitt.mobilesecurity.utils.MyAsyncTask;
import edu.pitt.mobilesecurity.utils.TaskUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity {

	protected static final String TAG = "TaskManagerActivity";
	private TextView tv_taskmanager_count;
	private TextView tv_taskmanger_mem;
	private int runningProcessCount;
	private long availMemory;
	private ListView lv_taskmanager;
	private View loading;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;

	private TaskAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		
		tv_taskmanager_count = (TextView) findViewById(R.id.tv_taskmanager_count);
		tv_taskmanger_mem = (TextView) findViewById(R.id.tv_taskmanger_mem);
		
		runningProcessCount = TaskUtils.getRunningProcessCount(this);
		availMemory = TaskUtils.getAvailableRam(this);
		
		tv_taskmanager_count.setText("RunningTasks: " + runningProcessCount);
		tv_taskmanger_mem.setText("AvaiMemory: "+ Formatter.formatFileSize(this, availMemory));
		
		loading = findViewById(R.id.loading);
		
		lv_taskmanager = (ListView) findViewById(R.id.lv_taskmanager);
		
		loadTaskData();
		
		
		lv_taskmanager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object obj = lv_taskmanager.getItemAtPosition(position);
				
				if (obj != null && obj instanceof TaskInfo) {
					TaskInfo taskInfo = (TaskInfo) obj;
					// Move self --> make it unclickable
					if (taskInfo.getPackName().equals(getPackageName())) {
						return;
					}
					
					CheckBox cb = (CheckBox) view.findViewById(R.id.cb_taskmanager);
					if (taskInfo.isChecked()) {
						// change the bean
						taskInfo.setChecked(false);
						// change UI
						cb.setChecked(false);
					} else {
						taskInfo.setChecked(true);
						cb.setChecked(true);
					}	
				}
				
			}
		});
		

		
	}

	
	private void loadTaskData() {
		new MyAsyncTask() {

			@Override
			protected void onPreExectue() {
				loading.setVisibility(View.VISIBLE);
				
			}

			@Override
			protected void onPostExectue() {
				loading.setVisibility(View.INVISIBLE);
				adapter = new TaskAdapter();
				lv_taskmanager.setAdapter(adapter);	
			}

			@Override
			protected void doInBackground() {
				List<TaskInfo> taskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : taskInfos) {
					if (taskInfo.isUserTask()) {
						userTaskInfos.add(taskInfo);
					} else {
						systemTaskInfos.add(taskInfo);
					}
				}
			}
			
		}.execute();
		
	}


	private class TaskAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			SharedPreferences mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
			boolean showSystem = mSharedPreferences.getBoolean("showSystem", true);
			
			if (showSystem) {
				return userTaskInfos.size() + systemTaskInfos.size() + 2;
			} else {
				return userTaskInfos.size() + 1;
			}
			
		}

		@Override
		public Object getItem(int position) {
			TaskInfo taskInfo;
			if (position == 0 || position == (userTaskInfos.size() + 1)) {
				return null;
			} else if (position <= userTaskInfos.size()) {
				taskInfo = userTaskInfos.get(position - 1);
			} else {
				int newPostion = position - 1 - userTaskInfos.size() - 1;
				taskInfo =  systemTaskInfos.get(newPostion);
			}
			
			return taskInfo;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo taskInfo = null;
			int newPosition;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("UserTask: " + userTaskInfos.size());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.BLACK);
				return tv;
				
			} else if (position == (userTaskInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("SystemTask: " + systemTaskInfos.size());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.BLACK);
				return tv;
				
			} else if (position <= userTaskInfos.size()) {
				taskInfo = userTaskInfos.get(position - 1);
				
			} else {
				newPosition = position - 1 - userTaskInfos.size() - 1;
				taskInfo = systemTaskInfos.get(newPosition);
			}
			
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView; 
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(), R.layout.list_taskmanager_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_taskmanger_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_taskmanager_name);
				holder.tv_mem = (TextView) view.findViewById(R.id.tv_taskmanager_mem);
				holder.cb = (CheckBox) view.findViewById(R.id.cb_taskmanager);
				view.setTag(holder);
			}
			
			holder.iv_icon.setImageDrawable(taskInfo.getAppIcon());
			holder.tv_name.setText(taskInfo.getAppName());
			holder.tv_mem.setText("Size: " + Formatter.formatFileSize(getApplicationContext(), taskInfo.getMemSize()));
			
			// solve the cache problem of converView
			holder.cb.setChecked(taskInfo.isChecked());
			if (getPackageName().equals(taskInfo.getPackName())) {
				holder.cb.setVisibility(View.INVISIBLE);
			} else {
				holder.cb.setVisibility(View.VISIBLE);
			}
			
			return view;
		}
		
	}
	
	static class ViewHolder {
		TextView tv_name;
		TextView tv_mem;
		ImageView iv_icon;
		CheckBox cb;
	}
	
	
	public void killAllTasks (View view) {
		ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<TaskInfo> killedTasks = new ArrayList<TaskInfo>();
		long savedMemory = 0;
		
		for (TaskInfo info : userTaskInfos) {
			if (info.isChecked()) {
				mActivityManager.killBackgroundProcesses(info.getPackName());
				killedTasks.add(info);
				savedMemory += info.getMemSize();
				
			}
		}
		
		for (TaskInfo info : systemTaskInfos) {
			if (info.isChecked()) {
				mActivityManager.killBackgroundProcesses(info.getPackName());
				killedTasks.add(info);
				savedMemory += info.getMemSize();
			}
		}
		
		for (TaskInfo info : killedTasks) {
			if (info.isUserTask()) {
				userTaskInfos.remove(info);
			} else {
				systemTaskInfos.remove(info);
			}
		}

		
		String text = "You killed " 
				+ killedTasks.size() + " Tasks and saved " 
				+ Formatter.formatFileSize(getApplicationContext(), savedMemory) 
				+ " memory";
		
		Toast.makeText(this, text, 1).show();
		
		
		// update UI
		adapter.notifyDataSetChanged();
		runningProcessCount -= killedTasks.size();
		availMemory += savedMemory;
		tv_taskmanager_count.setText("RunningTasks: " + runningProcessCount);
		tv_taskmanger_mem.setText("AvaiMemory: " + Formatter.formatFileSize(getApplicationContext(), availMemory)); 
		
	}
	
}
