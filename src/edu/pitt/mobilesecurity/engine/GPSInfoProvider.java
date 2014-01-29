package edu.pitt.mobilesecurity.engine;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSInfoProvider {
	private static GPSInfoProvider mGpsInfoProvider;
	private static LocationManager mLocationManager;
	private static MyListener listener;
	private static SharedPreferences mSharedPreferences;
	
	
	public GPSInfoProvider(){
		
	}
	
	public synchronized static GPSInfoProvider getInstance(Context context) {
		// single instance configuration -- initial
		if (mGpsInfoProvider == null) {
			mGpsInfoProvider = new GPSInfoProvider();
			mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			
			// Check available location providers
			List<String> names = mLocationManager.getAllProviders();
			for(String name : names) {
				System.out.print(name);
			}
			
			// Provider configuration
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_HIGH);
			
			String provider = mLocationManager.getBestProvider(criteria, true);
			System.out.println(provider);
			listener = mGpsInfoProvider.new MyListener();
			mLocationManager.requestLocationUpdates(provider, 0, 0, listener);
		}
		return mGpsInfoProvider;
	}
	
	public void unRegisterListener() {
		if (listener != null) {
			mLocationManager.removeUpdates(listener);
		}
	}
	
	public String getLastLocation() {
		return mSharedPreferences.getString("lastlocation", "");
	}
	
	private class MyListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			double accuracy = location.getAccuracy();
			String result = "http://maps.google.com/?q="+ latitude +","+ longitude;
			
			Editor editor = mSharedPreferences.edit();
			editor.putString("lastlocation", result);
			editor.commit();
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub	
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
		
	}
	
}
