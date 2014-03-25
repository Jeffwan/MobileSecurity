package edu.pitt.mobilesecurity;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class SystemOptActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_opt);
		
		TabHost tabHost = getTabHost();
		TabSpec tab1 = tabHost.newTabSpec("tab1");
		TabSpec tab2 = tabHost.newTabSpec("tab2");
		
		tab1.setIndicator("CacheClean");
		tab2.setIndicator("SDCardClean");
		tab1.setContent(new Intent(this,CleanCacheActivity.class));
		tab2.setContent(new Intent(this,CleanSDActivity.class));
		
		// Add Tab to tabHost
		tabHost.addTab(tab1);
		tabHost.addTab(tab2);
		
	}
}
