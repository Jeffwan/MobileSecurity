package edu.pitt.mobilesecurity.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class CopyAssetsFile {
	// get a file object from assets folder(physical storage)
	
	public static File copyAssetsFile(Context context, String fileName, String destPath) {
		
		try {
			// get stream from AssetsManger
			InputStream is = context.getAssets().open(fileName);
			File destFile = new File(destPath);
			FileOutputStream fos = new FileOutputStream(destFile);
			
			int length = 0;
			byte[] buffer = new byte[1024];
			while((length = is.read(buffer)) != -1) {
				fos.write(buffer, 0, length);
			}
			
			is.close();
			fos.close();
			return destFile;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
