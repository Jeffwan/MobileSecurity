package edu.pitt.mobilesecurity.test;

import edu.pitt.mobilesecurity.db.BlackNumberDBHelper;
import android.test.AndroidTestCase;

public class TestCreateBlackNumberDB extends AndroidTestCase {

	public void testCreateDB() throws Exception{
		// not real context, just provided by Test Framework to test
		BlackNumberDBHelper helper = new BlackNumberDBHelper(getContext());
		helper.getWritableDatabase();
	}
}
