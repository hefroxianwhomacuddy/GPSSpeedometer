package com.pnorton.gpsspeedometer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class SpeedRoundelView extends View{
	
	// ------------------------------------------------------------------------------------------
	
	private int speed_limit = 70;
	
	private Paint outerRed;
	private Paint innerWhite;
	private Paint blackText;
	
	private PointF dialCentre;
	private PointF textPoint;
	
	private float outerRadius;
	private float innerRadius;
	
	private Rect textBounds;
	
	// ------------------------------------------------------------------------------------------

	public SpeedRoundelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
		
	}

	// ------------------------------------------------------------------------------------------
	
	public SpeedRoundelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	
	// ------------------------------------------------------------------------------------------

	public SpeedRoundelView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	// ------------------------------------------------------------------------------------------

	private void init()
	{
		outerRed = new Paint();
		outerRed.setFlags(Paint.ANTI_ALIAS_FLAG);
		outerRed.setStyle(Style.FILL);
		outerRed.setColor(Color.RED);
		
		innerWhite = new Paint();
		innerWhite.setFlags(Paint.ANTI_ALIAS_FLAG);
		innerWhite.setStyle(Style.FILL);
		innerWhite.setColor(Color.WHITE);
		
		blackText = new Paint();
		blackText.setFlags(Paint.ANTI_ALIAS_FLAG);
		blackText.setStyle(Style.FILL);
		blackText.setTextSize(96.0f);
		blackText.setColor(Color.BLACK);
		
		textBounds = new Rect();
		
		blackText.getTextBounds(speed_limit + "", 0, 2, textBounds);
		
		textPoint = new PointF();
		
	}
	
	// ------------------------------------------------------------------------------------------
	
	public void setSpeedLimit(int speed)
	{
		speed_limit = speed;
		
		textBounds = new Rect();
		
		blackText.getTextBounds(speed_limit + "", 0, 2, textBounds);
	}
	
	// ------------------------------------------------------------------------------------------
	
	public int getSpeedLimit()
	{
		return speed_limit;
	}
	
	// ------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		
		canvas.drawCircle(dialCentre.x, dialCentre.y, outerRadius, outerRed);
		canvas.drawCircle(dialCentre.x, dialCentre.y, innerRadius, innerWhite);
		canvas.drawText(this.speed_limit + "", textPoint.x, textPoint.y, blackText);
		
	}
	
	// ------------------------------------------------------------------------------------------

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		dialCentre = new PointF(w /2.0f,h / 2.0f);
		
		if(w > h)
		{
			outerRadius = h / 2.0f;
			innerRadius = h / 2.5f;
		}
		else
		{
			outerRadius = w / 2.0f;
			innerRadius = w / 2.5f;
		}
		
		textPoint.x = dialCentre.x - ((textBounds.right - textBounds.left) / 1.8f);
		textPoint.y = dialCentre.y + ((textBounds.bottom - textBounds.top) / 2.2f);
		
	}
	
	// ------------------------------------------------------------------------------------------
	

}
