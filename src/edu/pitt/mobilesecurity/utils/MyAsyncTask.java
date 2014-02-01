package edu.pitt.mobilesecurity.utils;

import android.os.Handler;
import android.os.Message;

public abstract class MyAsyncTask {

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			onPostExectue();
		};
	};
	
	protected abstract void onPreExectue();
	
	protected abstract void doInBackground();
	
	protected abstract void onPostExectue();
	
	public void execute() {
		onPreExectue();
		new Thread(){
			public void run() {
				doInBackground();
				Message msg = new Message();
				handler.sendMessage(msg);
			};
		}.start();
	}
}
