package edu.pitt.mobilesecurity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class AtoolsActivity extends Activity implements OnClickListener {

	private LinearLayout ll_atools_number_query;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		
		ll_atools_number_query = (LinearLayout) findViewById(R.id.ll_atools_number_query);
		ll_atools_number_query.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.ll_atools_number_query:
			Intent intent = new Intent(this, NumberQueryActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}


}
