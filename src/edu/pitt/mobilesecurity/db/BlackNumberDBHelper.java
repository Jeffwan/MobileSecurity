package edu.pitt.mobilesecurity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

// SQLiteOpenHelper makes it easy to operate database
public class BlackNumberDBHelper extends SQLiteOpenHelper {

	// the paramenter constructer give me is uselss because we will make it constant
	public BlackNumberDBHelper(Context context) {
		super(context, "blacknumber.db", null, 1);
	}

	
	/**
	 * number: phoneNumber
	 * mode: block mode    1.combination 2. phone 3. msg 
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table blacknumber (id integer primary key autoincrement, number varchar(20), mode varchar(2) )" ;
		db.execSQL(sql);

	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
