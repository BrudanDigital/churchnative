package com.example.trial_map;

import android.app.Activity;
import android.os.Bundle;

//this is the screen shown to user when he clicks add event
public class NewEvent extends Activity
{
	private int newEventXmlFile=R.layout.get_event;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(newEventXmlFile);
		
	}
}
