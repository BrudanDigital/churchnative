package com.example.trial_map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

//factory class for Events
public class EventsFactory extends AsyncTask<String, String, String>
{
	// Progress Dialog
	private ProgressDialog													pDialog;
	private Context																				context;

	// Creating JSON Parser object
	JSONParser																									jParser									= new JSONParser();

	ArrayList<HashMap<String, String>>	productsList;

	// url to get all products list
	private static String														url_all_events		= "http://localhost/android_connect/get_all_events.php";

	// JSON Node names
	private static final String								TAG_SUCCESS					= "success";
	private static final String								TAG_EVENTS						= "events";
	private static final String								TAG_OWNERID					= "owner_id";
	private static final String								TAG_DESCRIPTION	= "description";
	private static final String								TAG_LOCATION				= "location";
	private static final String								TAG_TIME								= "time";
	private static final String								TAG_DATE								= "date";

	// products JSONArray
	JSONArray																										events										= null;

	public EventsFactory(Context aContext)
	{
		context = aContext;
	}

	public boolean saveNewEvent(Event anEvent)
	{
		return false;
	}

	public Event[] getEventsIn10KmRadius()
	{
		return null;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Loading events. Please wait...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected String doInBackground(String... args)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_all_events, "GET", params);

		// Check your log cat for JSON reponse
		Log.d("All Events: ", json.toString());

		try
		{
			// Checking for SUCCESS TAG
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1)
			{
				// events found
				// Getting Array of Events
				events = json.getJSONArray(TAG_EVENTS);

				// looping through All events
				for (int i = 0; i < events.length(); i++)
				{
					JSONObject c = events.getJSONObject(i);

					// Storing each json item in variable
					String owner_id = c.getString(TAG_OWNERID);
					String description = c.getString(TAG_DESCRIPTION);
					String location = c.getString(TAG_LOCATION);
					String time = c.getString(TAG_TIME);
					String date = c.getString(TAG_DATE);

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(TAG_OWNERID, owner_id);
					map.put(TAG_DESCRIPTION, description);
					map.put(TAG_LOCATION, location);
					map.put(TAG_TIME, time);
					map.put(TAG_DATE, date);

					// adding HashList to ArrayList
					productsList.add(map);
				}
			}
			else
			{
				// no products found
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return null;

	}

	@Override
	protected void onPostExecute(String file_url)
	{
		// dismiss the dialog after getting all products
		pDialog.dismiss();
	}
	
}
