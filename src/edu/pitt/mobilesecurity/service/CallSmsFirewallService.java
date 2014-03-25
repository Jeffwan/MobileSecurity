package edu.pitt.mobilesecurity.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import edu.pitt.mobilesecurity.CallSmsSafeActivity;
import edu.pitt.mobilesecurity.R;
import edu.pitt.mobilesecurity.db.dao.BlackNumberDao;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
		filter.setPriority(2000);
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
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String sender = smsMessage.getOriginatingAddress();
				String body = smsMessage.getMessageBody();
				Log.i(TAG, "message is comming from " + sender);
								
				String blockMode = dao.findMode(sender);
				if ("1".equals(blockMode) || "3".equals(blockMode)) {
					Log.i(TAG, "Msg From blackNumber List, abort!");
					abortBroadcast();
				}
			}
		}		
	}
	
	
	private class PhoneListener extends PhoneStateListener {
		long startTime = 0;
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				Log.i(TAG, "Phone is comming");
				startTime = System.currentTimeMillis();
				String mode = dao.findMode(incomingNumber);
				if ("1".equals(mode) || "2".equals(mode)) {
					Log.i(TAG, "Block this number");
					Uri uri = Uri.parse("content://call_log/calls/");
					// register first and then end call.
					getContentResolver().registerContentObserver(uri, true, new CallLogObserver(new Handler(), incomingNumber));
					endCall();
				}
							
				break;
			
			case TelephonyManager.CALL_STATE_IDLE:
				long endTime = System.currentTimeMillis();
				if (endTime - startTime > 2000) {
					showNotification(incomingNumber);
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

	public void showNotification(String incomingNumber) {
		// 1. create Notification Manger
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// 2. create Notification object
		Notification notification = new Notification(R.drawable.notification, "Find suspicious number", System.currentTimeMillis());
		
		// 3. configure notification
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(this, CallSmsSafeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, "Hint: Suspicious Number", "Number: " + incomingNumber, contentIntent);
		
		// 4. popup notification
		mNotificationManager.notify(0, notification);
		
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
