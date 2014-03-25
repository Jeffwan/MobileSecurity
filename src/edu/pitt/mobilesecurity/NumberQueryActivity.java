package edu.pitt.mobilesecurity;

import edu.pitt.mobilesecurity.db.dao.AddressDao;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberQueryActivity extends Activity {

	private EditText et_query_number;
	private TextView tv_query_result;
	
	// vibrate phone Manger
	private Vibrator vibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_query);
		
		et_query_number = (EditText) findViewById(R.id.et_query_number);
		tv_query_result = (TextView) findViewById(R.id.tv_query_result);
		
		// vibrator makes app exit unnormally, we don't use it here.
//		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
//		
//		et_query_number.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				String address = AddressDao.getAddress(s.toString().trim());
//				tv_query_result.setText(address);
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//					int arg3) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void afterTextChanged(Editable arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
	}

	public void query(View view) {
		String number = et_query_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			// share effect of EditText if input is empty 
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_query_number.startAnimation(shake);
			// vibrator
			vibrator.vibrate(100);
			
			Toast.makeText(this, "Number cannot be empty", 1).show();
			return;
		}
		
		String address = AddressDao.getAddress(number);
		tv_query_result.setText(address);
	}

}
