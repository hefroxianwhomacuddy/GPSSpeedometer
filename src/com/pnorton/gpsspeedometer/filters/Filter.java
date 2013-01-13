package com.pnorton.gpsspeedometer.filters;

/**
 * 
 * @author Peter B Norton
 * @version 1.0.26
 *
 */
public interface Filter {
	
	public abstract void reset();
	
	public abstract void reset(int weights);
	
	public abstract float processSample(float sample);
	

}
