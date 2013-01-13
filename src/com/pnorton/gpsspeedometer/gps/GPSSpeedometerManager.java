package com.pnorton.gpsspeedometer.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


/* REVISION HISTORY
 * 
 * 1.0.4 Initial Version
 * 
 * 1.0.6 Basic GPS Puller with methods to retrieve speed in m/s, mph and kph
 * 
 * 1.0.16 Integration system added to compute total distance
 * 
 * 1.0.17 Fix for milliseconds being time format and mutator for Odometer and Trip
 * 
 * 1.0.23 Status indicator added as a new seperate class that displays toasts on
 * GPS Status
 * 
 * 1.0.24 Strength reduce all divisions to multiplications
 * 
 */
 
/**
 * Main Manager for GPS Capture
 * 
 * @author Peter B Norton
 * @version 1.0.24
 * 
 */
public class GPSSpeedometerManager {
	
	// --------------------------------------------------------------------------------------------

	// Current Speed Value
	private float m_speed = 0;
	
	// Location Manager Object
	private LocationManager m_location;
	
	// Conversion factor for m/s into mph
	private static final float MPH_CONVERT = 2.237f;
	
	// Conversion factor for m/s into kph
	private static final float KPH_CONVERT = 3.6f;
	
	// Previous Time used for distance integration
	private long m_previous_time = 0;
	
	// Total Distance recorded by integration
	private float m_total_distance = 0;
	
	// Odometer Reading
	private float m_odometer;
	
	// Previous Speed Reading
	private float m_previous_speed = 0;
	
	// GPS Status Listener
	private GPSStatusManager m_status;
	
	// --------------------------------------------------------------------------------------------

	/**
	 * Start the GPS Capture
	 * @param context Context from Activity
	 */
	public void start(Context context) {

		// Get the Location Manager
		m_location = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		// Start searching for location updates
		m_location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
					loc_listener);
		
		m_status = new GPSStatusManager(context);
		
		// Register a GPS Status Listener
		m_location.addGpsStatusListener(m_status);
		
	}
	
	// --------------------------------------------------------------------------------------------

	/**
	 * Stop the GPS Capture
	 */
	public void stop() {
		
		// Stop the Location Updates
		m_location.removeGpsStatusListener(m_status);
		m_location.removeUpdates(loc_listener);
		m_previous_time = 0;

	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Get the current speed in metres per second
	 * @return Speed in m/s
	 */
	public float getSpeed() {
		
		return m_speed;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Get the current speed in miles per hour
	 * @return Speed in mph
	 */
	public float getSpeedMPH() {
		
		return m_speed * MPH_CONVERT;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Get the current speed in kilometres per hour
	 * @return Speed in kph
	 */
	public float getSpeedKPH() {
		
		return m_speed * KPH_CONVERT;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	public float getTotalDistanceMiles() {
		
		return (m_total_distance / 1609.0f);
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	public float getOdometerMiles() {
		
		return m_odometer;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	public void setOdometerMiles(float odo) {
		
		m_odometer = odo;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	public void setTripMiles(float trip) {
		
		m_total_distance = trip * 1609.0f;
		
	}
	
	// --------------------------------------------------------------------------------------------

	// Listener Callback for Location
	private LocationListener loc_listener = new LocationListener() {

		public void onLocationChanged(Location location) {

			// Get the read values from the LocationManager
			m_speed = location.getSpeed();
			long time = location.getTime();

			// Check if previous time is recorded
			if(m_previous_time == 0) {
				
				// If not store previous time
				m_previous_time = time;
				
			} else {
				
				// Get the difference in time (in ms)
				long m_delta = time - m_previous_time;
				
				// Store previous time
				m_previous_time = time;
				
				// Determine Distance with approx integral with respect to time
				float distance = (((m_previous_speed + m_speed) * 0.5f) * (m_delta * 0.001f));
				
				// Add distance to total
				m_total_distance += distance;
				
				// Add the distance to the odometer
				m_odometer += (distance * 0.000621504f);
				
				// Store the previous speed value for the next integral
				m_previous_speed = m_speed;
				
			}
			
		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {

		}

		public void onStatusChanged(String provider, int n, Bundle bundle) {

		}

	};
	
	// --------------------------------------------------------------------------------------------
	
}
