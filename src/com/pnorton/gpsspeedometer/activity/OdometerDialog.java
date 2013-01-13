package com.pnorton.gpsspeedometer.activity;

import com.pnorton.gpsspeedometer.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/*
 * REVISION HISTORY
 * 
 * 1.0.18 Initial Variant
 * 
 * 1.0.19 Fix for a NumberFormatException that occurs if the box entry is
 * invalid
 * 
 */

/**
 * Class to set the Odometer reading in the speedometer view
 * @author Peter B Norton
 * @version 1.0.19
 *
 */
public class OdometerDialog extends DialogFragment {
	
	// --------------------------------------------------------------------------------------------
	
	private int m_odometer;
	private EditText m_edit;
	private GPSSpeedometerActivity m_host;
	
	// --------------------------------------------------------------------------------------------
	
	private OdometerDialog(GPSSpeedometerActivity host) {
		
		m_host = host;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	public static OdometerDialog getInstanceOf(GPSSpeedometerActivity host) {
		
		return new OdometerDialog(host);
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	public int getOdometerSetting() {
		
		return m_odometer;
		
	}
	
	// --------------------------------------------------------------------------------------------

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.odometer_dialog_layout, container,false);
		
		((Button)v.findViewById(R.id.buttonOdoOK)).setOnClickListener(okListener);
		
		((Button)v.findViewById(R.id.buttonOdoCancel)).setOnClickListener(cancelListener);
		
		m_edit = ((EditText)v.findViewById(R.id.editText1));
		
		return v;
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	private OnClickListener okListener = new OnClickListener() {

		public void onClick(View v) {
			
			try {
				
				m_host.setOdometer(Integer.decode(m_edit.getText().toString()));
				
			}
			catch(NumberFormatException exception) {
				
				// Catch for if the user enters bollocks in the box
				
			}
			dismiss();
			
		}
		
	};
	
	// --------------------------------------------------------------------------------------------
	
	private OnClickListener cancelListener = new OnClickListener() {

		public void onClick(View v) {
			
			dismiss();
			
		}
		
	};

}
