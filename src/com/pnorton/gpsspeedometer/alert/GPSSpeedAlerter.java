package com.pnorton.gpsspeedometer.alert;

public class GPSSpeedAlerter implements SpeedAlerter {
	
	private int speed_limit;
	
	private boolean sound_playing = false;
	
	public GPSSpeedAlerter(int speed)
	{
		speed_limit = speed;
	}

	public void updateSpeed(int speed) {
	
		if((float)speed > (speed_limit * 1.1f))
		{
			// Plus 10 percent section 
		}
		else if(speed > speed_limit)
		{
			// Speed Limit is being exceeded
		}
		else
		{
			// Speed Limit is not being exceeded
		}
		
	}

	public void setSpeedLimit(int speed) {
		
		speed_limit = speed;

	}

	public int getSpeedLimit() {
		
		return speed_limit;
		
	}

}
