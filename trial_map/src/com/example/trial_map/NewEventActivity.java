package com.example.trial_map;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.google.android.gms.maps.model.LatLng;

//this is the screen shown to user when he clicks add event
public class NewEventActivity extends Activity
{
	private static final String		COUNTRY								= "ug";
	private static final String		PLACES_API_BASE				= "https://maps.googleapis.com/maps/api/place/autocomplete/";
	private static final String		GOOGLE_PLACES_API_KEY	= "AIzaSyCN1vdOEKhXyHSM0IvanKE6FYFoUaWjAPA";
	private static final int			NEW_EVENT_XML					= R.layout.new_event;
	private static final String		GEOCODE_ADDRESS				= "https://maps.googleapis.com/maps/api/geocode/json";
	private static final String		TAG_RESULTS						= "results";
	private static final String		TAG_STATUS						= "status";

	private PlacesTask						placesTask;
	private ParserTask						parserTask;
	// widgets
	private AutoCompleteTextView	location_auto_complete;

	private EditText							description;
	private TimePicker						timePicker;
	private DatePicker						datePicker;
	private Button								button_addEvent;
	private Button								button_cancel;
	private EditText							name_of_event;
	private Spinner								duration_of_event;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(NEW_EVENT_XML);

		// get handles to user input fields
		location_auto_complete = (AutoCompleteTextView) findViewById(R.id.autoComplete_location);
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		description = (EditText) findViewById(R.id.textbox_description);
		button_addEvent = (Button) findViewById(R.id.button_addEvent);
		button_cancel = (Button) findViewById(R.id.button_cancel);
		name_of_event = (EditText) findViewById(R.id.editText_name);
		duration_of_event = (Spinner) findViewById(R.id.spinner);

		// disable screen gui till user picks location from auto_complete text box
		EnableWidgets(false);

		// make auto_complete work after user types at least 1 word
		location_auto_complete.setThreshold(1);
		

		// add listeners to widgets

		button_addEvent.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (dataIsValid())
				{
					// saving event in background thread
					new SaverTask().execute();
					// close activity
					finishActivity(100);
				}

			}

			private boolean dataIsValid()
			{
				// check date

				return true;
			}
		});

		button_cancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				// close activity
				finish();
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
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
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				// if user picks location from drop down list
				EnableWidgets(true);
			}
		});
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
	class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>>
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
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result,
					android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			location_auto_complete.setAdapter(adapter);
		}
	}

	// saves new event in background thread using network to send data
	class SaverTask extends AsyncTask<String, String, String>
	{

		private String					result	= null;
		private ProgressDialog	pDialog;

		@Override
		protected void onPreExecute()
		{
			// create progress dialog and display it to user
			pDialog = new ProgressDialog(NewEventActivity.this);
			pDialog.setMessage("Saving Event. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params)
		{
			// get user input
			String location_in_words = location_auto_complete.getText().toString();
			LatLng location = getLatLngOfLocationInputByUser(location_in_words);
			if (location == null)
			{
				String text="Failed to find location Of Event From Google";		
				return text;
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

			// store all info in an event object
			Event anEvent = new Event(location, time, date, desc, event_name,
					event_duration);

			// save the event
			result = EventsFactory.SaveEvent(NewEventActivity.this, anEvent);
			return result;
		}

		@Override
		protected void onPostExecute(String string)
		{
			// dismiss the progress dialog
			pDialog.dismiss();
			// tell user event is saved or failed to save
			Toast.makeText(NewEventActivity.this, "" + string, Toast.LENGTH_LONG)
					.show();

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
			{//there are no results from google places
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
		button_addEvent.setEnabled(bool);
		timePicker.setEnabled(bool);
		datePicker.setEnabled(bool);
		description.setEnabled(bool);
		name_of_event.setEnabled(bool);
		duration_of_event.setEnabled(bool);
	}
}
