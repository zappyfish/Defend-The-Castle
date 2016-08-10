package com.example.defendthecastle;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import android.view.MotionEvent;
import java.util.Set;
import java.util.UUID;
import java.io.OutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;





public class TestView extends View {
	
	
	
	BluetoothSocket btSocket = null;
	GameActivity gameActivity;
	OutputStream[] outStream = btSetup();
	private char lastChar = 'a';
	public int myColor = getResources().getColor(R.color.black);
	public double myAngle = 0;
	public boolean draw = false;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	public OutputStream[] btSetup() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		OutputStream myoutStream;
		OutputStream[] returnStream = new OutputStream[pairedDevices.size()];
		if(pairedDevices.size() > 0) {
			int counter = 0;
		
			for(BluetoothDevice device : pairedDevices) {
				try {
					btSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
					btSocket.connect();
					myoutStream = btSocket.getOutputStream();
					returnStream[counter] = myoutStream;
					
					counter++;
					
					}
				catch (IOException e) {
					
				}
				
			}
		}
		return returnStream;
	}
	
	public TestView(Context context) {
		super(context);
		this.gameActivity = (GameActivity) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		initializeCanvas(canvas); // initialize the canvas
		if(draw) { // if we are supposed to draw the circle
			myColor = getResources().getColor(R.color.black);
			drawCircles(myAngle, canvas, myColor); // draw the circle in the appropriate position
		}

	}

	/**
	 * draw the circle at given position w/ correct radius
	 * @param x xcoord of circle center
	 * @param y ycoord of circle center
	 */
	public void drawCircles(double myAngle, Canvas canvas, int color) {
		int radius;
		int x, y;
		double circleDiff = getWidth()/3-getWidth()/4; // get difference in radii
		double length = getWidth()/3 - circleDiff/2; // length of position
		x = (int)(length*Math.cos(myAngle)); // get relative x
		y = -(int)(length*Math.sin(myAngle)); // get relative y. we negate because
		// the y coordinates are flipped!
		
		x+= getWidth()/2; // translate to relative position
		y+= getHeight()/2;
		radius = (int)(circleDiff/2);
		Path circle = new Path(); // declare new path
		Paint circlePaint = new Paint(); // new paint
		circlePaint.setColor(color); // set color to parameter
		circle.addCircle(x, y, radius, Direction.CW);
		canvas.drawPath(circle,circlePaint); // draw the circle
	}
	/**
	 * prepare the canvas with the white annulus and blue background
	 * @param canvas
	 */
	public void initializeCanvas(Canvas canvas) {
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.white));
		canvas.drawRect(0,0, getWidth(),getHeight(),background);
		Path circle1 = new Path();
		Path circle2 = new Path();
		Paint circle_paint1 = new Paint();
		circle_paint1.setColor(getResources().getColor(R.color.black));
		Paint circle_paint2 = new Paint();
		circle_paint2.setColor(getResources().getColor(R.color.white));
		circle1.addCircle(getWidth()/2, getHeight()/2, Math.min(getHeight(), getWidth())/3, Direction.CW);
		circle2.addCircle(getWidth()/2, getHeight()/2, Math.min(getHeight(), getWidth())/4, Direction.CW);
		canvas.drawPath(circle1, circle_paint1);
		canvas.drawPath(circle2, circle_paint2);
		
	}
	public void setColor(boolean draw) {
		int color;
		if(draw) {
			color = getResources().getColor(R.color.black);
		}
		else {
			color = getResources().getColor(R.color.white);
		}
	}

	
	/**
	 * get the angle relative to the circle based on x and y pos
	 * @param x
	 * @param y
	 * @return
	 */
	public double getAngle(double x, double y) {
		double relativeX = x-getWidth()/2;
		double relativeY = getHeight()/2 - y;
		if(x == 0) { // deal with singularities
			if(y > 0) {
				return Math.PI/2;
			}
			else {
				return 3*Math.PI/2;
			}
		}
		else {
			if(relativeX > 0) {
				return Math.atan(relativeY/relativeX);
			}
			else { // make sure we can access left side of circle too!
				return Math.PI + Math.atan(relativeY/relativeX);
			}
		}
	}
	public char getSector(int val) {
		int sector = (int)(val/25.5);
		char sectorChar = 'a';
		sectorChar += sector;
		return sectorChar;
	}
	/**
	 * this method sends the data from the position of the circle
	 * to the arduino over bluetooth
	 * it finds paired devices, creates a socket, connects, then
	 * tries to send data
	 * @param sendChar char of data to be sent to arduino
	 * @return the corresponding char of the sector to be sent
	 * note: the annulus is divided into 20 sectors
	 */
	
	public void sendData(char sendChar) {
		/*BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		if(pairedDevices.size() > 0) {
			for(BluetoothDevice device : pairedDevices) {
				try {
					btSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
					btSocket.connect();
					outStream = btSocket.getOutputStream();
					
					}
				catch (IOException e) {
					
				}
		*/
			byte[] b1 = {(byte)sendChar};
			for(OutputStream stream: outStream) {
				
				
				try {
					
					stream.write(b1);
				}
				catch (IOException e) {
					
				}
			}
			
			//}
		//}
	}
		
		
	
	
	
	/**
	 * capture touch events and draw or erase circles accordingly
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		double x = event.getX();
		double y = event.getY(); // get x and y coords
		myAngle = getAngle(x,y); // get angle
		int xysquared = (int)(Math.pow((x-getWidth()/2),2) + Math.pow(y-getHeight()/2,2));
		int xyroot = (int)(Math.pow(xysquared, 0.5)); // calculate the euclidean distance from the coordinate to the circle center
		int innerCircleRadius = Math.min(getHeight(),getWidth())/4;
		int outerCircleRadius = Math.min(getHeight(),getWidth())/3;
		int val = (int)(myAngle*255/(2*Math.PI));
		char currentChar = getSector(val);
		switch(event.getAction()) { // check action type
		
			case MotionEvent.ACTION_DOWN:
				if(xyroot >= innerCircleRadius && xyroot <= outerCircleRadius) { // if you're inside the annulus
					draw = true; // draw
					if(lastChar != currentChar) {
						sendData(currentChar);
						lastChar = currentChar;
					}
					invalidate(); // update the view
					
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if(xyroot >= innerCircleRadius && xyroot <= outerCircleRadius || draw) {
					draw = true;
					if(lastChar != currentChar) {
						sendData(currentChar);
						lastChar = currentChar;
					}
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				draw = false;
				sendData('a');
				invalidate();
				break;
				
				
			
		}
		return true;

	}
}
