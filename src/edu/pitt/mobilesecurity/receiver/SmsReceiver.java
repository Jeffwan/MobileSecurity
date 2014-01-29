package edu.pitt.mobilesecurity.receiver;

import edu.pitt.mobilesecurity.R;
import edu.pitt.mobilesecurity.engine.GPSInfoProvider;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Object[] objs = (Object[]) intent.getExtras().get("pdus"); //pdu is type of messages
		for(Object obj : objs) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String sender = smsMessage.getOriginatingAddress();
			String body = smsMessage.getMessageBody();
			
			if ("#*location*#".equals(body)) {
				Log.i(TAG, "#*location*#");
				String location = GPSInfoProvider.getInstance(context).getLastLocation();
				if(TextUtils.isEmpty(location)) {
					android.telephony.SmsManager.getDefault().sendTextMessage(sender, null, "can't get location now", null, null);
				} else {
					android.telephony.SmsManager.getDefault().sendTextMessage(sender, null, location, null, null);
				}
				
			} else if ("#*alarm*#".equals(body)) {
				Log.i(TAG, "#*alarm*#");
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setLooping(false); // set true in real
				player.setVolume(1.0f, 1.0f);
				player.start();
				
				abortBroadcast();
				
			} else if ("#*wipedate*#".equals(body)) {
				Log.i(TAG, "#*wipedate*#");
				DevicePolicyManager dpManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				dpManager.wipeData(0);
				abortBroadcast();
			} else if ("#*lockscreen*#".equals(body)) {
				Log.i(TAG, "#*wipedate*#");
				DevicePolicyManager dpManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				dpManager.resetPassword("123", 0);
				dpManager.lockNow();
				abortBroadcast();
			}
		}
	}

}
