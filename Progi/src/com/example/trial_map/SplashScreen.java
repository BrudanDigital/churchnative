package com.example.trial_map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ImageView;

public class SplashScreen extends Activity
{

	private static final long	VIBRATION_DURATION	= 500;

	// The thread to process splash screen events
	private Thread						mSplashThread;
	private Vibrator					myVib;


	/** Called when the app is first starting. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//vibrate to thank user
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		myVib.vibrate(VIBRATION_DURATION);
		// Splash screen view
		setContentView(R.layout.splash_screen);

		// Start animating the image
		final ImageView splashImageView = (ImageView) findViewById(R.id.SplashImageView);

		final SplashScreen sPlashScreen = this;

		// The thread to wait for splash screen events
		mSplashThread = new Thread()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{
				Intent intent = new Intent();
				try
				{
					synchronized (this)
					{
						splashImageView.setBackgroundResource(R.drawable.progi_logo);
						// Run next activity
						intent.setClass(sPlashScreen, MainActivity.class);
						// Wait given period of time or exit on touch
						wait(3000);
					}
				}
				catch (InterruptedException ex)
				{
				}
				startActivity(intent);
				finish();
				stop();
			}
		};

		mSplashThread.start();

	}

}
