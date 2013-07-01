package com.example.trial_map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

//this is the screen shown to user when he clicks add event
public class NewEventActivity extends Activity
{
	private static final String		COUNTRY										= "UG";
	private static final String		PLACES_API_BASE		= "https://maps.googleapis.com/maps/api/place";
	private static final String		OUT_JSON									= "/json";

	private static final String		API_KEY										= "AIzaSyCN1vdOEKhXyHSM0IvanKE6FYFoUaWjAPA";

	// JSON Node names
	private static final String		TAG_SUCCESS						= "success";

	// url to create new product
	private static String								url_create_event	= "http://10.0.2.2/android_connect/create_event.php";
	private int																		newEventXmlFile		= R.layout.new_event;
	private PlacesTask											placesTask;
	private ParserTask											parserTask;
	private AutoCompleteTextView	location_auto_complete;
	private EditText													description;
	private TimePicker											timePicker;
	private DatePicker											datePicker;
	private JSONParser											jsonParser							= new JSONParser();
	// Progress Dialog
	private ProgressDialog							pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(newEventXmlFile);

		// get handles to user input fields
		location_auto_complete = (AutoCompleteTextView) findViewById(R.id.autoComplete_location);
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		description = (EditText) findViewById(R.id.textbox_description);
		Button button_addEvent = (Button) findViewById(R.id.button_addEvent);
		Button button_cancel = (Button) findViewById(R.id.button_cancel);

		// make auto_complete work after user types at least 1 word
		location_auto_complete.setThreshold(1);

		
		// add listeners to widgets
		button_addEvent.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				// creating new product in background thread
				new CreateNewEvent().execute();
			}
		});
		
		button_cancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				//close activity
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
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException
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
			Log.d("Exception while downloading url", e.toString());
		}
		finally
		{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... place)
		{
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=AIzaSyCN1vdOEKhXyHSM0IvanKE6FYFoUaWjAPA";

			String input = "";

			try
			{
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			}
			catch (UnsupportedEncodingException e1)
			{
				e1.printStackTrace();
			}

			String country = "components=country:ug";
			// Sensor enabled
			String sensor = "sensor=true";

			// Building the parameters to the web service
			String parameters = input + "&" + country + "&" + sensor + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
					+ output + "?" + parameters;

			try
			{
				// Fetching the data from we service
				data = downloadUrl(url);
			}
			catch (Exception e)
			{
				Log.d("Background Task", e.toString());
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

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>>
	{

		JSONObject	jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData)
		{

			List<HashMap<String, String>> places = null;

			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try
			{
				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			}
			catch (Exception e)
			{
				Log.d("Exception", e.toString());
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

	/**
	 * Background Async Task to Create new product
	 * */
	class CreateNewEvent extends AsyncTask<String, String, String>
	{

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(NewEventActivity.this);
			pDialog.setMessage("Creating Event..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating event
		 * */
		protected String doInBackground(String... args)
		{
			// get location entered
			String location = location_auto_complete.getText().toString();
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

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("location", location));
			params.add(new BasicNameValuePair("time", time));
			params.add(new BasicNameValuePair("date", date));
			params.add(new BasicNameValuePair("description", desc));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_create_event, "POST",
					params);

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try
			{
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					// successfully created product
					Toast.makeText(getApplicationContext(), "Event Sucessfully Created", Toast.LENGTH_SHORT).show();
					// closing this screen
					finish();
				}
				else
				{
					// failed to create product
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url)
		{
			// dismiss the dialog once done
			pDialog.dismiss();
		}

	}
}
