package edu.pitt.mobilesecurity.adapter;

import edu.pitt.mobilesecurity.R;
import android.content.Context;
import android.text.TextUtils.StringSplitter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeAdapter extends BaseAdapter {

	private static final String TAG = "HomeAdapter";

	private Context context;
	
	public HomeAdapter(Context context) {
		this.context = context;
	}
	
	private String[] names = {"AntiTheft","CallMgt","SoftMgt", "TaskMgt",
			"NetMgt","AntiVirus","SysOptimize","ATools","Settings"}; 
	
	private int[] icons = {R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app, 
			R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan, 
			R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings}; 
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		// Log.i(TAG, "getView"+ position); -- Google bugs here.
		View view = View.inflate(context, R.layout.grid_home_item, null);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_home_item_icon);
		TextView tv = (TextView) view.findViewById(R.id.tv_home_item_name);
		iv.setImageResource(icons[position]);
		tv.setText(names[position]);
		
		return view;
	}

}
