package edu.pitt.mobilesecurity;

import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import edu.pitt.mobilesecurity.db.dao.BlackNumberDao;
import edu.pitt.mobilesecurity.domain.BlackNumberInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsSafeActivity extends Activity {

	private ListView lv_callsms_safe;
	private BlackNumberDao dao;
	private List<BlackNumberInfo> blackNumberInfos;
	private CallSmsSafeAdapter callSmsSafeAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		dao = new BlackNumberDao(this);
		blackNumberInfos = dao.findAll();
		callSmsSafeAdapter = new CallSmsSafeAdapter();
		lv_callsms_safe.setAdapter(callSmsSafeAdapter);
		
	}

	private class CallSmsSafeAdapter extends BaseAdapter {

		protected static final String TAG = "CallSmsSafeAdapter";

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return blackNumberInfos.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.list_call_sms_item, null);
			TextView tv_mode = (TextView) view.findViewById(R.id.tv_call_sms_mode);
			TextView tv_number = (TextView) view.findViewById(R.id.tv_call_sms_number);
			ImageView iv_deleteImageView = (ImageView) view.findViewById(R.id.iv_call_sms_delete);
			
			// Set Data
			BlackNumberInfo info = blackNumberInfos.get(position);
			tv_number.setText(info.getNumber());
			
			iv_deleteImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 1. delete from database
					BlackNumberInfo info = blackNumberInfos.get(position);
					dao.delete(info.getNumber());
					// 2. delete from list
					blackNumberInfos.remove(position);
					// 3. nofity UI update
					callSmsSafeAdapter.notifyDataSetChanged();
				}
			});
			
			
			switch (Integer.parseInt(info.getMode())) {
			case 1:
				tv_mode.setText("All Blocked");
				break;
				
			case 2:
				tv_mode.setText("Phone Blocked");
				break;
			
			case 3:
				tv_mode.setText("Msg Blocked");
				break;
				
			default:
				break;
			}
			
			return view;
		}
		
	}
	
	/**
	 * Add a Black Number
	 */
	private EditText et_number;
	private Button bt_ok;
	private Button bt_cancel;
	private RadioGroup rg_mode;
	private AlertDialog dialog;
	
	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		View dialogView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		et_number = (EditText) dialogView.findViewById(R.id.et_black_number);
		rg_mode = (RadioGroup) dialogView.findViewById(R.id.rg_mode);
		bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
		bt_ok = (Button) dialogView.findViewById(R.id.bt_ok);
		
		bt_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String blackNumber = et_number.getText().toString().trim();
				if (TextUtils.isEmpty(blackNumber)) {
					Toast.makeText(getApplicationContext(), "Black Number cannot be empty", 0).show();
					return;
				}
				
				if (dao.find(blackNumber)) {
					Toast.makeText(getApplicationContext(), "Black Number exists", 0).show();
					return;
				}
				
				int id = rg_mode.getCheckedRadioButtonId();
				String mode = "1";
				switch (id) {
				case R.id.rb_all:
					mode = "1";
					break;
				case R.id.rb_phone:
					mode = "2";
					break;

				case R.id.rb_sms:
					mode = "3";
					break;
				}
				
				// 1. Add to database
				dao.add(blackNumber, mode);
				// 2. Update UI
				blackNumberInfos.add(0,new BlackNumberInfo(blackNumber, mode));
				callSmsSafeAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		
		
		builder.setView(dialogView);
		dialog = builder.show();
	}

}
