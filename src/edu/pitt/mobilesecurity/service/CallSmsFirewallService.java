package edu.pitt.mobilesecurity.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import edu.pitt.mobilesecurity.db.dao.BlackNumberDao;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;


public class CallSmsFirewallService extends Service {

	private BlackNumberDao dao;
	private ComeSmsReceiver comeSmsReceiver;
	private TelephonyManager mTelephonyManager;
	private PhoneListener phoneListener;
	
	public static final String TAG = "CallSmsFirewallService";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		dao = new BlackNumberDao(this);
		
		// 1. Register sms block receiver
		comeSmsReceiver = new ComeSmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);
		this.registerReceiver(comeSmsReceiver, filter);
		
		
		// 2. register phone block Listener
		phoneListener = new PhoneListener();
		mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mTelephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	private class ComeSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj : objs) {
				Log.i(TAG, "message comming!");
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String sender = smsMessage.getOriginatingAddress();
				String body = smsMessage.getMessageBody();
				
				if (body.contains("sex")) {
					Log.i(TAG, "rubbish message, abort!");
					abortBroadcast();
				}
				
				String blockMode = dao.findMode(sender);
				if ("1".equals(blockMode) || "3".equals(blockMode)) {
					Log.i(TAG, "Msg From blackNumber List, abort!");
					abortBroadcast();
				}
				
			}
			
		}		
	}
	
	
	private class PhoneListener extends PhoneStateListener {
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				Log.i(TAG, "Phone is comming");
				String mode = dao.findMode(incomingNumber);
				if ("1".equals(mode) || "2".equals(mode)) {
					Log.i(TAG, "Block this number");
					Uri uri = Uri.parse("content://call_log/calls/");
					// register first and then end call.
					getContentResolver().registerContentObserver(uri, true, new CallLogObserver(new Handler(), incomingNumber));
					endCall();
				}
							
				break;

			default:
				break;
			}
			
			super.onCallStateChanged(state, incomingNumber);
		}
	}
	
	private class CallLogObserver extends ContentObserver {
		String incomingNumber;
		
		public CallLogObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			deleteCallLog(incomingNumber);
			
			// unregister to release memory (if not unregister, other normal number may be deleted)
			getContentResolver().unregisterContentObserver(this);
		}
		
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(comeSmsReceiver);
		comeSmsReceiver = null;
		
	}

	// Use ContentResolver to delete call log
	public void deleteCallLog(String incomingNumber) {
		Uri uri = Uri.parse("content://call_log/calls/");
		getContentResolver().delete(uri, "number=?", new String[]{ incomingNumber });
		
	}

	public void endCall() {
		try {
			Class clazz = getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getMethod("getService", new Class[] {String.class});
			IBinder iBinder = (IBinder) method.invoke(null, new String[] {TELEPHONY_SERVICE});
			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
			iTelephony.endCall();
			Log.i(TAG, "Phone comes from blacklist, abort!");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
