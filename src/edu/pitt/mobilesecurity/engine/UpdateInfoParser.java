package edu.pitt.mobilesecurity.engine;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import edu.pitt.mobilesecurity.domain.UpdateInfo;

public class UpdateInfoParser {
	
	public static UpdateInfo getUpdateInfo(InputStream inputStream) {
		
		// Get xml parser
		XmlPullParser parser = Xml.newPullParser();
		
		// Create a new updateInfo object to return
		UpdateInfo updateInfo = new UpdateInfo();
		
		try {
			parser.setInput(inputStream, "UTF-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch(type) {
					case XmlPullParser.START_TAG:
						if("version".equals(parser.getName())){
							updateInfo.setVersion(parser.nextText());
						} else if("description".equals(parser.getName())) {
							updateInfo.setDescription(parser.nextText());
						} else if ("path".equals(parser.getName())) {
							updateInfo.setPath(parser.nextText());
						}
						break;
				}
				
				type = parser.next();
			}
			inputStream.close();
			return updateInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 	
	}	
}
