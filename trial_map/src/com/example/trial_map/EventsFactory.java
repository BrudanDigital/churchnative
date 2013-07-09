package com.example.trial_map;

<<<<<<< HEAD
//factory class for Events
public class EventsFactory
{
	public boolean saveNewEvent(Event anEvent)
	{
		return false;
	}
	
		public Event[] getEventsIn10KmRadius()
		{
			return null;
		}
=======
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

//factory class for Events

public class EventsFactory
{
	// a context object
	Context											aContext;

	// Creating JSON Parser object
	static JSONParser						jParser							= new JSONParser();

	// JSON Node names
	private static final String	TAG_SUCCESS					= "success";
	private static final String	TAG_EVENTS					= "events";
	private static final String	TAG_DESCRIPTION			= "description";
	private static final String	TAG_LATITUDE				= "latitude";
	private static final String	TAG_TIME						= "time";
	private static final String	TAG_LONGITUDE				= "longitude";
	private static final String	TAG_DATE						= "date";
	private static final String	TAG_NAME						= "name";
	private static final String	TAG_DURATION				= "duration";
	//url to php script at website
	private static final String	PHP_SCRIPT_ADDRESS	= "http://192.168.43.169/android_connect/";
	private static final String	CREATE_EVENT_URL		= PHP_SCRIPT_ADDRESS
																											+ "create_event.php";
	private static final String	GET_EVENTS_URL			= PHP_SCRIPT_ADDRESS
																											+ "get_events.php";

	public static String SaveEvent(Context aContext, Event anEvent)
	{
		// get parameters
		String latitude = "" + anEvent.getLatitude();
		String longitude = "" + anEvent.getLongitude();
		String time = anEvent.getTime();
		String date = anEvent.getDate();
		String name = anEvent.getName_of_event();
		String duration = anEvent.getDuration();
		String description = anEvent.getDescription_of_event();
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("latitude", latitude));
		params.add(new BasicNameValuePair("longitude", longitude));
		params.add(new BasicNameValuePair("time", time));
		params.add(new BasicNameValuePair("date", date));
		params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("duration", duration));

		// storing events
		JSONObject json = jParser.makeHttpRequest(CREATE_EVENT_URL, "POST", params);

		if (json != null)
		{

			try
			{
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					return "Event created successfully";
				}
				else
				{
					return "failed to create event";

				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		return "no connection to server";

	}

	public static ArrayList<Event> getEventsIn10KmRadius()
	{
		ArrayList<Event> all_events_10km_radius = new ArrayList<Event>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = jsonParser.makeHttpGetRequest(GET_EVENTS_URL);
		try
		{
			// Checking for SUCCESS TAG
			int success = jsonObject.getInt(TAG_SUCCESS);

			if (success == 1)
			{
				// Getting Array of events
				JSONArray events_array = jsonObject.getJSONArray(TAG_EVENTS);

				// looping through All Contacts
				for (int i = 0; i < events_array.length(); i++)
				{
					JSONObject events = events_array.getJSONObject(i);
					// Storing each json item in variable
					double latitude = events.getDouble(TAG_LATITUDE);
					Log.e("latitude", "" + latitude);
					double longitude = events.getDouble(TAG_LONGITUDE);
					Log.e("longitude", "" + longitude);
					String time = events.getString(TAG_TIME);
					Log.e("time", "" + time);
					String date = events.getString(TAG_DATE);
					Log.e("date", "" + date);
					String description = events.getString(TAG_DESCRIPTION);
					Log.e("description", "" + description);
					String name = events.getString(TAG_NAME);
					String duration = events.getString(TAG_DURATION);
					LatLng location = new LatLng(latitude, longitude);
					Event anEvent = new Event(location, time, date, description, name,
							duration);
					all_events_10km_radius.add(anEvent);
				}
			}
			else
			{// success is 0 and no results
				return null;
			}
			return all_events_10km_radius;
		}
		catch (JSONException e)
		{
			Log.e("JSON error", "" + e.getMessage());
		}
		return null;
	}

>>>>>>> trial
}
