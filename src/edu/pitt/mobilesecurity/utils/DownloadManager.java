package edu.pitt.mobilesecurity.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.os.Environment;

public class DownloadManager {
	
	public static File download(String path, String savedPath, ProgressDialog progressDialog) throws Exception {
		
		// first, check the availability of SDCard 
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(3000);
			int code = conn.getResponseCode();
			
			// Configure progressDialog 
			progressDialog.setMax(conn.getContentLength());
			int total = 0;
			
			if(code == 200) {
				File file = new File(savedPath);
				
				FileOutputStream fos = new FileOutputStream(file);
				InputStream is = conn.getInputStream();
				
				byte[] buffer = new byte[1024];
				int length = 0;
				
				while ((length = is.read(buffer))!= -1) {
					Thread.sleep(10);
					fos.write(buffer, 0, length);
					total += length;
					progressDialog.setProgress(total);
				}
				
				is.close();
				fos.close();
				return file;
			} else {
				return null;
			}

		} else {
			
			throw new IllegalAccessException("sd is not available now!");
		}
		
	}
	
	
}
