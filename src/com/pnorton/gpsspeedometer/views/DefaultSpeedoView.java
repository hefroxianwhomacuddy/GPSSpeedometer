package com.pnorton.gpsspeedometer.views;

import com.pnorton.gpsspeedometer.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


/**
 * Default Speedo View for the Application
 * 
 * @author Peter B Norton
 * @version 1.0.21
 * 
 */
public class DefaultSpeedoView extends View {

	// --------------------------------------------------------------------------------------------

	// GEOMETRY DEFINING OBJECTS
	private RectF m_dial;
	private RectF m_dial_centre;
	private Path m_needle_path;
	private Path[] m_dial_ticks_big;
	private Path[] m_dial_ticks_kph;
	private PointF[] m_pts_text;
	private PointF[] m_pts_text_kph;
	private PointF m_mph_label;
	private PointF m_kmh_label;
	private PointF m_miles_label;
	private PointF m_centre;
	private RectF m_display;
	private float m_rounding;

	// --------------------------------------------------------------------------------------------

	// PAINT OBJECTS
	private Paint m_paint_dial;
	private Paint m_paint_dial_centre;
	private Paint m_paint_needle;
	private Paint m_paint_ticks_big;
	private Paint m_paint_ticks_small;
	private Paint m_paint_ticks_ex_small;
	private Paint m_paint_text;
	private Paint m_paint_text_kph;
	private Paint m_paint_text_label;
	private Paint m_paint_text_display;

	// --------------------------------------------------------------------------------------------

	// PREVIOUS SPEED RECORDED
	private float m_last_speed;

	// --------------------------------------------------------------------------------------------

	// Trip Counter Digits
	private SevenSegmentDigit[] m_trip;

	// Odometer Counter Digits
	private SevenSegmentDigit[] m_odometer;

	// --------------------------------------------------------------------------------------------

	public DefaultSpeedoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	// --------------------------------------------------------------------------------------------

	public DefaultSpeedoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	// --------------------------------------------------------------------------------------------

	public DefaultSpeedoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	// --------------------------------------------------------------------------------------------

	/*
	 * Initialise the Paint objects for the 7 segment display
	 */
	private void init() {

		// Paint Object for Dial Background
		m_paint_dial = new Paint();
		m_paint_dial.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_dial.setStyle(Style.FILL);
		m_paint_dial.setColor(getContext().getResources().getColor(
				R.color.color_dial));

		// Paint object for Dial Centre
		m_paint_dial_centre = new Paint();
		m_paint_dial_centre.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_dial_centre.setStyle(Style.FILL);
		m_paint_dial_centre.setColor(getContext().getResources().getColor(
				R.color.color_dial_centre));

		// Paint object for the Speedo Needle
		m_paint_needle = new Paint();
		m_paint_needle.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_needle.setStyle(Style.FILL_AND_STROKE);
		m_paint_needle.setStrokeWidth(8.0f);
		m_paint_needle.setColor(getContext().getResources().getColor(
				R.color.color_needle));

		// Paint object for the Large Ticks on the Speedo Rim
		m_paint_ticks_big = new Paint();
		m_paint_ticks_big.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_ticks_big.setStyle(Style.FILL_AND_STROKE);
		m_paint_ticks_big.setStrokeWidth(8.0f);
		m_paint_ticks_big.setColor(getContext().getResources().getColor(
				R.color.color_ticks));

		// Paint object for the Small Ticks on the Speedo Rim
		m_paint_ticks_small = new Paint();
		m_paint_ticks_small.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_ticks_small.setStyle(Style.FILL_AND_STROKE);
		m_paint_ticks_small.setStrokeWidth(3.0f);
		m_paint_ticks_small.setColor(getContext().getResources().getColor(
				R.color.color_ticks));

		// Paint object for the Extra Small Ticks on the Speedo Rim
		m_paint_ticks_ex_small = new Paint();
		m_paint_ticks_ex_small.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_ticks_ex_small.setStyle(Style.FILL_AND_STROKE);
		m_paint_ticks_ex_small.setStrokeWidth(1.0f);
		m_paint_ticks_ex_small.setColor(getContext().getResources().getColor(
				R.color.color_ticks));

		// Paint object for the Text on the Main Speed Dial
		m_paint_text = new Paint();
		m_paint_text.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_text.setStyle(Style.FILL);
		m_paint_text.setTextSize(42.0f);
		m_paint_text.setColor(getContext().getResources().getColor(
				R.color.color_ticks));

		// Paint object for the Text on the Main Speed Dial
		m_paint_text_kph = new Paint();
		m_paint_text_kph.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_text_kph.setStyle(Style.FILL);
		m_paint_text_kph.setTextSize(14.0f);
		m_paint_text_kph.setColor(getContext().getResources().getColor(
				R.color.color_ticks));

		m_paint_text_label = new Paint();
		m_paint_text_label.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_text_label.setStyle(Style.FILL);
		m_paint_text_label.setTextSize(20.0f);
		m_paint_text_label.setColor(getContext().getResources().getColor(
				R.color.color_ticks));

		m_paint_text_display = new Paint();
		m_paint_text_display.setFlags(Paint.ANTI_ALIAS_FLAG);
		m_paint_text_display.setStyle(Style.FILL);
		m_paint_text_display.setTextSize(20.0f);
		m_paint_text_display.setColor(Color.argb(0x99, 0x00, 0x00, 0x00));

	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Set the Speed value of the Speedometer
	 * @param speed Speed value
	 */
	public void setSpeed(float speed) {

		// Rotation Matrix
		Matrix matrix = new Matrix();

		if (m_last_speed == 0) {

			m_last_speed = speed;
			matrix.setRotate(speed * 1.6f, m_centre.x, m_centre.y);
			m_needle_path.transform(matrix);

		} else {

			matrix.setRotate(-(m_last_speed * 1.6f), m_centre.x, m_centre.y);
			m_needle_path.transform(matrix);
			m_last_speed = speed;
			matrix.setRotate(speed * 1.6f, m_centre.x, m_centre.y);
			m_needle_path.transform(matrix);

		}

		invalidate();

	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Set the Trip Counter Value
	 * @param value Trip Counter Value
	 */
	public void setTrip(float value) {

		int[] values = new int[5];

		values[0] = (int)(value / 1000) % 10;
		values[1] = (int)(value / 100) % 10;
		values[2] = (int)(value / 10) % 10;
		values[3] = (int)(value) % 10;
		values[4] = (int)((value - (int)value) * 10); 

		boolean zeroflag = true;
		
		m_trip[3].setDecimalPoint(true);

		for (int i = 0; i < 5; i++) {

			if (i == 4 || i == 3) {
				
				m_trip[i].setValue(values[i]);

			} else {

				if (zeroflag) {

					// No value found
					if(values[i] != 0) {
						
						m_trip[i].setValue(values[i]);
						zeroflag = false;
						
					} else {
						
						m_trip[i].setValue(0xF);
						
					}

				} else {

					// Value found
					m_trip[i].setValue(values[i]);

				}
			}

		}
		
	}
	
	// --------------------------------------------------------------------------------------------

	/**
	 * Set the Odometer Value
	 * @param Odometer Value
	 */
	public void setOdometer(int value) {

		int[] values = new int[6];

		values[0] = value / 100000;
		values[1] = (value / 10000) % 10;
		values[2] = (value / 1000) % 10;
		values[3] = (value / 100) % 10;
		values[4] = (value / 10) % 10;
		values[5] = value % 10;

		boolean zeroflag = true;

		for (int i = 0; i < 6; i++) {

			if (i == 5) {
				
				m_odometer[i].setValue(values[i]);

			} else {

				if (zeroflag) {

					// No value found
					if(values[i] != 0) {
						
						m_odometer[i].setValue(values[i]);
						zeroflag = false;
						
					} else {
						
						m_odometer[i].setValue(0xF);
						
					}

				} else {

					// Value found
					m_odometer[i].setValue(values[i]);

				}
			}

		}

	}

	// --------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawOval(m_dial, m_paint_dial);
		canvas.drawOval(m_dial_centre, m_paint_dial_centre);

		int mph = 0;

		for (PointF point : m_pts_text) {

			canvas.drawText("" + mph, point.x, point.y, m_paint_text);
			mph += 20;

		}

		int kph = 20;

		for (PointF point : m_pts_text_kph) {

			canvas.drawText("" + kph, point.x, point.y, m_paint_text_kph);
			kph += 20;

		}

		canvas.drawText("mph", m_mph_label.x, m_mph_label.y, m_paint_text_label);
		canvas.drawText("km/h", m_kmh_label.x, m_kmh_label.y,
				m_paint_text_label);

		for (int i = 0; i < 33; i++) {

			if ((i % 2) == 0) {
				canvas.drawPath(m_dial_ticks_big[i], m_paint_ticks_big);
			} else {
				canvas.drawPath(m_dial_ticks_big[i], m_paint_ticks_small);
			}

		}

		for (int i = 0; i < 26; i++) {

			if ((i % 2) == 0) {
				canvas.drawPath(m_dial_ticks_kph[i], m_paint_ticks_small);
			} else {
				canvas.drawPath(m_dial_ticks_kph[i], m_paint_ticks_ex_small);
			}

		}

		canvas.drawPath(m_needle_path, m_paint_needle);

		canvas.drawRoundRect(m_display, m_rounding, m_rounding,
				m_paint_ticks_ex_small);

		canvas.drawText("miles", m_miles_label.x, m_miles_label.y,
				m_paint_text_display);

		for (SevenSegmentDigit digit : m_trip) {

			digit.draw(canvas);

		}

		for (SevenSegmentDigit digit : m_odometer) {

			digit.draw(canvas);

		}

	}

	// --------------------------------------------------------------------------------------------

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		m_dial_ticks_big = new Path[33];

		m_dial_ticks_kph = new Path[26];

		Matrix matrix = new Matrix();

		m_centre = new PointF();

		if (w > h) {

			// Width greater than height (Landscape)
			int h_offset = (w - h) / 2;

			m_dial = new RectF(h_offset, 0, h_offset + h, h);

			m_dial_centre = new RectF(h_offset + (h * 0.42f), h * 0.42f,
					h_offset + (h * 0.58f), h * 0.58f);

			m_needle_path = new Path();

			m_needle_path.moveTo(h_offset + (h * 0.5f), (h * 0.05f));
			m_needle_path.lineTo(h_offset + (h * 0.5f), (h * 0.6f));

			m_centre.x = h_offset + (h * 0.5f);
			m_centre.y = h * 0.5f;

			Matrix needleMatrix = new Matrix();

			m_paint_text.setTextSize(h / 12.0f);
			m_paint_text_kph.setTextSize(h / 36.0f);
			m_paint_text_label.setTextSize(h / 24.0f);
			m_paint_text_display.setTextSize(h / 24.0f);

			needleMatrix.setRotate(-128.0f, h_offset + (h * 0.5f), h * 0.5f);

			m_needle_path.transform(needleMatrix);

			for (int i = 0; i < 33; i++) {

				m_dial_ticks_big[i] = new Path();

				m_dial_ticks_big[i].moveTo(h_offset + (h * 0.5f), 0);
				m_dial_ticks_big[i].lineTo(h_offset + (h * 0.5f), h * 0.07f);

				matrix.setRotate(8.0f * (i - 16), h_offset + (h * 0.5f),
						h * 0.5f);

				m_dial_ticks_big[i].transform(matrix);

			}

			for (int i = 0; i < 26; i++) {

				m_dial_ticks_kph[i] = new Path();

				m_dial_ticks_kph[i].moveTo(h_offset + (h * 0.5f), h * 0.22f);
				m_dial_ticks_kph[i].lineTo(h_offset + (h * 0.5f), h * 0.24f);

				m_dial_ticks_kph[i].transform(needleMatrix);

				matrix.setRotate(10.0f * i, h_offset + (h * 0.5f), h * 0.5f);

				m_dial_ticks_kph[i].transform(matrix);

			}

			m_pts_text = new PointF[9];

			m_pts_text[0] = new PointF(h_offset + (h * 0.17f), h * 0.77f);
			m_pts_text[1] = new PointF(h_offset + (h * 0.08f), h * 0.57f);
			m_pts_text[2] = new PointF(h_offset + (h * 0.12f), h * 0.37f);
			m_pts_text[3] = new PointF(h_offset + (h * 0.25f), h * 0.21f);
			m_pts_text[4] = new PointF(h_offset + (h * 0.45f), h * 0.14f);
			m_pts_text[5] = new PointF(h_offset + (h * 0.63f), h * 0.21f);
			m_pts_text[6] = new PointF(h_offset + (h * 0.74f), h * 0.38f);
			m_pts_text[7] = new PointF(h_offset + (h * 0.78f), h * 0.58f);
			m_pts_text[8] = new PointF(h_offset + (h * 0.71f), h * 0.76f);

			m_pts_text_kph = new PointF[12];

			m_pts_text_kph[0] = new PointF(h_offset + (h * 0.2f), h * 0.6f);
			m_pts_text_kph[1] = new PointF(h_offset + (h * 0.19f), h * 0.5f);
			m_pts_text_kph[2] = new PointF(h_offset + (h * 0.21f), h * 0.4f);
			m_pts_text_kph[3] = new PointF(h_offset + (h * 0.26f), h * 0.31f);
			m_pts_text_kph[4] = new PointF(h_offset + (h * 0.34f), h * 0.25f);
			m_pts_text_kph[5] = new PointF(h_offset + (h * 0.43f), h * 0.22f);
			m_pts_text_kph[6] = new PointF(h_offset + (h * 0.53f), h * 0.22f);
			m_pts_text_kph[7] = new PointF(h_offset + (h * 0.63f), h * 0.26f);
			m_pts_text_kph[8] = new PointF(h_offset + (h * 0.71f), h * 0.32f);
			m_pts_text_kph[9] = new PointF(h_offset + (h * 0.77f), h * 0.42f);
			m_pts_text_kph[10] = new PointF(h_offset + (h * 0.78f), h * 0.52f);
			m_pts_text_kph[11] = new PointF(h_offset + (h * 0.76f), h * 0.63f);

			m_mph_label = new PointF(h_offset + (h * 0.13f), h * 0.67f);
			m_kmh_label = new PointF(h_offset + (h * 0.7f), h * 0.69f);
			m_miles_label = new PointF(h_offset + (h * 0.32f), h * 0.87f);

			m_display = new RectF(h_offset + (0.3f * h), 0.75f * h, h_offset
					+ (0.7f * h), 0.9f * h);
			m_rounding = 0.02f * h;

			m_trip = new SevenSegmentDigit[5];

			m_trip[0] = new SevenSegmentDigit(new PointF(
					h_offset + (h * 0.45f), h * 0.8f), h * 0.05f, h * 0.12f);
			m_trip[1] = new SevenSegmentDigit(new PointF(h_offset + (h * 0.5f),
					h * 0.8f), h * 0.05f, h * 0.12f);
			m_trip[2] = new SevenSegmentDigit(new PointF(
					h_offset + (h * 0.55f), h * 0.8f), h * 0.05f, h * 0.12f);
			m_trip[3] = new SevenSegmentDigit(new PointF(h_offset + (h * 0.6f),
					h * 0.8f), h * 0.05f, h * 0.12f);
			m_trip[4] = new SevenSegmentDigit(new PointF(
					h_offset + (h * 0.65f), h * 0.8f), h * 0.05f, h * 0.12f);

			m_odometer = new SevenSegmentDigit[6];

			m_odometer[0] = new SevenSegmentDigit(new PointF(h_offset
					+ (h * 0.4f), h * 0.73f), h * 0.05f, h * 0.12f);
			m_odometer[1] = new SevenSegmentDigit(new PointF(h_offset
					+ (h * 0.45f), h * 0.73f), h * 0.05f, h * 0.12f);
			m_odometer[2] = new SevenSegmentDigit(new PointF(h_offset
					+ (h * 0.5f), h * 0.73f), h * 0.05f, h * 0.12f);
			m_odometer[3] = new SevenSegmentDigit(new PointF(h_offset
					+ (h * 0.55f), h * 0.73f), h * 0.05f, h * 0.12f);
			m_odometer[4] = new SevenSegmentDigit(new PointF(h_offset
					+ (h * 0.6f), h * 0.73f), h * 0.05f, h * 0.12f);
			m_odometer[5] = new SevenSegmentDigit(new PointF(h_offset
					+ (h * 0.65f), h * 0.73f), h * 0.05f, h * 0.12f);

		} else {

			// Height is greater than width (Portrait)
			int v_offset = (h - w) / 2;

			m_dial = new RectF(0, v_offset, w, v_offset + w);

			m_dial_centre = new RectF(w * 0.42f, v_offset + (w * 0.42f),
					w * 0.58f, v_offset + (w * 0.58f));

			m_needle_path = new Path();

			m_needle_path.moveTo((w * 0.5f), v_offset + (w * 0.05f));
			m_needle_path.lineTo((w * 0.5f), v_offset + (w * 0.6f));

			m_centre.x = w * 0.5f;
			m_centre.y = v_offset + (w * 0.5f);

			Matrix needleMatrix = new Matrix();

			m_paint_text.setTextSize(w / 12.0f);
			m_paint_text_kph.setTextSize(w / 36.0f);
			m_paint_text_label.setTextSize(w / 24.0f);
			m_paint_text_display.setTextSize(w / 24.0f);

			needleMatrix.setRotate(-128.0f, w * 0.5f, v_offset + (w * 0.5f));

			m_needle_path.transform(needleMatrix);

			for (int i = 0; i < 33; i++) {

				m_dial_ticks_big[i] = new Path();

				m_dial_ticks_big[i].moveTo(w * 0.5f, v_offset);
				m_dial_ticks_big[i].lineTo(w * 0.5f, v_offset + (w * 0.07f));

				matrix.setRotate(8.0f * (i - 16), w * 0.5f, v_offset
						+ (w * 0.5f));

				m_dial_ticks_big[i].transform(matrix);

			}

			for (int i = 0; i < 26; i++) {

				m_dial_ticks_kph[i] = new Path();

				m_dial_ticks_kph[i].moveTo(w * 0.5f, v_offset + (w * 0.22f));
				m_dial_ticks_kph[i].lineTo(w * 0.5f, v_offset + (w * 0.24f));

				m_dial_ticks_kph[i].transform(needleMatrix);

				matrix.setRotate(10.0f * i, w * 0.5f, v_offset + (w * 0.5f));

				m_dial_ticks_kph[i].transform(matrix);

			}

			m_pts_text = new PointF[9];

			m_pts_text[0] = new PointF(w * 0.17f, v_offset + (w * 0.77f));
			m_pts_text[1] = new PointF(w * 0.08f, v_offset + (w * 0.57f));
			m_pts_text[2] = new PointF(w * 0.12f, v_offset + (w * 0.37f));
			m_pts_text[3] = new PointF(w * 0.25f, v_offset + (w * 0.21f));
			m_pts_text[4] = new PointF(w * 0.45f, v_offset + (w * 0.14f));
			m_pts_text[5] = new PointF(w * 0.63f, v_offset + (w * 0.21f));
			m_pts_text[6] = new PointF(w * 0.75f, v_offset + (w * 0.38f));
			m_pts_text[7] = new PointF(w * 0.78f, v_offset + (w * 0.58f));
			m_pts_text[8] = new PointF(w * 0.7f, v_offset + (w * 0.77f));

			m_pts_text_kph = new PointF[12];

			m_pts_text_kph[0] = new PointF(w * 0.2f, v_offset + (w * 0.6f));
			m_pts_text_kph[1] = new PointF(w * 0.19f, v_offset + (w * 0.5f));
			m_pts_text_kph[2] = new PointF(w * 0.21f, v_offset + (w * 0.4f));
			m_pts_text_kph[3] = new PointF(w * 0.26f, v_offset + (w * 0.31f));
			m_pts_text_kph[4] = new PointF(w * 0.34f, v_offset + (w * 0.25f));
			m_pts_text_kph[5] = new PointF(w * 0.43f, v_offset + (w * 0.22f));
			m_pts_text_kph[6] = new PointF(w * 0.53f, v_offset + (w * 0.22f));
			m_pts_text_kph[7] = new PointF(w * 0.63f, v_offset + (w * 0.26f));
			m_pts_text_kph[8] = new PointF(w * 0.71f, v_offset + (w * 0.32f));
			m_pts_text_kph[9] = new PointF(w * 0.77f, v_offset + (w * 0.42f));
			m_pts_text_kph[10] = new PointF(w * 0.78f, v_offset + (w * 0.52f));
			m_pts_text_kph[11] = new PointF(w * 0.76f, v_offset + (w * 0.63f));

			m_mph_label = new PointF(w * 0.13f, v_offset + (w * 0.67f));
			m_kmh_label = new PointF(w * 0.7f, v_offset + (w * 0.69f));
			m_miles_label = new PointF(w * 0.32f, v_offset + (w * 0.87f));

			m_display = new RectF(0.3f * w, v_offset + (0.75f * w), 0.7f * w,
					v_offset + (0.9f * w));

			m_rounding = 0.02f * w;

			m_trip = new SevenSegmentDigit[5];

			m_trip[0] = new SevenSegmentDigit(new PointF(w * 0.45f, v_offset
					+ (w * 0.8f)), w * 0.05f, w * 0.12f);
			m_trip[1] = new SevenSegmentDigit(new PointF(w * 0.5f, v_offset
					+ (w * 0.8f)), w * 0.05f, w * 0.12f);
			m_trip[2] = new SevenSegmentDigit(new PointF(w * 0.55f, v_offset
					+ (w * 0.8f)), w * 0.05f, w * 0.12f);
			m_trip[3] = new SevenSegmentDigit(new PointF(w * 0.6f, v_offset
					+ (w * 0.8f)), w * 0.05f, w * 0.12f);
			m_trip[4] = new SevenSegmentDigit(new PointF(w * 0.65f, v_offset
					+ (w * 0.8f)), w * 0.05f, w * 0.12f);

			m_odometer = new SevenSegmentDigit[6];

			m_odometer[0] = new SevenSegmentDigit(new PointF(w * 0.4f, v_offset
					+ (w * 0.73f)), w * 0.05f, w * 0.12f);
			m_odometer[1] = new SevenSegmentDigit(new PointF(w * 0.45f,
					v_offset + (w * 0.73f)), w * 0.05f, w * 0.12f);
			m_odometer[2] = new SevenSegmentDigit(new PointF(w * 0.5f, v_offset
					+ (w * 0.73f)), w * 0.05f, w * 0.12f);
			m_odometer[3] = new SevenSegmentDigit(new PointF(w * 0.55f,
					v_offset + (w * 0.73f)), w * 0.05f, w * 0.12f);
			m_odometer[4] = new SevenSegmentDigit(new PointF(w * 0.6f, v_offset
					+ (w * 0.73f)), w * 0.05f, w * 0.12f);
			m_odometer[5] = new SevenSegmentDigit(new PointF(w * 0.65f,
					v_offset + (w * 0.73f)), w * 0.05f, w * 0.12f);

		}
		
		setOdometer(0);
		setTrip(0);

	}

	// --------------------------------------------------------------------------------------------

}
