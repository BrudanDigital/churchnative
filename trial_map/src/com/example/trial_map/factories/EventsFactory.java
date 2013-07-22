package com.example.trial_map.factories;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.trial_map.beans.Event;
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
	private static final String	TAG_LOCATION				= "location";
	// url to php script at website
	private static final String	PHP_SCRIPT_ADDRESS	= "http://192.168.43.169/android_connect/";
	private static final String	CREATE_EVENT_URL		= PHP_SCRIPT_ADDRESS + "create_event.php";
	private static final String	GET_EVENTS_URL			= PHP_SCRIPT_ADDRESS + "get_events.php";
	private static final String	DELETE_EVENT_URL		= PHP_SCRIPT_ADDRESS + "delete_event.php";
	private static final String	UPDATE_EVENT_URL		= PHP_SCRIPT_ADDRESS + "update_event.php";
	// integer FLAGS
	public static final int			SUCCESS							= 1;
	public static final int			FAILURE							= 0;
	public static final int			NO_CONNECTION				= 2;

	public static Integer SaveEvent(Event anEvent)
	{
		// get parameters
		String location = anEvent.getEvent_location_in_words();
		String latitude = "" + anEvent.getLatitude();
		String longitude = "" + anEvent.getLongitude();
		String time = anEvent.getTime();
		String date = anEvent.getDate();
		String name = anEvent.getName_of_event();
		String duration = anEvent.getDuration();
		String description = anEvent.getDescription_of_event();
		String user_id = "" + anEvent.getUser_id();
		String type_of_event = anEvent.getType_of_event();
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("location", location));
		params.add(new BasicNameValuePair("latitude", latitude));
		params.add(new BasicNameValuePair("longitude", longitude));
		params.add(new BasicNameValuePair("time", time));
		params.add(new BasicNameValuePair("date", date));
		params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("duration", duration));
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("type", type_of_event));
		// storing events
		JSONObject json = jParser.makeHttpRequest(CREATE_EVENT_URL, "POST", params);
		Log.e("SAVE EVENT", "" + json.toString());
		if (json != null)
		{

			try
			{
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					return SUCCESS;
				}
				else
				{
					return FAILURE;
				}
			}
			catch (JSONException e)
			{
				Log.e("SAVE EVENT", "" + e.getMessage());
			}
		}
		return NO_CONNECTION;

	}

	//returns all events within 10km of users location
	public static ArrayList<Event> getEventsIn10KmRadius(LatLng user_latLng)
	{
		try
		{

			JSONParser jsonParser = new JSONParser();

			JSONObject jsonObject = jsonParser.makeHttpGetRequest(GET_EVENTS_URL);
			;

			// Checking for SUCCESS TAG
			int success = jsonObject.getInt(TAG_SUCCESS);

			if (success == 1)
			{
				ArrayList<Event> all_events_10km_radius = new ArrayList<Event>();
				// Getting Array of events
				JSONArray events_array = jsonObject.getJSONArray(TAG_EVENTS);

				// looping through All Events returned and storing each separately
				for (int i = 0; i < events_array.length(); i++)
				{
					JSONObject events = events_array.getJSONObject(i);
					// Storing each json item in variable
					double latitude = events.getDouble(TAG_LATITUDE);
					Log.e("latitude", "" + latitude);
					double longitude = events.getDouble(TAG_LONGITUDE);
					Log.e("longitude", "" + longitude);
					LatLng location = new LatLng(latitude, longitude);
					//only add event if its with in 10km of user location
					if (distanceBtnIsLessThan10km(location, user_latLng))
					{
						String time = events.getString(TAG_TIME);
						Log.e("time", "" + time);
						String date = events.getString(TAG_DATE);
						Log.e("date", "" + date);
						String description = events.getString(TAG_DESCRIPTION);
						Log.e("description", "" + description);
						String name = events.getString(TAG_NAME);
						Log.e("name", "" + name);
						String duration = events.getString(TAG_DURATION);
						Log.e("duration", "" + duration);
						String event_location_in_words = events.getString(TAG_LOCATION);
						Log.e("location", "" + event_location_in_words);
						int user_id = events.getInt("user_id");
						int event_id = events.getInt("event_id");
						String type_of_event = events.getString("type");
						Event anEvent = new Event(latitude, longitude, time, date, description, name, duration, event_location_in_words, user_id, event_id, type_of_event);
						all_events_10km_radius.add(anEvent);
					}
				}
				return all_events_10km_radius;
			}
			else
			{// success is 0 and no results
				Log.e("SUCCESS", "is 0");
				return null;
			}

		}
		catch (Exception e)
		{
			Log.e("JSON error", "" + e.getMessage());
			return null;
		}

	}

	//deletes an event at the server
	public static int DeleteEvent(Event event)
	{
		String event_id = "" + event.getEvent_id();
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", event_id));

		JSONObject json = jParser.makeHttpRequest(DELETE_EVENT_URL, "POST", params);
		Log.e("DELETE EVENT", "" + json.toString());

		try
		{
			// Checking for SUCCESS TAG
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1)
			{
				return SUCCESS;
			}
			else
			{
				return FAILURE;
			}
		}
		catch (Exception e)
		{
			Log.e("DELETE EVENT", "" + e.getMessage());
		}

		return NO_CONNECTION;
	}

	//updates an event at the server side
	public static Integer UpdateEvent(Event anEvent)
	{
		// get parameters
		String location = anEvent.getEvent_location_in_words();
		String latitude = "" + anEvent.getLatitude();
		String longitude = "" + anEvent.getLongitude();
		String time = anEvent.getTime();
		String date = anEvent.getDate();
		String name = anEvent.getName_of_event();
		String duration = anEvent.getDuration();
		String description = anEvent.getDescription_of_event();
		String user_id = "" + anEvent.getUser_id();
		String type_of_event = anEvent.getType_of_event();
		String id = "" + anEvent.getEvent_id();
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("location", location));
		params.add(new BasicNameValuePair("latitude", latitude));
		params.add(new BasicNameValuePair("longitude", longitude));
		params.add(new BasicNameValuePair("time", time));
		params.add(new BasicNameValuePair("date", date));
		params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("duration", duration));
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("type", type_of_event));
		params.add(new BasicNameValuePair("id", id));
		// storing events
		JSONObject json = jParser.makeHttpRequest(UPDATE_EVENT_URL, "POST", params);
		Log.e("UPDATE EVENT", "" + json.toString());
		if (json != null)
		{

			try
			{
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					return SUCCESS;
				}
				else
				{
					return FAILURE;
				}
			}
			catch (JSONException e)
			{
				Log.e("UPDATE EVENT", "" + e.getMessage());
			}
		}
		return NO_CONNECTION;
	}

	//checks if the any 2 points are within 10km of each other
	private static boolean distanceBtnIsLessThan10km(LatLng latLng1, LatLng latLng2)
	{
		Location locationA = new Location("point A");

		locationA.setLatitude(latLng1.latitude);
		locationA.setLongitude(latLng1.longitude);

		Location locationB = new Location("point B");

		locationB.setLatitude(latLng2.latitude);
		locationB.setLongitude(latLng2.longitude);

		float distance = locationA.distanceTo(locationB);
		System.out.print("DISTANCE=" + distance);
		if (distance <= 10000)
		{
			return true;
		}
		return false;
	}
}
