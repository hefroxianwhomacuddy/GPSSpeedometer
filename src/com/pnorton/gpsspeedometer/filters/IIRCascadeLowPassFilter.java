package com.pnorton.gpsspeedometer.filters;

/*
 * Revision History
 * 
 * (SMART PEDOMETER)
 * 
 * 0.17 Initial Version of Cascaded Low Pass Single Pole IIR Filter
 * 
 * 0.20 Addition of setCentreFrequency method to adjust the filter dynamically
 * 
 * 0.21 Modification for new ISignalFilter interface which takes in timing
 * parameter
 * 
 * (VRMS)
 * 
 * 1.0.20 Port to Vessel Roll Monitoring System
 * 
 * (GPSSPEEDOMETER)
 * 
 * 1.0.7 Port to the GPSSpeedometer Project removed interface using no time 
 * information just simply filters the value due to consistent sample time
 * from activity timer task
 * 
 * 1.0.8 Removed the setCentreFrequency method and added overloaded reset
 * methods that allows the setting of new centre frequency and cascades number
 * 
 */

/**
 * Class IIRCascadeLowPassFilter
 * 
 * Responsibilities: Filter to perform a low pass operation on a signal using a
 * cascade of single pole low pass recursive digital filters. The implementation
 * allows the setting of a cut-off frequency along with the number of cascades
 * required. This also implements the ISignalFilter interface
 * 
 * Dependencies: Not dependent on any specific Java or Android features however
 * does depend upon the ISignalFilter implementation so must provide the methods
 * specified in that interface.
 * 
 * Android Dependencies: No Android Dependencies
 * 
 * @author P.B.Norton (Strath CIS)
 * @version 1.0.8
 * 
 */
public class IIRCascadeLowPassFilter {

	// ------------------------------------------------------------------------------------------

	// A Weight Value
	private float a_weight;
	
	// B Weight Value
	private float b_weight;
	
	// Previous values array
	private float[] previous_value;
	
	// Current centre frequency
	private float centre_frequency;
	
	// Current number of cascades
	private int number_of_cascades;

	// ------------------------------------------------------------------------------------------

	/**
	 * Default Constructor
	 * 
	 * @param f
	 *            Centre Fractional Frequency
	 * @param n
	 *            Number of Cascades
	 */
	public IIRCascadeLowPassFilter(float f, int n) {
		centre_frequency = f;
		number_of_cascades = n;
		reset();
		compute_weights();
	}

	// ------------------------------------------------------------------------------------------

	/**
	 * Compute the 2 filter weights
	 */
	public void compute_weights() {
		float x = (float) Math.exp(-2 * Math.PI * centre_frequency);
		a_weight = 1 - x;
		b_weight = x;
	}

	// ------------------------------------------------------------------------------------------

	public void reset() {
		// TODO Auto-generated method stub
		previous_value = new float[number_of_cascades];
		for (int i = 0; i < number_of_cascades; i++) {
			previous_value[i] = 0.0f;
		}
	}
	
	// ------------------------------------------------------------------------------------------
	
	public void reset(float f) {
		
		centre_frequency = f;
		reset();
		
	}
	
	// ------------------------------------------------------------------------------------------
	
	public void reset(int n) {
		
		number_of_cascades = n;
		reset();
		
	}
	
	// ------------------------------------------------------------------------------------------
	
	public void reset(float f,int n) {
		
		centre_frequency = f;
		number_of_cascades = n;
		reset();
		
	}

	// ------------------------------------------------------------------------------------------

	public float processSample(float n) {
		float y = n;
		for (int i = 0; i < number_of_cascades; i++) {
			y = (a_weight * y) + (previous_value[i] * b_weight);
			previous_value[i] = y;
		}
		return y;
	}

	// ------------------------------------------------------------------------------------------

}
