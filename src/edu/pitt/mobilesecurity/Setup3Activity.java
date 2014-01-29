package edu.pitt.mobilesecurity;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {

	private EditText et_setup3_safenumber;
	private String contactName;
	private String contactNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_setup3_safenumber = (EditText) findViewById(R.id.et_setup3_safenumber);
		et_setup3_safenumber.setText(mSharedPreferences.getString("safenumber", ""));

	}

	@Override
	public void showNext() {
		contactNumber = et_setup3_safenumber.getText().toString().trim();
		if (TextUtils.isEmpty(contactNumber)) {
			Toast.makeText(getApplicationContext(), "Select an Contact!", 1).show();
			return;
		}
		
		// save safe number
		Editor editor = mSharedPreferences.edit();
		editor.putString("safenumber", contactNumber);
		editor.commit();
		
		Intent intent = new Intent(this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_next_in, R.anim.trans_next_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}

	
	public void selectContact(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
             Uri contactData = data.getData();
             @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
             cursor.moveToFirst();
             contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
                     null, 
                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, 
                     null, 
                     null);
             while (phone.moveToNext()) {
                 contactNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                 et_setup3_safenumber.setText(contactNumber);
             }

         }
	
	}
	
	
	
	
}
