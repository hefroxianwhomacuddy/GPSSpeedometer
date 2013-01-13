package com.pnorton.gpsspeedometer.activity;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * REVISION HISTORY
 * 
 * 1.0.13 Initial version
 * 
 * 1.0.17 Added odometer and trip preferences
 * 
 * 1.0.22 Refactor for Moving Average Filter 
 * 
 */

/**
 * Class to store application preferences
 * 
 * @author Peter B Norton
 * @version 1.0.22
 *
 */
public class GPSSpeedometerPreferences {
	
	// --------------------------------------------------------------------------------------------
	
	// Application Context
	private Context m_context;
	
	// LOCAL VALUES
	
	private int m_weights;
	
	private float m_odo;
	
	private float m_trip;
	
	// --------------------------------------------------------------------------------------------
	
	// SHARED PREFERENCES IDENTIFIERS
	
	private static final int SHARED_PREF_VERSION_ID = 3;
	
	private static final String SHARED_PREF_VERSION = "version";
	private static final String SHARED_PREF_WEIGHTS = "weights";
	private static final String SHARED_PREF_ODOMETER = "odometer";
	private static final String SHARED_PREF_TRIP = "trip";
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Create a new Preferences Manager
	 * @param context Main Activity Context
	 */
	public GPSSpeedometerPreferences(Context context) {
	
		m_context = context;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Get the number of filter cascades
	 * @return Number of filter cascades
	 */
	public int getWeights() {
		
		return m_weights;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	public float getOdometer() {
		
		return m_odo;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	public void setOdometer(float odo) {
		
		m_odo = odo;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	public float getTrip() {
		
		return m_trip;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	public void setTrip(float trip) {
		
		m_trip = trip;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Set the number of cascades for the filter
	 * @param cascades Number of cascades for the filter
	 */
	public void setWeights(int weights) {
		
		m_weights = weights;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Load the shared preferences
	 */
	public void loadPreferences() {
		
		SharedPreferences prefs = m_context.getSharedPreferences("GPSSpeedometer", Context.MODE_PRIVATE);
		if(prefs.getInt(SHARED_PREF_VERSION, 0) == SHARED_PREF_VERSION_ID) {
			
			m_weights = prefs.getInt(SHARED_PREF_WEIGHTS, 4);
			m_odo = prefs.getFloat(SHARED_PREF_ODOMETER, 0);
			m_trip = prefs.getFloat(SHARED_PREF_TRIP, 0);
			
		} else {
			
			m_weights = 4;
			m_odo = 0;
			m_trip = 0;
			
		}
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Save the shared preferences
	 */
	public void savePreferences() {
		
		SharedPreferences prefs = m_context.getSharedPreferences("GPSSpeedometer", Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putInt(SHARED_PREF_VERSION, SHARED_PREF_VERSION_ID);
		ed.putInt(SHARED_PREF_WEIGHTS, m_weights);
		ed.putFloat(SHARED_PREF_ODOMETER, m_odo);
		ed.putFloat(SHARED_PREF_TRIP, m_trip);
		ed.commit();
		
	}
	
	// --------------------------------------------------------------------------------------------

}
