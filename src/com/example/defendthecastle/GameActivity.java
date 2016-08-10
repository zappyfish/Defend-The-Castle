package com.example.defendthecastle;


import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GameView gameView = new GameView(this);
		setContentView(gameView);
		gameView.requestFocus();
	}
	
}
