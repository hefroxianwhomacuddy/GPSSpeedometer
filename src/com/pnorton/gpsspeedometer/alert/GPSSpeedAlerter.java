package com.pnorton.gpsspeedometer.alert;

import com.pnorton.gpsspeedometer.activity.TrafficLightListener;

public class GPSSpeedAlerter implements SpeedAlerter {
	
	// --------------------------------------------------------------------------------------------
	
	private int speed_limit;
	
	private TrafficLightListener listener = null;
	
	// --------------------------------------------------------------------------------------------
	
	public GPSSpeedAlerter(TrafficLightListener l,int speed)
	{
		speed_limit = speed;
		listener = l;
	}
	
	// --------------------------------------------------------------------------------------------

	public void updateSpeed(int speed) {
	
		if((float)speed > (speed_limit * 1.1f))
		{
			// Plus 10 percent section 
			if(listener != null)
			{
				listener.setRed();
			}
		}
		else if(speed > speed_limit)
		{
			// Speed Limit is being exceeded
			if(listener != null)
			{
				listener.setAmber();
			}
		}
		else
		{
			// Speed Limit is not being exceeded
			if(listener != null)
			{
				listener.setGreen();
			}
		}
		
	}
	
	// --------------------------------------------------------------------------------------------

	public void setSpeedLimit(int speed) {
		
		speed_limit = speed;

	}
	
	// --------------------------------------------------------------------------------------------

	public int getSpeedLimit() {
		
		return speed_limit;
		
	}
	
	// --------------------------------------------------------------------------------------------

}
