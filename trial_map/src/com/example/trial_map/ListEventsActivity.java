package com.example.trial_map;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.trial_map.beans.Event;
import com.example.trial_map.factories.EventsFactory;
import com.example.trial_map.widgets.CustomArrayAdapter;

//This class displays all events in 10km as a list for user to select from
public class ListEventsActivity extends ListActivity
{
	private static final int	DETAILS_ACTIVITY_RESULT_CODE	= 100;
	private static int				NO_EVENTS_RESULT_CODE					= 350;
	ArrayList<Event>					events_ArrayList							= EventsFactory.getEventsIn10KmRadius();
	private View							view													= null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// make background black
		getWindow().getDecorView().setBackgroundColor(Color.BLACK);
		// check if events exist
		if (events_ArrayList == null)
		{// if no events then finish activity and return to main
			Log.e("LIST EVENTS", "array is null");
			setResult(NO_EVENTS_RESULT_CODE);
			finish();
			return;
		}
		// set list adapter to my customer adapter
		setListAdapter(new CustomArrayAdapter(this, getEventsAsStringArray()));
	}

	//handler for click on an item by user
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		//make view global
		view = v;
		v.setBackgroundColor(getResources().getColor(R.color.orange));
		// create a new sub activity
		Intent intent = new Intent(ListEventsActivity.this, EventDetailsActivity.class);
		// Use helper method to send event to next activity
		sendEvent(intent, events_ArrayList.get(position));
		// start new sub activity
		startActivityForResult(intent, DETAILS_ACTIVITY_RESULT_CODE);

	}

	// handle results returned by sub activities
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == DETAILS_ACTIVITY_RESULT_CODE)
		{
			if (view != null)
			{
				view.setBackgroundColor(Color.BLACK);
			}
		}
	}

	//breaks event into primitives so data can be sent to activity
	private void sendEvent(Intent intent, Event anEvent)
	{
		// get object state
		Double latitude = anEvent.getLatitude();
		Log.e("sendEvent", "" + latitude);
		Double longitude = anEvent.getLongitude();
		Log.e("sendEvent", "" + longitude);
		String time = anEvent.getTime();
		Log.e("sendEvent", "" + time);
		String date = anEvent.getDate();
		Log.e("sendEvent", "" + date);
		String description = anEvent.getDescription_of_event();
		Log.e("sendEvent", "" + description);
		String name = anEvent.getName_of_event();
		Log.e("sendEvent", "" + name);
		String duration = anEvent.getDuration();
		String location_in_words = anEvent.getEvent_location_in_words();
		int user_id=anEvent.getUser_id();
		int event_id=anEvent.getEvent_id();
		String type=anEvent.getType_of_event();
		// create bundle to store all the object state
		Bundle extras = new Bundle();
		// add state to bundle
		extras.putDouble("latitude", latitude);
		extras.putDouble("longitude", longitude);
		extras.putString("time", time);
		extras.putString("date", date);
		extras.putString("description", description);
		extras.putString("name", name);
		extras.putString("duration", duration);
		extras.putString("location_in_words", location_in_words);
		extras.putInt("user_id", user_id);
		extras.putInt("event_id", event_id);
		extras.putString("type", type);
		// add bundle to intent
		intent.putExtras(extras);
	}

	//returns event descriptions in string array
	public ArrayList<String> getEventsAsStringArray()
	{

		if (events_ArrayList == null)
		{
			return null;
		}
		Iterator<Event> iterator = events_ArrayList.iterator();
		ArrayList<String> eventStrings = new ArrayList<String>();
		while (iterator.hasNext())
		{
			Log.e("GET EVENTS", "events gotten");
			Event event = (Event) iterator.next();
			eventStrings.add(event.getName_of_event());
		}
		return eventStrings;
	}
}
