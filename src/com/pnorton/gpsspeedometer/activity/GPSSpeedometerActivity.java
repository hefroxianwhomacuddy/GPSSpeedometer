package com.pnorton.gpsspeedometer.activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.pnorton.gpsspeedometer.R;
import com.pnorton.gpsspeedometer.filters.Filter;
import com.pnorton.gpsspeedometer.filters.MovingAverageFilter;
import com.pnorton.gpsspeedometer.gps.GPSSpeedometerManager;
import com.pnorton.gpsspeedometer.views.DefaultSpeedoView;
import com.pnorton.gpsspeedometer.views.SpeedRoundelView;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ViewFlipper;


/**
 * Main Activity for the GPS Speedometer
 * 
 * @author Peter B Norton
 * @version 1.0.26
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
	private Filter m_filter;

	// Wake lock
	private PowerManager.WakeLock m_wake_lock;

	// Shared Preferences for this application
	private GPSSpeedometerPreferences m_prefs;
	
	private Vector<SpeedRoundelView> m_roundels;
	
	private Point touch_pos;

	// --------------------------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gps_speedometer_layout);

		// Unpack Speedometer View
		m_speedo_view = ((DefaultSpeedoView) findViewById(R.id.defaultSpeedoView));

		// Set the Low Pass Cascade Filter to default value
		m_filter = getMovingAverageFilter(4);

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
		
		touch_pos = new Point();
		
		setupSpeedRoundels();
		
		
		for(SpeedRoundelView v : m_roundels)
		{
			((ViewFlipper)findViewById(R.id.viewFlipperRoundels)).addView(v);
		}

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
	
	private void setupSpeedRoundels()
	{
		// Setup the Speed Roundels 5 in total
		m_roundels = new Vector<SpeedRoundelView>();
		
		// Standard Speed Limits used on UK Roads in mph
		int speeds[] = {30,40,50,60,70};
		
		// Iterate through the speeds adding each view to the 
		// ViewFlipper
		for(int s : speeds)
		{
			SpeedRoundelView view = new SpeedRoundelView(this);
			view.setSpeedLimit(s);
			m_roundels.add(view);
		}
	}
	
	// --------------------------------------------------------------------------------------------
	
	private Filter getMovingAverageFilter(int weights)
	{
		return new MovingAverageFilter(weights);
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
		((ViewFlipper) findViewById(R.id.viewFlipperRoundels)).setOnTouchListener(flipper_listener);

	}
	
	// --------------------------------------------------------------------------------------------
	
	private OnTouchListener flipper_listener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			
			// Check the Touch Action
			switch(event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				// Down Stroke Event store position
				touch_pos.x = (int) event.getX();
				touch_pos.y = (int) event.getY();
				return true;
			case MotionEvent.ACTION_UP:
				// Up Stroke Event handle roundel flip
				if(touch_pos.x > (int)event.getX())
				{
					// Increase Speed Limit
					((ViewFlipper) findViewById(R.id.viewFlipperRoundels)).showPrevious();
				}
				else
				{
					// Decrease Speed Limit
					((ViewFlipper) findViewById(R.id.viewFlipperRoundels)).showNext();
				}
				return true;
			default:
				break;
			}
			
			return false;
		}
		
	};


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
