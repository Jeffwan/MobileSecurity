package edu.pitt.mobilesecurity.db.dao;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.mobilesecurity.db.BlackNumberDBHelper;
import edu.pitt.mobilesecurity.domain.BlackNumberInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.AvoidXfermode.Mode;

public class BlackNumberDao {

	private Context context;
	private BlackNumberDBHelper helper;
	private String blackNumberTableName = "blacknumber"; // we should use varaible becuase of only one table


	public BlackNumberDao(Context context){
		this.context = context;
		helper = new BlackNumberDBHelper(context);
	}
	
	
	public boolean add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		// configurat insert data
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		long result = db.insert("blacknumber", null, values);
		db.close();
		if (result > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select id from blacknumber where number=?", new String[]{number});
		if (cursor.moveToNext()) {
			result = true;
		} 
		cursor.close();
		db.close();
		return result;
	}
	
	public boolean update(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		long result = db.update("blacknumber", values, "number=?", new String[] {number});
		db.close();
		if (result > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		long result = db.delete("blacknumber", "number=?", new String[] {number});
		db.close();
		if (result > 0) {
			return true;
		} else {
			return false;
		}	
	}

	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = helper.getReadableDatabase();
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by id desc", null);
		while (cursor.moveToNext()) {
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			blackNumberInfos.add(new BlackNumberInfo(number, mode));
		}
		cursor.close();
		db.close();
		return blackNumberInfos;
	}
	
	public String findMode(String number) {
		String result = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacknumber where number=?", new String[]{number});
		if (cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	
	
}
