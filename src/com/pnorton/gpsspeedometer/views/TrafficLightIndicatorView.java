package com.pnorton.gpsspeedometer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * View to show a Traffic Light type indicator in a custom view
 * 
 * @author P.B.Norton
 * @version 1.0.69
 *
 */
public class TrafficLightIndicatorView extends View {
	
	//------------------------------------------------------------------------------------------
	
	// Centre of the Light
	private PointF m_light_centre;
	
	// Radius of the Light
	private float m_light_radius;
	
	//------------------------------------------------------------------------------------------
	
	// Light codes for the indicator
	public final static int LIGHT_RED = 0;
	public final static int LIGHT_AMBER = 1;
	public final static int LIGHT_GREEN = 2;
	public final static int LIGHT_OFF = 3;
	
	//------------------------------------------------------------------------------------------
	
	// Current state of the indicator
	private int m_state = LIGHT_RED;
	
	//------------------------------------------------------------------------------------------
	
	// Array of Paints for the colours
	private Paint[] m_paints;
	
	// Paint should the state be invalid
	private Paint m_paint_invalid;
	
	//------------------------------------------------------------------------------------------

	public TrafficLightIndicatorView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	//------------------------------------------------------------------------------------------

	public TrafficLightIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	//------------------------------------------------------------------------------------------

	public TrafficLightIndicatorView(Context context) {
		super(context);
		init();
	}
	
	//------------------------------------------------------------------------------------------

	/**
	 * Initialise the View with default values and prepare
	 * drawing objects
	 */
	public void init() {
		
		// Instantiate Geometry Values
		m_light_centre = new PointF();
		m_light_radius = 0;
		
		// Instantiate all the Paint objects
		m_paints = new Paint[4];
		for(int i = 0; i < 4; i++) {
			
			m_paints[i] = new Paint();
			
		}
		m_paint_invalid = new Paint();
		
		// Set values to paint objects
		m_paints[LIGHT_RED].setColor(Color.RED);
		m_paints[LIGHT_RED].setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paints[LIGHT_RED].setStyle(Style.FILL);
		m_paints[LIGHT_AMBER].setColor(Color.YELLOW);
		m_paints[LIGHT_AMBER].setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paints[LIGHT_AMBER].setStyle(Style.FILL);
		m_paints[LIGHT_GREEN].setColor(Color.GREEN);
		m_paints[LIGHT_GREEN].setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paints[LIGHT_GREEN].setStyle(Style.FILL);
		m_paints[LIGHT_OFF].setColor(Color.BLACK);
		m_paints[LIGHT_OFF].setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paints[LIGHT_OFF].setStyle(Style.FILL);
		m_paint_invalid.setColor(Color.GRAY);
		m_paint_invalid.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_invalid.setStyle(Style.FILL);
		
	}
	
	//------------------------------------------------------------------------------------------
	
	/**
	 * Change the state of this indicator
	 * @param state State to change to
	 */
	public void setState(int state) {
		
		m_state = state;
		invalidate();
		
	}
	
	//------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		
		if(m_state >= 0 && m_state < 4) {
			
			// Draw Traffic Light Colour
			canvas.drawCircle(m_light_centre.x, m_light_centre.y, m_light_radius, m_paints[m_state]);
			
		} else {
			
			// Draw the Invalid Colour since state is not valid
			canvas.drawCircle(m_light_centre.x, m_light_centre.y, m_light_radius, m_paint_invalid);
			
		}
		
	}
	
	//------------------------------------------------------------------------------------------

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		// Determine centre point 
		m_light_centre.x = w * 0.5f;
		m_light_centre.y = h * 0.5f;
		
		if (w >= h) {
			
			// Width is greater than height
			
			// Determine light radius
			m_light_radius = h * 0.4f;
			
		} else {
			
			// Height is greater than width
			
			// Determine light radius
			m_light_radius = w * 0.4f;
			
		}
		
		// Force a control redraw
		invalidate();
		
	}
	
	//------------------------------------------------------------------------------------------
	
}
