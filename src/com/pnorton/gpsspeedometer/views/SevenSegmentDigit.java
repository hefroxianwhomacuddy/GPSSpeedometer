package com.pnorton.gpsspeedometer.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;

/*
 * REVISION HISTORY
 * 
 * <VRMS>
 * 
 * 1.0.1 Initial Version ported from VRMS Stub
 * 
 * <GPSSpeedometer>
 * 
 * 1.0.15 Port to GPSSpeedometer
 * 
 */

/**
 * Class to represent a single retro look 7 segment digit display with decimal point
 * 
 * @author P.B.Norton
 * @version 1.0.15
 * 
 */
public class SevenSegmentDigit {

	//------------------------------------------------------------------------------------------
	
	// Current Stored Value
	private byte m_value;

	// Paths for the Digits
	private Path[] m_digit_paths;

	// Paint objects for the Digits
	private Paint[] m_digit_paints;

	// Translation Table from input to encoded values
	private byte[] m_translation_table = { 0x3F, 0x06, 0x5B, 0x4F, 0x66, 0x6D,
			0x7D, 0x07, 0x7F, 0x6F, 0x39, 0x38, 0x73, 0x40, 0x79, 0x00 };
	
	// Radius of the Decimal Point
	private float m_dp_radius;
	
	// Center of the Decimal Point
	private PointF m_dp_centre;
	
	// Decimal Point flag
	private boolean m_dp_set;

	// X Position Values for the segments
	private static float[][] x_values = {
			{ 0.245f, 0.333f, 0.646f, 0.708f, 0.625f, 0.313f },
			{ 0.729f, 0.798f, 0.769f, 0.679f, 0.613f, 0.642f },
			{ 0.673f, 0.744f, 0.713f, 0.625f, 0.556f, 0.588f },
			{ 0.598f, 0.513f, 0.202f, 0.135f, 0.221f, 0.531f },
			{ 0.113f, 0.046f, 0.077f, 0.165f, 0.233f, 0.202f },
			{ 0.167f, 0.100f, 0.131f, 0.219f, 0.290f, 0.260f },
			{ 0.190f, 0.279f, 0.592f, 0.654f, 0.570f, 0.256f } };

	// Y Position Values for the segments
	private static float[][] y_values = {
			{ 0.148f, 0.087f, 0.087f, 0.148f, 0.213f, 0.213f },
			{ 0.167f, 0.227f, 0.413f, 0.478f, 0.413f, 0.227f },
			{ 0.517f, 0.577f, 0.765f, 0.828f, 0.765f, 0.577f },
			{ 0.845f, 0.907f, 0.907f, 0.845f, 0.780f, 0.780f },
			{ 0.828f, 0.768f, 0.577f, 0.517f, 0.577f, 0.765f },
			{ 0.478f, 0.417f, 0.227f, 0.167f, 0.227f, 0.417f },
			{ 0.500f, 0.433f, 0.433f, 0.500f, 0.558f, 0.558f } };
	
	//------------------------------------------------------------------------------------------

	/**
	 * Default Constructor for a Single 7 Segment Digit
	 * @param origin Top Left hand corner point of the digit
	 * @param width Width of the digit
	 * @param height Height of the digit
	 */
	public SevenSegmentDigit(PointF origin, float width, float height) {

		// Store the Width and Height values locally
		float w = width;
		float h = height;
		
		// Set Values to Zero no DP
		m_value = 0x00;
		m_dp_set = false;

		// Perform aspect correction and apply offset
		if (width > height) {

			origin.x += ((w - height) / 2);
			w = height;

		} else {

			origin.y += ((h - width) / 2);
			h = width;

		}

		// Establish Seven Segment Digit Paths

		m_digit_paths = new Path[7];

		for (int i = 0; i < 7; i++) {

			m_digit_paths[i] = new Path();

		}

		// Generate the Paths for the Seven Segment
		for (int i = 0; i < 7; i++) {

			m_digit_paths[i].moveTo(((x_values[i][5] * w) + origin.x),
					((y_values[i][5] * h) + origin.y));

			for (int j = 0; j < 6; j++) {

				m_digit_paths[i].lineTo(((x_values[i][j] * w) + origin.x),
						((y_values[i][j] * h) + origin.y));

			}

		}
		
		m_dp_radius = w * 0.052f;
		
		m_dp_centre = new PointF();
		
		m_dp_centre.x = ((0.792f * w) + origin.x);
		m_dp_centre.y = ((0.867f * h) + origin.y);

		// Create a new paint array
		m_digit_paints = new Paint[2];

		// Off Paint
		m_digit_paints[0] = new Paint();
		m_digit_paints[0].setARGB(0x10, 0x00, 0x00, 0x00);
		m_digit_paints[0].setFlags(Paint.ANTI_ALIAS_FLAG);
		m_digit_paints[0].setStyle(Style.FILL);

		// On Paint
		m_digit_paints[1] = new Paint();
		m_digit_paints[1].setARGB(0x99, 0x00, 0x00, 0x00);
		m_digit_paints[1].setFlags(Paint.ANTI_ALIAS_FLAG);
		m_digit_paints[1].setStyle(Style.FILL);

	}
	
	//------------------------------------------------------------------------------------------

	/**
	 * Set the Value of the Digit to display
	 * 0x0 - 0x9 Numbers 0 - 9
	 * 0xA - Letter C
	 * 0xB - Letter L
	 * 0xC - Letter P
	 * 0xD - Dash
	 * 0xE - Letter E
	 * 0xF - Blank
	 * @param value Value of the Digit to display as per above table
	 */
	public void setValue(int value) {

		// Check Value is in range
		if(value >= 0 && value < 16) {
			
			// If it is translate to 7 segment
			m_value = m_translation_table[value];
			
		} else {
			
			// Or Display E on LCD
			m_value = m_translation_table[0xE];
			
		}
		
	}
	
	//------------------------------------------------------------------------------------------
	
	/**
	 * Set the Decimal Point Flag
	 * @param dp Decimal Point Flag to set
	 */
	public void setDecimalPoint(boolean dp) {
		
		m_dp_set = dp;
		
	}
	
	//------------------------------------------------------------------------------------------

	/**
	 * Draw the 7 segment digit onto a canvas
	 * @param canvas Canvas on which to draw the digit
	 */
	public void draw(Canvas canvas) {

		// Iterate through the digits
		for (int i = 0; i < 7; i++) {

			// Check if this segment value is set or not
			if ((m_value & (0x01 << i))== (0x01 << i)) {

				// Segment ON
				canvas.drawPath(m_digit_paths[i], m_digit_paints[1]);

			} else {

				// Segment OFF
				canvas.drawPath(m_digit_paths[i], m_digit_paints[0]);

			}

		}
		
		// Check if the Decimal Point Flag is set
		if(m_dp_set) {
			
			// DP ON
			canvas.drawCircle(m_dp_centre.x, m_dp_centre.y, m_dp_radius, m_digit_paints[1]);
			
		} else {
			
			// DP OFF
			canvas.drawCircle(m_dp_centre.x, m_dp_centre.y, m_dp_radius, m_digit_paints[0]);
			
		}

	}
	
	//------------------------------------------------------------------------------------------

}
