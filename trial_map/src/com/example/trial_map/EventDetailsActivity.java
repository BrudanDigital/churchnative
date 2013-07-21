package com.example.trial_map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.trial_map.beans.Event;

public class EventDetailsActivity extends SherlockActivity
{
	private static final int	eventDetailsScreen	= R.layout.event_details;
	private static final int	BACK								= R.id.menu_back;
	// widgets
	private TextView					location_TextView;
	private TextView					name_TextView;
	private TextView					time_TextView;
	private TextView					duration_TextView;
	private TextView					date_TextView;
	private TextView					description_TextView;
	private TextView					type_TextView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(eventDetailsScreen);

		// get objects of the widgets
		location_TextView = (TextView) findViewById(R.id.details_locationValue);
		name_TextView = (TextView) findViewById(R.id.details_title);
		type_TextView = (TextView) findViewById(R.id.details_typeValue);
		time_TextView = (TextView) findViewById(R.id.details_timeValue);
		duration_TextView = (TextView) findViewById(R.id.details_durationValue);
		date_TextView = (TextView) findViewById(R.id.details_dateValue);
		description_TextView = (TextView) findViewById(R.id.details_descriptionValue);

		// get event picked by user
		Event anEvent = getEvent(getIntent());
		if (anEvent != null)
		{
			showEventDetails(anEvent);
		}
		else
		{
			Log.e("event gotten", "event is null");
		}

	}

	// gets event passed as parameters from previous activity
	private Event getEvent(Intent intent)
	{

		Bundle aBundle = intent.getExtras();
		if (aBundle != null)
		{
			Double latitude = aBundle.getDouble("latitude");
			Double longitude = aBundle.getDouble("longitude");
			String time = aBundle.getString("time");
			String date = aBundle.getString("date");
			String description = aBundle.getString("description");
			String name = aBundle.getString("name");
			String duration = aBundle.getString("duration");
			String location_in_words = aBundle.getString("location_in_words");
			int user_id = aBundle.getInt("user_id");
			int event_id = aBundle.getInt("event_id");
			String type = aBundle.getString("type");
			return new Event(latitude, longitude, time, date, description, name, duration, location_in_words, user_id, event_id, type);
		}
		return null;
	}

	// fills widgets with event details
	private void showEventDetails(Event anEvent)
	{
		location_TextView.setText(anEvent.getEvent_location_in_words());
		name_TextView.setText(anEvent.getName_of_event().toUpperCase());
		type_TextView.setText(anEvent.getType_of_event());
		time_TextView.setText(anEvent.getTime());
		duration_TextView.setText(anEvent.getDuration());
		date_TextView.setText(anEvent.getDate());
		description_TextView.setText(anEvent.getDescription_of_event());

	}

	// method called to create menu and its items
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// show action bar
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		// add menu options to the UI
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.layout.menu_custom, menu);
		return true;
	}

	// handler for click on menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem menu_item)
	{

		switch (menu_item.getItemId())
		{
			case BACK:
				setResult(RESULT_CANCELED);
				finish();
				return true;
		}
		return super.onOptionsItemSelected(menu_item);

	}
}
