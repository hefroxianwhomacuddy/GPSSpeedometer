package com.pnorton.gpsspeedometer.filters;

/*
 * REVISION HISTORY
 * 
 * 1.0.22 Initial Version for GPSSpeedometer correction for an
 * inherent defect in all previous versions
 * 
 */

/**
 * Class to Represent a Moving Average Data Filter of variable
 * kernel length 
 * 
 * @author Peter B Norton
 * @version 1.0.22
 *
 */
public class MovingAverageFilter {

	// Array of Values in the kernel
	private float[] m_values;
	
	// Length of the Filter Kernel
	private int m_kernel_length;
	
	// Default Kernel Length 
	private static final int DEFAULT_LENGTH = 4;
	
	// Reciprocal of Length to allow multiplication instead of division
	private float m_length_reciprocal;
	
	/**
	 * Default constructor for DEFAULT_LENGTH filter
	 */
	public MovingAverageFilter() {
		
		m_values = new float[DEFAULT_LENGTH];
		m_kernel_length = DEFAULT_LENGTH;
		m_length_reciprocal = (1.0f / DEFAULT_LENGTH);
		
	}
	
	/**
	 * Construct a Moving Average Filter of specified length
	 * NOTE: Value must be greater than 1
	 * @param n Length of the filter in samples
	 */
	public MovingAverageFilter(int n) {
		
		// Ensure the kernel length is greater than 1
		if(n > 1) {
			
			m_values = new float[n];
			m_kernel_length = n;
			m_length_reciprocal = (1.0f / n);
			
			
		} else {
			
			// Set default values for invalid length parameter
			m_values = new float[DEFAULT_LENGTH];
			m_kernel_length = DEFAULT_LENGTH;
			m_length_reciprocal = (1.0f / DEFAULT_LENGTH);
			
		}
			
	}
	
	/**
	 * Get the current length of the filter kernel
	 * @return Length of the filter kernel
	 */
	public int getKernelLength() {
		
		return m_kernel_length;
		
	}
	
	/**
	 * Reset the filter to zero values keeping the 
	 * current kernel length
	 */
	public void reset() {
		
		m_values = new float[m_kernel_length];
		
	}
	
	/**
	 * Reset the filter to zero values and adjust the
	 * kernel length at the same time
	 * NOTE: Value must be greater than 1
	 * @param n Kernel length to set
	 */
	public void reset(int n) {
		
		if(n > 1) {
			
			m_values = new float[n];
			m_kernel_length = n;
			m_length_reciprocal = (1.0f / n);
			
		} else  {
			
			m_values = new float[DEFAULT_LENGTH];
			m_kernel_length = DEFAULT_LENGTH;
			m_length_reciprocal = (1.0f / DEFAULT_LENGTH);
			
		}
		
	}
	
	/**
	 * Process a sample through the filter
	 * @param sample Sample value to process
	 * @return Current moving average result
	 */
	public float processSample(float sample) { 
		
		// Shunt values already in the stack up by 1
		for(int i = (m_kernel_length - 2); i >= 0 ; i--) {
			
			m_values[i + 1] = m_values[i];
			
		}
		
		// Store the current sample in the first slot
		m_values[0] = sample;
		
		// Declare a value for the running sum
		float sum = 0;
		
		// Compute the moving average
		for(int i = 0; i < m_kernel_length; i++) {
			
			if(m_values[i] == Float.NaN) {
				
				// If any values equal NaN return the sample value
				return m_values[0];
				
			} else {
				
				// Add value to running sum
				sum += m_values[i];
				
			}
			
		}
		
		// If no NaN's are found then the running sum divided by kernel
		// length 
		return (sum * m_length_reciprocal);
		
	}
}
