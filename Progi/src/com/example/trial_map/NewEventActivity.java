package com.example.trial_map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.trial_map.adapters.PlacesAutoCompleteAdapter;
import com.example.trial_map.asyncTasks.SaverTask;
import com.example.trial_map.beans.Event;
import com.example.trial_map.managers.EventManager;
import com.example.trial_map.managers.LocationManager;
import com.example.trial_map.managers.Manager;
import com.google.android.gms.maps.model.LatLng;

/** this activity helps user to create a new event on the server side **/
public class NewEventActivity extends ActionBarActivity implements OnItemClickListener
{
	private static final int			NEW_EVENT_XML								= R.layout.new_event;
	
	

	// widgets
	private AutoCompleteTextView	location_auto_complete;
	private EditText							description;
	private TimePicker						timePicker;
	private DatePicker						datePicker;
	private Button								button_saveEvent;

	private EditText							name_of_event;
	private Spinner								duration_of_event;
	private Spinner								type_of_event;
	private int										user_id;
	private int										total_people_who_have_heard	= 0;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(NEW_EVENT_XML);

		// get objects of widgets
		location_auto_complete = (AutoCompleteTextView) findViewById(R.id.autoComplete_location);
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		description = (EditText) findViewById(R.id.editText_description);
		button_saveEvent = (Button) findViewById(R.id.button_addEvent);
		name_of_event = (EditText) findViewById(R.id.editText_name);
		duration_of_event = (Spinner) findViewById(R.id.spinner);
		type_of_event = (Spinner) findViewById(R.id.eventType_spinner);
		// disable screen gui till user picks location from auto_complete text box
		EnableWidgets(false);
		// get user id from previous activity
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			user_id = extras.getInt("user_id");
		}

		// add listeners to widgets
		button_saveEvent.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (dataIsValid())
				{
					GetUserInputTask getUserInputTask = new GetUserInputTask();
					getUserInputTask.execute();
				}

			}


			private boolean dataIsValid()
			{
				// check date

				return true;
			}
		});

		location_auto_complete.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));

		location_auto_complete.setOnItemClickListener(this);

	}


	
	/** enables or disables gui widgets **/
	private void EnableWidgets(boolean bool)
	{
		button_saveEvent.setEnabled(bool);
		timePicker.setEnabled(bool);
		datePicker.setEnabled(bool);
		description.setEnabled(bool);
		name_of_event.setEnabled(bool);
		duration_of_event.setEnabled(bool);
	}


	/**
	 * async task that gets user input and then saves it.all happens in background
	 **/
	private class GetUserInputTask extends AsyncTask<Void, String, Event>
	{

		@Override
		protected void onPreExecute()
		{
			showProgressDialog();
		}


		@Override
		protected Event doInBackground(Void... params)
		{
			// get location of event
			String location_in_words = location_auto_complete.getText().toString();
			// get latitude and longitude of event
			LatLng location = LocationManager.getLatLngOfLocationInputByUser(location_in_words);
			// if no latitude and longitude can be found for given location
			if (location == null)
			{
				return null;
			}
			// get date
			int day = datePicker.getDayOfMonth();
			int month = datePicker.getMonth() + 1;
			int year = datePicker.getYear();
			String date = day + "/" + month + "/" + year;

			// get time
			int hour = timePicker.getCurrentHour();
			int min = timePicker.getCurrentMinute();
			String minutes = "" + min;
			if (min < 10)
			{// less than ten minutes past the hour
				// add a 0 before the digit
				minutes = "0" + minutes;
			}

			String time = hour + ":" + minutes;

			// get Description
			String desc = description.getText().toString();

			// get name of event
			String event_name = name_of_event.getText().toString();

			// get duration of event
			String event_duration = duration_of_event.getSelectedItem().toString();

			// get type of event
			String type = type_of_event.getSelectedItem().toString();

			// store all info in an event object
			Event anEvent = new Event(location.latitude, location.longitude, time, date, desc, event_name, event_duration, location_in_words, user_id, -1, type, true, total_people_who_have_heard);

			return anEvent;
		}


		@Override
		protected void onPostExecute(Event anEvent)
		{
			closeProgressDialog();
			// no event is returned becoz no location for the event could be found
			if (anEvent == null)
			{
				displayToast(NewEventActivity.this, Manager.MESSAGE, LONG_DURATION);
				return;
			}
			if (EventManager.EventIsValid(anEvent) == EventManager.SUCCESS)
			{// event is valid.save the event

				SaverTask saverTask = new SaverTask(NewEventActivity.this);
				saverTask.execute(anEvent);
			}
			else if (EventManager.EventIsValid(anEvent) == EventManager.DATE_ERROR)
			{
				// event not valid.user input is wrong inform user
				displayToast(NewEventActivity.this, Manager.MESSAGE, LONG_DURATION);
				return;
			}
			else
			{// event not valid.user input is wrong inform user
				displayToast(NewEventActivity.this, Manager.MESSAGE, LONG_DURATION);
				return;
			}

		}

	}


	/** called when user clicks on item from auto complete drop down list **/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// if user picks location from drop down list
		EnableWidgets(true);
	}

}
