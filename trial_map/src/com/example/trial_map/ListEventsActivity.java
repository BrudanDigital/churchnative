package com.example.trial_map;

import java.util.ArrayList;
import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ListEventsActivity extends ListActivity
{
	ArrayList<String>	events_stringArray	= EventsFactory
																						.getEventsAsStringArray();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setBackgroundColor(Color.BLACK);
		if (events_stringArray == null)
		{
			Log.e("LIST EVENTS", "array is null");
			return;
		}
		setListAdapter(new CustomArrayAdapter(this, events_stringArray));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		//get selected items
		//TextView textView=(TextView)v;
		//textView.setBackgroundColor(0xFF0000FF);
		String selectedValue = (String) getListAdapter().getItem(position);
		Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();

	}
}
