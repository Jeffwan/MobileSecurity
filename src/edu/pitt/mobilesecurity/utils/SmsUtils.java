package edu.pitt.mobilesecurity.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import edu.pitt.mobilesecurity.domain.SmsInfo;


import android.R.interpolator;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.Contacts.Intents.Insert;
import android.util.Xml;

public class SmsUtils {
	
	public interface BackUpStatusListener {
		
		public void beforeBackup(int max);
		
		public void onProgress(int progress);
	}
	
	public static void deleteAllsms(Context context) {
		Uri uri = Uri.parse("content://sms/");
		context.getContentResolver().delete(uri, null,null);
	}
	
	public static void restoreSms(Context context) throws Exception {
		Uri uri = Uri.parse("content://sms/");
		XmlPullParser parser = Xml.newPullParser();
		File file = new File(Environment.getExternalStorageDirectory(),"smsbackup.xml");
		FileInputStream fis = new FileInputStream(file);
		parser.setInput(fis, "utf-8");
		
		int type = parser.getEventType();
		SmsInfo smsInfo = null;
		
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("sms".equals(parser.getName())) {
					smsInfo = new SmsInfo();
				} else if ("address".equals(parser.getName())) {
					smsInfo.setAddress(parser.nextText());
				} else if ("body".equals(parser.getName())) {
					smsInfo.setBody(parser.nextText());
				} else if ("type".equals(parser.getName())) {
					smsInfo.setType(parser.nextText());
				}  else if ("date".equals(parser.getName())) {
					smsInfo.setDate(parser.nextText());
				}  
				break;
			
			case XmlPullParser.END_TAG:
				if ("sms".equals(parser.getName())) {
					ContentValues values = new ContentValues();
					values.put("address", smsInfo.getAddress());
					values.put("body", smsInfo.getBody());
					values.put("type", smsInfo.getType());
					values.put("date", smsInfo.getDate());
					context.getContentResolver().insert(uri, values);
				}
				break;
			}
			type = parser.next();	
		}
	}
	
	
	public static void backupSms(Context context, BackUpStatusListener listener) throws Exception {
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = context.getContentResolver().query(uri, 
				new String[]{"address","date","type","body"}, null, null, null);
		
		int max = cursor.getCount();
		listener.beforeBackup(max);
		
		// Initial xml parser
		File file = new File(Environment.getExternalStorageDirectory(), "smsbackup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", null);
		serializer.startTag(null, "smss");
		
		int total = 0;
		
		
		// store
		while(cursor.moveToNext()) {
			// <sms>
			serializer.startTag(null, "sms");
			
			// address
			String address = cursor.getString(0);
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");
			
			// date
			String date = cursor.getString(1);
			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			
			// type
			String type = cursor.getString(2);
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");

			//body
			String body = cursor.getString(3);
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");
			
			// </sms>
			serializer.endTag(null, "sms");
			Thread.sleep(100);
			total ++;
			listener.onProgress(total);
		};
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
		
	}

}
