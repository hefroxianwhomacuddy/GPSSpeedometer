package com.pnorton.gpsspeedometer.gps;

import android.content.Context;
import android.location.GpsStatus;
import android.widget.Toast;

/*
 * REVISION HISTORY
 * 
 * 1.0.23 Initial Variant
 * 
 */

/**
 * Class to handle changes in GPS display status
 * 
 * @author Peter B Norton
 * @version 1.0.23
 *
 */
public class GPSStatusManager implements GpsStatus.Listener {
	
	// --------------------------------------------------------------------------------------------
	
	private Context m_context;
	
	// --------------------------------------------------------------------------------------------
	
	public GPSStatusManager(Context context) {
		
		m_context = context;
		
	}
	
	// --------------------------------------------------------------------------------------------

	public void onGpsStatusChanged(int event) {
		
		switch(event) {
		case GpsStatus.GPS_EVENT_STARTED:
			Toast toast_start = Toast.makeText(m_context, 
					"Waiting for Location", Toast.LENGTH_LONG);
			toast_start.show();
			break;
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			Toast toast_found = Toast.makeText(m_context, 
					"Location Found", Toast.LENGTH_LONG);
			toast_found.show();
			break;
		default:
			break;
		}
		
	}
	
	// --------------------------------------------------------------------------------------------

}
