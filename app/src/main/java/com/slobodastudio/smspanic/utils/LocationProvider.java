package com.slobodastudio.smspanic.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Observable;

public class LocationProvider extends Observable implements LocationListener {

	private static LocationProvider instance = new LocationProvider();
	private boolean run = false;

	public static LocationProvider getInstance() {

		return instance;
	}

	private Location currentLocation;
	private LocationManager lm;
	private Context mContext;

	private LocationProvider() {

	}

	/** @return the currentLocation */
	public Location getCurrentLocation() {

		Location result = currentLocation;
		currentLocation = null;
		return result;
	}

	public Location getLastLocation() {

		return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}

	@Override
	public void onLocationChanged(final Location location) {

		setChanged();
		notifyObservers(location);
		Log.v(LocationProvider.class.getSimpleName(), location.getLatitude() + "");
		setCurrentLocation(location);
		clearChanged();
	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	public void stopUpdates() {

		if (run && countObservers() == 0) {
			try {
				lm.removeUpdates(this);
				// lm = null;
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
			run = false;
		}
	}

	/** @param currentLocation
	 *            the currentLocation to set */
	public void setCurrentLocation(Location currentLocation) {

		this.currentLocation = currentLocation;
	}

	/** Set global parameters for location provider's class
	 * 
	 * @param context
	 *            - context of application
	 * @param interval
	 *            - interval updating of location */
	public void setParams(Context context) {

		mContext = context;
	}

	public void startUpdates() {

		if (!run) {
			Log.v(LocationProvider.class.getSimpleName(), run + "");
			try {
				lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
				run = true;
			} catch (IllegalArgumentException e) {
				Log.e(LocationProvider.class.getSimpleName(), "Location crash!", e);
			}
		}
	}
}
