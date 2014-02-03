package edu.pitt.mobilesecurity.db.dao;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.mobilesecurity.db.ApplockDBHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AppLockDao {
	private Context context;
	private ApplockDBHelper helper;
	public static Uri uri = Uri.parse("content://edu.pitt.mobilesecurity/applockdbchange");

	public AppLockDao(Context context) {
		this.context = context;
		helper = new ApplockDBHelper(context);
	}
	
	public boolean add(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		long result = db.insert("applock", null, values);
		db.close();
		if(result>0){
			
			// tell contentResolver update tempLocked List
			context.getContentResolver().notifyChange(uri, null);
			return true;
		}else{
			return false;
		}
	}
	

	public boolean find(String packname){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select id from applock where packname=?", new String[]{packname});
		if(cursor.moveToFirst()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	
	public boolean delete(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		int result = db.delete("applock", "packname=?", new String[]{packname});
		db.close();
		if(result>0){
			context.getContentResolver().notifyChange(uri, null);
			return true;
		}else{
			return false;
		}
	}

	
	public List<String> findAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		List<String> packnames = new ArrayList<String>();
		Cursor cursor = db.rawQuery("select packname from applock", null);
	
		while(cursor.moveToNext()){
			String packname = cursor.getString(0);
		
			packnames.add(packname);
		}
		db.close();
		return packnames;
	}
	
	
	
	
}
