package br.ufc.ubiparkingclient_using_frontes.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;
import br.ufc.ubiparkingclient_using_frontes.UbiParkingClientActivity;

public class LocationCheckService extends Service implements LocationListener {

	private final long minTime = 2000; 
	private final float minDistance = 0;
	
	private final double minLatitude = -3.7462;
	private final double maxLatitude = -3.7467;
	private final double minLongitude = -38.5778;
	private final double maxLongitude = -38.5799;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    if (!gpsEnabled) {
	    	Toast.makeText(getApplicationContext(), "GPS Must be enable", Toast.LENGTH_SHORT).show();
	    	enableLocationSettings();
	    }
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
		
	}
	
	
	private void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(settingsIntent);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		double latitude  = location.getLatitude();
		double longitude = location.getLongitude();
		if(evaluatePosition(latitude, longitude)){
			Intent intent = new Intent(getApplicationContext(), UbiParkingClientActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	/**
	 * @param latitude
	 * @param longitude
	 * @return Retorna "true" se a localização atual estiver no intervalo de latitude/longitude definidos, "falso" caso contrário.
	 */
	private boolean evaluatePosition(double latitude, double longitude) {
		if(latitude>=minLatitude && latitude<=maxLatitude){
			if(longitude>= minLongitude && longitude<= maxLongitude){
				return true;
			}
		}
		return false;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

}
