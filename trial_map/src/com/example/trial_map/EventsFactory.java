package com.example.trial_map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;

//factory class for Events

public class EventsFactory
{
	// Progress Dialog
	private ProgressDialog							pDialog;
	Context															aContext;

	// Creating JSON Parser object
	static JSONParser										jParser						= new JSONParser();

	ArrayList<HashMap<String, String>>	productsList;

	// JSON Node names
	private static final String					TAG_SUCCESS				= "success";
	private static final String					TAG_EVENTS				= "events";
	private static final String					TAG_OWNERID				= "owner_id";
	private static final String					TAG_DESCRIPTION		= "description";
	private static final String					TAG_LOCATION			= "location";
	private static final String					TAG_TIME					= "time";
	private static final String					TAG_MSG						= "message";
	private static final String					TAG_DATE					= "date";
	private static final String					ADDRESS						= "http://192.168.43.169/android_connect/";
	private static final String					CREATE_EVENT_URL	= "http://192.168.43.169/android_connect/create_event.php";

	// products JSONArray
	JSONArray														events						= null;

	public static String SaveEvent(Context aContext, Event anEvent)
	{
		String location = anEvent.getLocation_of_event();
		String time = anEvent.getTime();
		String date = anEvent.getDate();
		String description = anEvent.getDescription_of_event();
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("location", location));
		params.add(new BasicNameValuePair("time", time));
		params.add(new BasicNameValuePair("date", date));
		params.add(new BasicNameValuePair("description", description));

		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(CREATE_EVENT_URL, "POST", params);

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
		return null;

	}

	protected String doInBackground()
	{
		return null;
	}

	public Event[] getEventsIn10KmRadius()
	{
		return null;
	}

}
