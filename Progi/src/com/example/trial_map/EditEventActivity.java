package com.example.trial_map;

import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.trial_map.asyncTasks.AutoCompleteTask;
import com.example.trial_map.asyncTasks.UpdateEventTask;
import com.example.trial_map.beans.Event;
import com.example.trial_map.beans.EventOwner;
import com.example.trial_map.factories.EventsFactory;
import com.example.trial_map.factories.NetworkManager;
import com.google.android.gms.maps.model.LatLng;

/**
 * This activity helps a user to edit an event already in the database
 **/
public class EditEventActivity extends ActionBarActivity
{
	// constants
	private static final int					NEW_EVENT_XML						= R.layout.new_event;
	private static final String				GEOCODE_ADDRESS					= "https://maps.googleapis.com/maps/api/geocode/json";
	private static final String				TAG_RESULTS							= "results";
	private static final String				TAG_STATUS							= "status";
	private static final String				TIME_DELIMETER					= ":";
	private static final String				DATE_DELIMETER					= "/";
	private static final CharSequence	PROGRESS_DIALOG_TEXT		= "Getting Input Data...";
	private final CharSequence				INVALID_USER_INPUT_TEXT	= "Please Fill In All The Required Fields Before Submiiting Data";
	// widgets
	private AutoCompleteTextView			location_auto_complete;
	private EditText									description;
	private TimePicker								timePicker;
	private DatePicker								datePicker;
	private Button										button_saveEvent;

	private EditText									name_of_event;
	private Spinner										duration_of_event;
	private Spinner										type_of_event;
	private int												user_id;
	private Resources									res;
	private String[]									duration_array;
	private String[]									type_array;

	private EventOwner								anEventOwner						= MainActivity.anEventOwner;
	private int												event_id;
	private CharSequence							NO_LOCATION_FOUND_TEXT	= "Failed To Find Location Of Event.Event Was Not Created";
	private ProgressDialog						pDialog;


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
		// EnableWidgets(false);

		// get user id from previous activity
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			Event anEvent = getEvent();
			fillWidgetsWithEventData(anEvent);
		}

		// make auto_complete work after user types at least 1 word
		location_auto_complete.setThreshold(1);

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

		location_auto_complete.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				AutoCompleteTask autoCompleteTask = new AutoCompleteTask(EditEventActivity.this, location_auto_complete);
				autoCompleteTask.execute(s.toString());
			}


			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{

			}


			@Override
			public void afterTextChanged(Editable s)
			{

			}
		});

		location_auto_complete.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// if user picks location from drop down list
				EnableWidgets(true);
			}
		});
	}


	/**
	 * @param anEvent
	 *          the event that was selected from previous activity this method
	 *          fills the widgets with data from that selected event
	 */
	private void fillWidgetsWithEventData(Event anEvent)
	{
		res = getResources();
		duration_array = res.getStringArray(R.array.duration_array);
		type_array = res.getStringArray(R.array.type_array);
		location_auto_complete.setText(anEvent.getEvent_location_in_words());
		description.setText(anEvent.getDescription_of_event());
		name_of_event.setText(anEvent.getName_of_event());
		setduration(duration_of_event, anEvent.getDuration());
		setDate(datePicker, anEvent.getDate());
		setTime(timePicker, anEvent.getTime());
		setType(type_of_event, anEvent.getType_of_event());
	}


	/**
	 * @param type_of_event
	 *          spinner whose value to change
	 * @param type
	 *          value to which to set spinner to
	 */
	private void setType(Spinner type_of_event, String type)
	{
		int position = 0;
		// loop thru the durations array to find position of item that was selected
		// by user
		for (int i = 0; i < type_array.length; i++)
		{
			if (type_array[i].equalsIgnoreCase(type))
			{
				position = i;
				break;
			}
		}
		// make the spinner display that selection
		type_of_event.setSelection(position);
	}


	/**
	 * @return the event sent by previous activity
	 */
	private Event getEvent()
	{
		Bundle aBundle = getIntent().getExtras();
		if (aBundle != null)
		{
			try
			{
				Double latitude = aBundle.getDouble("latitude");
				Double longitude = aBundle.getDouble("longitude");
				String time = aBundle.getString("time");
				String date = aBundle.getString("date");
				String description = aBundle.getString("description");
				String name = aBundle.getString("name");
				String duration = aBundle.getString("duration");
				String location_in_words = aBundle.getString("location_in_words");
				if (anEventOwner != null)
				{
					user_id = anEventOwner.getUser_id();
					Toast.makeText(EditEventActivity.this, "User_id=" + user_id, Toast.LENGTH_SHORT).show();
				}
				event_id = aBundle.getInt("event_id");
				String type = aBundle.getString("type");
				return new Event(latitude, longitude, time, date, description, name, duration, location_in_words, user_id, event_id, type);
			}
			catch (Exception e)
			{
				// Log.e("GET EVENT", e.getMessage());
				return null;
			}

		}
		return null;
	}


	/** sets date to the event date */
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


	/** sets the duration to event duration **/
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


	/** sets the time to event time **/
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


	/** closes an open progress dialog **/
	private void closeProgressDialog()
	{
		if (pDialog != null)
		{
			pDialog.dismiss();
		}
	}


	/** shows a progress dialog to the user **/
	private void showProgressDialog()
	{
		// create progress dialog and display it to user
		pDialog = new ProgressDialog(this);
		pDialog.setMessage(PROGRESS_DIALOG_TEXT);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}


	/** returns the latitude and longitude of location chosen by user **/
	private LatLng getLatLngOfLocationInputByUser(String location_in_words)
	{
		// replace all commas in the input with a + sign
		location_in_words = location_in_words.replaceAll(",", "+");
		// replace all spaces in the input with a %20 sign
		location_in_words = location_in_words.replaceAll(" ", "%20");
		// finally generate address parameter
		String address = "address=" + location_in_words;
		// set required sensor parameter
		String sensor = "sensor=true";
		// set parameters into 1 string
		String paramaters = sensor + "&" + address;
		// generate url
		String url = GEOCODE_ADDRESS + "?" + paramaters;

		// make the request to google
		JSONObject jsonObject = new NetworkManager().makeHttpGetRequest(url);

		try
		{// to get latitude and longitude
			double latitude;
			double longitude;
			String status = jsonObject.getString(TAG_STATUS);
			if (status.equalsIgnoreCase("ok"))
			{
				// Getting Array of results
				JSONArray results = jsonObject.getJSONArray(TAG_RESULTS);
				// get results object from result array
				JSONObject result_components = results.getJSONObject(0);
				// get geometry object from json results
				JSONObject geometery = result_components.getJSONObject("geometry");
				// get location object from json geometry
				JSONObject location = geometery.getJSONObject("location");
				// finally read the latitude and longitude values from object
				latitude = location.getDouble("lat");
				longitude = location.getDouble("lng");
				// return new latlng object
				return new LatLng(latitude, longitude);
			}
			else
			{// there are no results from google places
				return null;
			}

		}
		catch (JSONException e)
		{
			// Log.e("JSON Parser", "" + e.getMessage());
		}
		return null;
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


	/** async task that gets user input and then updates the event in background **/
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
			LatLng location = getLatLngOfLocationInputByUser(location_in_words);
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
			String time = hour + ":" + min;

			// get Description
			String desc = description.getText().toString();

			// get name of event
			String event_name = name_of_event.getText().toString();

			// get duration of event
			String event_duration = duration_of_event.getSelectedItem().toString();

			// get type of event
			String type = type_of_event.getSelectedItem().toString();

			// store all info in an event object
			Event anEvent = new Event(location.latitude, location.longitude, time, date, desc, event_name, event_duration, location_in_words, user_id, -1, type);

			return anEvent;
		}


		@Override
		protected void onPostExecute(Event anEvent)
		{
			closeProgressDialog();
			if (anEvent == null)
			{// the location of the event could not be geocoded
				Toast.makeText(EditEventActivity.this, NO_LOCATION_FOUND_TEXT, Toast.LENGTH_LONG).show();
				return;
			}
			if (EventsFactory.EventIsValid(anEvent))
			{// event is valid.save the event
				UpdateEventTask updateEventTask = new UpdateEventTask(EditEventActivity.this);
				updateEventTask.execute(anEvent);
			}
			else
			{// event not valid.user input is wrong inform user
				Toast.makeText(EditEventActivity.this, INVALID_USER_INPUT_TEXT, Toast.LENGTH_LONG).show();
			}

		}

	}

}
