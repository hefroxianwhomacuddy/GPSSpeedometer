package com.pnorton.gpsspeedometer.alert;

public interface SpeedAlerter 
{

	public abstract void updateSpeed(int speed);
	
	public abstract void setSpeedLimit(int speed);
	
	public abstract int getSpeedLimit();
	
}
