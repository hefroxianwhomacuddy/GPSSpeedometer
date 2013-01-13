package com.pnorton.gpsspeedometer.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.pnorton.gpsspeedometer.R;
import com.pnorton.gpsspeedometer.filters.MovingAverageFilter;
import com.pnorton.gpsspeedometer.gps.GPSSpeedometerManager;
import com.pnorton.gpsspeedometer.views.DefaultSpeedoView;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/*
 * REVISION HISTORY
 * 
 * 1.0.4 Initial Version built from GPSReader
 * 
 * 1.0.6 GPSPedometerManager now set in the activity allows GPS pulls
 * 
 * 1.0.7 Implemented the low pass filter for smoothing speed changes (2 cascade)
 * 
 * 1.0.8 Layout changed to XML type to now allow debug filter adjustments
 * 
 * 1.0.12 Wake Lock now implemented to prevent screen sleep
 * 
 * 1.0.13 Shared Preferences Manager added
 * 
 * 1.0.16 Data system now added for odometer and trip counter (no reset yet though)
 * 
 * 1.0.17 Prefs added for odometer and trip these are now saved by the app
 * 
 * 1.0.18 Changed to Fragment Activity to support Fragments
 * 
 * 1.0.20 Fix for Odometer/Trip preferences not being saved 
 * 
 * 1.0.21 Fix to make sure Cut-off/Cascade values are sent to 
 * the filter
 * 
 * 1.0.22 Moving Average filter now replaces IIRCascadeLowPass
 *
 */

/**
 * Main Activity for the GPS Speedometer
 * 
 * @author Peter B Norton
 * @version 1.0.22
 * 
 */
public class GPSSpeedometerActivity extends FragmentActivity {

	// --------------------------------------------------------------------------------------------

	// Speedometer View
	private DefaultSpeedoView m_speedo_view;

	// GPS Hardware Manager
	private GPSSpeedometerManager m_manager;

	// Render Timer
	private Timer m_timer;

	// Render Timer Task
	private GPSTimerTask m_timer_task;
	
	// Moving Average Filter Object
	private MovingAverageFilter m_filter;

	// Wake lock
	private PowerManager.WakeLock m_wake_lock;

	// Shared Preferences for this application
	private GPSSpeedometerPreferences m_prefs;

	// --------------------------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gps_speedometer_layout);

		// Unpack Speedometer View
		m_speedo_view = ((DefaultSpeedoView) findViewById(R.id.defaultSpeedoView));

		// Set the Low Pass Cascade Filter to default value
		m_filter = new MovingAverageFilter(4);

		// Start a new GPS Capture 
		m_manager = new GPSSpeedometerManager();

		// Load the Saved Preferences
		m_prefs = new GPSSpeedometerPreferences(this);
		m_prefs.loadPreferences();
		
		m_filter.reset(m_prefs.getWeights());

		// Set the Odometer Miles Value
		m_manager.setOdometerMiles(m_prefs.getOdometer());

		// Set the Trip Miles Value
		m_manager.setTripMiles(m_prefs.getTrip());

		// Set the SeekBars and Event Handlers
		setupControls();

		// Get the Wake Lock from Power Manager
		m_wake_lock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.FULL_WAKE_LOCK,
						"GPSSpeedometerActivity");

		// Start GPS Capture
		m_manager.start(this);

		// Setup the Render Timer
		m_timer = new Timer();

		// TimerTask for Render
		m_timer_task = new GPSTimerTask();

		// Set the Update rate for the speedo view (currently 40 ms = 25 fps)
		m_timer.schedule(m_timer_task, 40, 40);

	}

	// --------------------------------------------------------------------------------------------

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.menu_main, menu);

		return true;
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.itemOdo:
			OdometerDialog fragment = OdometerDialog.getInstanceOf(this);
			fragment.show(getSupportFragmentManager(), "odo");
			return true;
		case R.id.itemTripReset:
			m_manager.setTripMiles(0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	// --------------------------------------------------------------------------------------------
	// Callback for the OdometerDialog
	
	/**
	 * Set the Odometer Value 
	 * @param Odometer value as an integer
	 */
	public void setOdometer(int odo) {
		
		m_manager.setOdometerMiles(odo);
		
	}

	// --------------------------------------------------------------------------------------------

	@Override
	protected void onPause() {

		super.onPause();
		
		// Save the current odometer/trip readings to preferences
		m_prefs.setOdometer(m_manager.getOdometerMiles());
		m_prefs.setTrip(m_manager.getTotalDistanceMiles());
		
		// Save the Preferences
		m_prefs.savePreferences();
		
		// Release the Wake Lock so the device can now sleep
		m_wake_lock.release();
	}

	// --------------------------------------------------------------------------------------------

	@Override
	protected void onResume() {

		super.onResume();
		
		// Acquire the Wake Lock prevents the device sleeping 
		// whilst this application is in focus
		m_wake_lock.acquire();
	}

	// --------------------------------------------------------------------------------------------

	@Override
	protected void onDestroy() {

		super.onDestroy();

		m_manager.stop();

		m_timer.cancel();

		m_timer_task.cancel();

	}

	// --------------------------------------------------------------------------------------------

	private void setupControls() {

		((SeekBar) findViewById(R.id.seekWeights)).setMax(30);
		((SeekBar) findViewById(R.id.seekWeights)).setProgress(m_prefs
				.getWeights() - 2);
		((TextView) findViewById(R.id.textViewWeights)).setText("Weights = "
				+ m_prefs.getWeights());
		((SeekBar) findViewById(R.id.seekWeights))
				.setOnSeekBarChangeListener(weights_listener);

	}


	// --------------------------------------------------------------------------------------------

	private OnSeekBarChangeListener weights_listener = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

			m_prefs.setWeights(progress + 2);

			((TextView) findViewById(R.id.textViewWeights))
					.setText("Cascades = " + m_prefs.getWeights());

			m_filter.reset(m_prefs.getWeights());

		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// Not Used

		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			// Not Used

		}
	};

	// --------------------------------------------------------------------------------------------

	private void updateSpeed() {

		final float mph_speed = m_filter.processSample(m_manager.getSpeedMPH());

		Runnable runner = new Runnable() {

			public void run() {

				// Update Controls
				m_speedo_view.setSpeed(mph_speed);
				m_speedo_view.setTrip(m_manager.getTotalDistanceMiles());
				m_speedo_view.setOdometer((int) m_manager.getOdometerMiles());

			}

		};

		runOnUiThread(runner);

	}

	// --------------------------------------------------------------------------------------------

	// Animation Timer Task to force a view repaint
	private class GPSTimerTask extends TimerTask {

		@Override
		public void run() {

			if (m_manager != null) {

				updateSpeed();

			}

		}

	}

	// --------------------------------------------------------------------------------------------

}
