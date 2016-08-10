package com.example.defendthecastle;
import java.util.Random;

public class Projectile {
	
	public float x;
	private static Random rand = new Random();
	public float y;
	public double speedX;
	public double speedY;
	public float radius;
	public String color = "black"; // default to black
	
	public Projectile(float x, float y, double speedX, double speedY, float radius) {
		this.x = x;
		this.y = y;
		this.speedX = speedX;
		this.speedY = speedY;
		this.radius = radius;
	}
	public void translate() {
		this.x += speedX;
		this.y += speedY;
	}
	
	public boolean contact(Projectile projectile) {
		float difX = this.x - projectile.x;
		float difY = this.y - projectile.y;
		double distance = (Math.pow(difX, 2)+Math.pow(difY, 2)); // calculate distance between centres
		return (distance < this.radius + projectile.radius); // determine is this distance is less than radii sum
		// if true, projectiles are in contact
		
	}
	
}
