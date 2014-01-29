package edu.pitt.mobilesecurity.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	public static final String path = "/data/data/edu.pitt.mobilesecurity/files/address.db";
	
	/**
	 * 
	 * @param number
	 * @return location of this number
	 */
	public static String getAddress(String number) {
		String address = "Can't find number in DB";
		
		// Query Database for result
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		
		if (number.matches("^1[3458]\\d{9}$")) {
			String sql = "select location from data2 where id = (select outkey from data1 where id=?)";
			String[] queryData = new String[] {number.substring(0,7)};
			Cursor cursor = db.rawQuery(sql, queryData);
			if(cursor.moveToFirst()) {
				address = cursor.getString(0);
			}
			cursor.close();
		} else {
			switch (number.length()) {
			case 3:
				
				break;
			case 4:

				break;
			case 5:

				break;
			case 6:

				break;
			case 7:

				break;

			default:
				// a little like UPMC's, need more time to consider different.
				if(number.length()>=10&&number.startsWith("0")){
					Cursor cursor = db.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 3)});
					if(cursor.moveToFirst()){
						String str = cursor.getString(0);
						address = str.substring(0, str.length()-2);
					}
					cursor.close();
					cursor = db.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 4)});
					if(cursor.moveToFirst()){
						String str = cursor.getString(0);
						address = str.substring(0, str.length()-2);
					}
					cursor.close();
				}
				
				break;
			}
		}
		
		db.close();
		return address;
		
	}
}
