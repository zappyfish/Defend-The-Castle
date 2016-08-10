package com.example.defendthecastle;
import com.example.defendthecastle.R;


//import android.R;
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
import android.graphics.drawable.*;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import java.lang.Runnable;
import java.io.*;
import android.widget.TextView;




import java.util.Set;
import java.util.UUID;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Random;

public class GameView extends View {
	
	private Random rand = new Random();
	
	private float width;
	private float height;
	
	float shieldRadius;
	
	private Projectile[] projectileArray = new Projectile[10];
			// no more than 10 projectiles at a given time
	
	// create textview for score
	

	
	public static GameActivity gameActivity;
	//public int backgroundblack = getResources().getColor(R.color.black);
	public int backgroundwhite = getResources().getColor(R.color.white);
	
	public int backgroundcolor = backgroundwhite;
	
	
	private Drawable castle;
	//private Drawable shield; // these are for images
	
	private float projectileRadius;
	private int projectileSpeed = 150;
	//Projectile shieldProtector = new Projectile(width/2, height/2, 0.0, 0.0, (int)shieldRadius);
	
	//double[] shieldCoordinates = new double[2];
	Projectile myShield = new Projectile(0,0,0,0,shieldRadius);
	
	Projectile target;
	
	private boolean drawShield; // whether or not to draw the shield based on whether the finger is
	// touching the screen
	
	int left, right, top, bottom;
	// left, right, top, and bottom are for drawing castle in centre
	int numOfProjectiles = 1; // increase this (up to 10) every 10 seconds
	long tStart = System.currentTimeMillis(); // start teh clock
	
	long tElapsed;
	int numEntries;
	
	int score;
	
	private boolean started = false;
	private Handler handler = new Handler();
	
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			updateProjectilePositions();
			checkAllForContact();
			for(int i = 0; i<10; i++) {
				if(projectileArray[i]!= null && targetHit(projectileArray[i])) {
					projectileArray[i] = null;
				}
			}
			start();
		}
	};
	/**
	 * this method checks if any of hte projectiles are in contact with the shield
	 * it also makes sure that the shield is drawn, so that projectiles aren't destroyed
	 * while the shield isn't on the screen b/c it still has coordinates
	 */
	public void checkAllForContact() {
		for(int i = 0; i<10; i++) {
			if (projectileArray[i]!=null) {
				if( drawShield && myShield.contact(projectileArray[i])) {
					projectileArray[i] = null;
					score++;
				}
			}
		}
	}
	
	public boolean targetHit(Projectile projectile) {
		return target.contact(projectile);
	}
	
	/** 
	 * run movement thread
	 */
	public void start()  {
		started = true;
		handler.postDelayed(runnable, 100);
	}
	
	
	public void updateProjectilePositions() {
		for(int i = 0; i<projectileArray.length;i++) {
			if(projectileArray[i] != null) {
				projectileArray[i].translate(); // updated position of all projectiles
				
			}
		}
			tElapsed = System.currentTimeMillis() - tStart; // find time elapsed since start of game
			if (tElapsed >= 10*4500) { // if 45 seconds have passed, then all projectiles have been 
				// added to game, so set numEntries to 10
				numEntries = 10;
			}
			else {
				numEntries = (int)tElapsed/5000 + 1; // every 5 seconds, add a new projectile (up to 10)
			}
			projectileArray = updateProjectileArray(projectileArray, numEntries); // add new projectiles if necessary
			invalidate(); // draw everything
			//mHandler.postDelayed(mHandlerTask, 50); // update array and translate projectiles every 50 ms
			//run();
			
	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		width = getWidth();
		height = getHeight();
		target = new Projectile(width/2, height/2, 0, 0, 50); // create target in centre of screen
		shieldRadius = (float)Math.pow(height*width/200,0.5);
		projectileRadius = (float)Math.pow(height*width/300,0.5); // area is ~1/100 of screen
		//projectileArray = firstEntry();
		projectileSpeed = 20;
		left = (int)(height/2 - width/5);
		right = (int)(width/2 + width/5);
		top = (int)(height/2 + height/5);
		bottom = (int)(height/2 + height/5);
		start();
	}
	
	public GameView(Context context) {
		super(context);
		castle = ContextCompat.getDrawable(context,  R.drawable.mycastle);
		//shield = ContextCompat.getDrawable(context,  R.drawable.shield-4);
		this.gameActivity = (GameActivity) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		
	}
	/**
	 * this method draws everything
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		;
		initalizeCanvas(canvas);
		if(drawShield) {
			drawShield(myShield.x,myShield.y,canvas);
		}
	}
	
	/**
	 * draw the canvas. this includes the background, castle, and projectiles. shield is drawn
	 * in onDraw
	 * @param canvas
	 */
	public void initalizeCanvas(Canvas canvas) {
		Paint background = new Paint();
		background.setColor(backgroundcolor);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		Paint textPaint = new Paint();
		textPaint.setColor(getResources().getColor(R.color.black));
		textPaint.setTextSize(25f);
		canvas.drawText("Your score: " + Integer.toString(score), 10, 25, textPaint);
		castle.setBounds(canvas.getClipBounds());
		castle.draw(canvas);
		// need to do this to avoid NPE before window size is captured
		if(target != null) {
		drawTarget(target, canvas);
		}
		// for now, try testing w/o drawing projectieles
		// below here, draw all projectiles
		for(int i = 0; i<projectileArray.length;i++) {
			if(projectileArray[i] != null) {
					drawProjectile(projectileArray[i], canvas);
			}
		}
		//drawShield(shieldCoordinates[0], shieldCoordinates[1], canvas);
	}
	/**
	 * this function draws a black projectile at a given position
	 * @param projectile: projectile to be drawn
	 * @param canvas
	 */
	public void drawProjectile(Projectile projectile, Canvas canvas) {
		float radius = projectile.radius;
		Path circle = new Path();
		Paint circle_paint = new Paint();
		circle_paint.setColor(getResources().getColor(R.color.black));
		circle.addCircle(projectile.x, projectile.y, radius, Direction.CW);
		canvas.drawPath(circle, circle_paint);
		
	}
	/**
	 * this method draws the shield at the correct coordinates
	 * @param x
	 * @param y
	 * @param canvas
	 */
	public void drawShield(double x, double y, Canvas canvas)
	{
		
		Path shieldCircle = new Path();
		Paint shield_paint = new Paint();
		shield_paint.setColor(getResources().getColor(R.color.red));
		shieldCircle.addCircle((float)x, (float)y, shieldRadius, Direction.CW);
		canvas.drawPath(shieldCircle, shield_paint);
	}
	
	public void drawTarget(Projectile target, Canvas canvas) {
		Path myTarget = new Path();
		Paint targetPaint = new Paint();
		targetPaint.setColor(getResources().getColor(R.color.blue));
		myTarget.addCircle(target.x, target.y, target.radius, Direction.CW);
		canvas.drawPath(myTarget, targetPaint);
	}
	
	/*
	 * various logic things:
	 * if shield contacts projectile, projectile disappears
	 * what is contact? if both are circles, then if length(pos_center - pos_center) <= radius + radius
	 * creating projectiles: there should be 1 new projectile every 10 seconds, maxing out at 10
	 * .5 seconds after a projectile is destroyed, a new one is created in its place
	 * projectile speed should be 1/20 of screen diagonal/second
	 * (int)Math.pow(((height * width)/314), 0.5); <-- use this for projectile size
	 * 
	 * 
	 * 
	 */
	
	/**
	 * this method will capture touch events to decide whether to draw the shield
	 * and where
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		myShield.x = event.getX();
		myShield.y = event.getY();
		
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawShield = true; //only draw shield when finger is touching the screen
			
			
			//background = getResources().getColor(R.color.black);
			break;
		case MotionEvent.ACTION_UP:
			drawShield = false;
			
			
			//background = getResources().getColor(R.color.white);
			break;
		}
		invalidate();
		return true;
	}
	/**
	 * this method adds new projectiles to the existing array as called for by numentires
	 * @param myArray: projectile array
	 * @param numEntries: number of projectiles that should exist
	 * @return: updated projectile array
	 */
	public Projectile[] updateProjectileArray(Projectile[] myArray, int numEntries) {
		for(int i = 0; i<numEntries;i++) {
			if (myArray[i] == null) {
				myArray[i] = generateProjectile(projectileRadius, height, width, projectileSpeed);
			}
		}
		return myArray;
	}
	
	/**
	 * this method will generate a new projectile somewhere along the border of the screen with normalized
	 * speed towards the centre
	 * 
	 * @param radius: radius of the desired projectile
	 * @param height: height of the screen
	 * @param width: width of the screen
	 * @param totSpeed: the normalized desired total speed of the projectile (pixels/ms, or something similar)
	 * @return a new projectile
	 */
	public Projectile generateProjectile(float radius, float height, float width, double totTime) {
		// create projectile which will move towards the centre of the screen where castle is
		int myCase = rand.nextInt(4);
		float x = 0;
		float y = 0;
		switch(myCase) { // choose whether to generate at top, left, right, or bottom side
		case 0:
			y = 0;
			x = (float)rand.nextInt((int)width+1);
			break;
		case 1:
			y = height;
			x = (float)rand.nextInt((int)width+1);
			break;
		case 2:
			x = 0;
			y = (float)rand.nextInt((int)height+1);
			break;
		case 3:
			x = width;
			y = (float)rand.nextInt((int)height+1);
			break;
		}
		double[] centre = {width/2, height/2};
		double distFromCentreX = centre[0] - x;
		double distFromCentreY = centre[1] - y;
		double totDistance = Math.pow((Math.pow(distFromCentreX, 2))+(Math.pow(distFromCentreY, 2)), 0.5);
		// suppose totTime is the time (arbitrary units) it takes to translate from border to the centre
		// in this case, totDistance/totSpeed = speedOfProjectile. Then, we find speedcoefficient
		double speedOfProjectile = totDistance/totTime;
		double speedCoefficient = Math.pow(((Math.pow(speedOfProjectile, 2))/(Math.pow(distFromCentreX, 2)+Math.pow(distFromCentreY, 2))),0.5);
		// the speedcoefficient is what we mutiply distfromcentrex and distfromcentrey by to achieve
		// their normalized speeds. we can call it c. We have (cx)^2 + (cy)^2 = s^2 therefore
		// c = ((s^2)/(x^2+y^2))^.5
		
		/*
		 * totspeed^2 = c^2x^2 + c^2y^2
		 */
		// 
		double speedX = (double)(distFromCentreX * speedCoefficient);
		double speedY = (double)(distFromCentreY * speedCoefficient);
		return new Projectile(x, y, speedX, speedY, radius);
	}
	
	/**
	 * the following method is for testing purposes only
	 */
	/*private Projectile[] firstEntry() {
		Projectile[] oneEntry = new Projectile[10];
		return updateProjectileArray(oneEntry, 10);
		
	}
	*/
	
}
