package edu.pitt.mobilesecurity.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {
	public static final String path = "/data/data/edu.pitt.mobilesecurity/files/antivirus.db";
	
	/**
	 * 
	 * @param number
	 * @return location of this number
	 */
	public static String findVirus(String md5) {
		String result = null;
		
		// Query Database for result
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select name,desc from datable where md5=?", new String[]{ md5 });
		if (cursor.moveToNext()) {
			result = cursor.getString(0) + "\n" + cursor.getString(1);
 		}
		cursor.close();
		db.close();
		return result;
		
	}
}
