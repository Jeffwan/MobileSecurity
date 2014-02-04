package edu.pitt.mobilesecurity;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;

public class CleanSDActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_sd);

		// TODO: SDcard Clean
		File sdFile = Environment.getExternalStorageDirectory();

		File[] files = sdFile.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				String dirname = f.getName();
			}
		}

	}

	public void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				deleteFile(f);
			}
		} else {
			file.delete();
		}
	}

}
