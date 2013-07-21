package com.example.trial_map;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.trial_map.beans.Event;
import com.example.trial_map.beans.EventOwner;
import com.example.trial_map.factories.EventsFactory;
import com.example.trial_map.factories.JSONParser;
import com.google.android.gms.maps.model.LatLng;

public class EditEventActivity extends SherlockActivity
{
	private static final String				COUNTRY								= "ug";
	private static final String				PLACES_API_BASE				= "https://maps.googleapis.com/maps/api/place/autocomplete/";
	private static final String				GOOGLE_PLACES_API_KEY	= "AIzaSyCN1vdOEKhXyHSM0IvanKE6FYFoUaWjAPA";
	private static final int					NEW_EVENT_XML					= R.layout.new_event;
	private static final int					NO_LOCATION_FOUND			= 3;
	private static final String				GEOCODE_ADDRESS				= "https://maps.googleapis.com/maps/api/geocode/json";
	private static final String				TAG_RESULTS						= "results";
	private static final String				TAG_STATUS						= "status";
	private static final CharSequence	SAVE_BUTTON_TEXT			= "SAVE";
	private static final int					BACK									= R.id.menu_back;
	private static final String				TIME_DELIMETER				= ":";
	private static final String				DATE_DELIMETER				= "/";

	// background threads
	private PlacesTask								placesTask;
	private ParserTask								parserTask;
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

	private EventOwner								anEventOwner					= MainActivity.anEventOwner;
	private int												event_id;
	
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
		// change the text on the buttons
		button_saveEvent.setText(SAVE_BUTTON_TEXT);

		// disable screen gui till user picks location from auto_complete text box
		EnableWidgets(false);
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
					// saving event in background thread
					new SaverTask().execute();
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
				placesTask = new PlacesTask();
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				// TODO Auto-generated method stub
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
				Log.e("GET EVENT", e.getMessage());
				return null;
			}

		}
		return null;
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

	// Fetches all places from GooglePlaces AutoComplete Web Service
	class PlacesTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... place)
		{
			// For storing data from web service
			String data = "";
			try
			{

				// Obtain browser key from https://code.google.com/apis/console
				String key = "key=" + GOOGLE_PLACES_API_KEY;

				String input = "";

				input = "input=" + URLEncoder.encode(place[0], "utf-8");

				String country = "components=country:" + COUNTRY;
				// Sensor enabled
				String sensor = "sensor=true";

				// Building the parameters to the web service
				String parameters = input + "&" + country + "&" + sensor + "&" + key;

				// Output format
				String output = "json";

				// Building the url to the web service
				String url = PLACES_API_BASE + output + "?" + parameters;

				// Fetching the data from we service
				data = downloadUrl(url);
			}
			catch (Exception e)
			{
				Log.d("Exception:", e.getMessage());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);

			// Creating ParserTask
			parserTask = new ParserTask();

			// Starting Parsing the JSON string returned by Web Service
			parserTask.execute(result);
		}
	}

	// A class to parse the Google Places in JSON format
	class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>>
	{

		JSONObject	jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData)
		{

			List<HashMap<String, String>> places = null;

			JSONParser placeJsonParser = new JSONParser();

			try
			{
				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			}
			catch (Exception e)
			{
				Log.d("Exception in Parsing", e.toString());
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result)
		{

			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			location_auto_complete.setAdapter(adapter);
		}
	}

	// saves new event in background thread using network to send data
	class SaverTask extends AsyncTask<String, String, Integer>
	{
		private final CharSequence	PROGRESS_DIALOG_TEXT	= "Saving Event. Please wait...";
		private Integer							result;
		private ProgressDialog			pDialog;

		@Override
		protected void onPreExecute()
		{
			// create progress dialog and display it to user
			pDialog = new ProgressDialog(EditEventActivity.this);
			pDialog.setMessage(PROGRESS_DIALOG_TEXT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Integer doInBackground(String... params)
		{
			// get location of event
			String location_in_words = location_auto_complete.getText().toString();
			// get latitude and longitude of event
			LatLng location = getLatLngOfLocationInputByUser(location_in_words);
			// if no latitude and longitude can be found for given location
			if (location == null)
			{
				return NO_LOCATION_FOUND;
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
			Event anEvent = new Event(location.latitude, location.longitude, time, date, desc, event_name, event_duration, location_in_words, user_id, event_id, type);

			// save the event
			result = EventsFactory.UpdateEvent(anEvent);
			return result;
		}

		@Override
		protected void onPostExecute(Integer integer)
		{

			// dismiss the progress dialog
			pDialog.dismiss();

			String text;
			int duration = Toast.LENGTH_LONG;

			switch (integer)
			{
			// if event was saved
				case EventsFactory.SUCCESS:
					// set result
					setResult(RESULT_OK);
					// close activity
					finish();
					break;
				// if we failed to save event coz of server side error
				case EventsFactory.FAILURE:
					text = "Failed To Save Event";
					Toast.makeText(EditEventActivity.this, text, duration).show();
					break;
				// if there is no Internet connection to server
				case EventsFactory.NO_CONNECTION:
					text = "Sorry but there is no connection to the server";
					Toast.makeText(EditEventActivity.this, text, duration).show();
					break;
				// if user entered a location whose coordinates cant be found
				case NO_LOCATION_FOUND:
					text = "Failed To Find Location Of Event.Event Was Not Created";
					Toast.makeText(EditEventActivity.this, text, duration).show();
					break;
			}

		}
	}

	// A method to download json data from url
	private String downloadUrl(String strUrl)
	{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try
		{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		}
		catch (Exception e)
		{
			Log.d("Exception while downloading url", e.getMessage());
		}
		finally
		{
			try
			{
				iStream.close();
				urlConnection.disconnect();
			}
			catch (Exception e)
			{
				Log.d("Exception while Closing url", e.getMessage());
			}

		}
		return data;
	}

	// returns the latitude and longitude of location chosen by user
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
		JSONObject jsonObject = new JSONParser().makeHttpGetRequest(url);

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
			Log.e("JSON Parser", "" + e.getMessage());
		}
		return null;
	}

	// enables or disables gui widgets
	private void EnableWidgets(boolean bool)
	{
		button_saveEvent.setEnabled(bool);
		timePicker.setEnabled(bool);
		datePicker.setEnabled(bool);
		description.setEnabled(bool);
		name_of_event.setEnabled(bool);
		duration_of_event.setEnabled(bool);
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
