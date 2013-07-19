package com.example.trial_map;

import java.util.StringTokenizer;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.trial_map.beans.Event;
import com.example.trial_map.widgets.CustomAutoCompleteTextView;

public class EventDetailsActivity extends SherlockActivity
{
	private static final int						eventDetailsScreen	= R.layout.new_event;
	private static final String					DATE_DELIMETER			= "/";
	private static final String					TIME_DELIMETER			= ":";
	private static final int						BACK								= R.id.menu_back;
	// widgets
	private CustomAutoCompleteTextView	location_autoComplete;
	private EditText										name_editText;
	private TimePicker									time_picker;
	private Spinner											duration_spinner;
	private DatePicker									date_picker;
	private EditText										description_editText;
	private Resources										res;
	private String[]										duration_array;
	private Button											button_saveEvent;
	private Button											button_close;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(eventDetailsScreen);
		res = getResources();
		duration_array = res.getStringArray(R.array.duration_array);

		// get objects of the widgets
		location_autoComplete = (CustomAutoCompleteTextView) findViewById(R.id.autoComplete_location);
		name_editText = (EditText) findViewById(R.id.editText_name);
		time_picker = (TimePicker) findViewById(R.id.timePicker);
		duration_spinner = (Spinner) findViewById(R.id.spinner);
		date_picker = (DatePicker) findViewById(R.id.datePicker);
		description_editText = (EditText) findViewById(R.id.editText_description);
		button_saveEvent = (Button) findViewById(R.id.button_addEvent);
		button_close = (Button) findViewById(R.id.button_cancel);
		// change the text on the buttons
		button_saveEvent.setText("SAVE");
		button_close.setText("CLOSE");
		// add listeners to buttons
		button_close.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setResult(RESULT_OK);
				finish();
			}
		});
		// get event picked by user
		Event anEvent = getEvent(getIntent());
		if (anEvent != null)
		{
			Log.e("event gotten", "" + anEvent.getDescription_of_event());
			Log.e("event gotten", "" + anEvent.getDate());
			Log.e("event gotten", "" + anEvent.getName_of_event());
			Log.e("event gotten", "" + anEvent.getTime());
			Log.e("event gotten", "" + anEvent.getDuration());
			Log.e("event gotten", "" + anEvent.getLatitude());
			Log.e("event gotten", "" + anEvent.getLongitude());
			Log.e("event gotten", "" + anEvent.getEvent_location_in_words());
			showEventDetails(anEvent);
			// disable widgets so user cant change details
			EnableWidgets(false);

		}
		else
		{
			Log.e("event gotten", "event is null");
		}

	}

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

	// enable or disable all activity widgets
	private void EnableWidgets(boolean b)
	{
		location_autoComplete.setEnabled(b);
		name_editText.setEnabled(b);
		time_picker.setEnabled(b);
		duration_spinner.setEnabled(b);
		date_picker.setEnabled(b);
		description_editText.setEnabled(b);
		button_saveEvent.setEnabled(b);

	}

	// fills widgets with event details
	private void showEventDetails(Event anEvent)
	{
		location_autoComplete.setText(anEvent.getEvent_location_in_words().toUpperCase());
		name_editText.setText(anEvent.getName_of_event().toUpperCase());
		setTime(time_picker, anEvent.getTime().toUpperCase());
		setduration(duration_spinner, anEvent.getDuration().toUpperCase());
		setDate(date_picker, anEvent.getDate().toUpperCase());
		description_editText.setText(anEvent.getDescription_of_event().toUpperCase());

	}

	// sets date to the event date
	private void setDate(DatePicker datePicker, String date)
	{
		// break up the date string into sub tokens
		StringTokenizer stringTokenizer = new StringTokenizer(date, DATE_DELIMETER);
		// retrieve and convert the day,month and year
		int day = Integer.parseInt(stringTokenizer.nextToken());
		int month = Integer.parseInt(stringTokenizer.nextToken());
		int year = Integer.parseInt(stringTokenizer.nextToken());
		// update the date picker to display the current date
		datePicker.updateDate(year, month - 1, day);

	}

	// sets the duration to event duration
	private void setduration(Spinner spinner, String duration)
	{
		int position = 0;
		// loop thru the durations array to find position of item that was selected
		// by user
		for (int i = 0; i < duration_array.length; i++)
		{
			if (duration_array[i].equalsIgnoreCase(duration))
			{
				position = i;
				break;
			}
		}
		// make the spinner display that selection
		spinner.setSelection(position);
	}

	// sets the time to event time
	private void setTime(TimePicker time_picker, String time)
	{
		// break up the time string into tokens
		StringTokenizer stringTokenizer = new StringTokenizer(time, TIME_DELIMETER);
		// change the hour and minute tokens into integers
		int hour = Integer.parseInt(stringTokenizer.nextToken());
		int min = Integer.parseInt(stringTokenizer.nextToken());
		// make time picker a 24 hour clock
		time_picker.setIs24HourView(true);
		// set the time of the time picker
		time_picker.setCurrentHour(hour);
		time_picker.setCurrentMinute(min);

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
