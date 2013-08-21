package com.example.trial_map.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.example.trial_map.beans.Event;
import com.example.trial_map.util.Validator;
import com.google.android.gms.maps.model.LatLng;

/** factory class for Events **/
public class EventManager extends Manager
{
	/** a context object **/
	Context											aContext;

	

	/** JSON Node names **/
	private static final String	TAG_EVENTS										= "events";
	private static final String	TAG_DESCRIPTION								= "description";
	private static final String	TAG_LATITUDE									= "latitude";
	private static final String	TAG_TIME											= "time";
	private static final String	TAG_LONGITUDE									= "longitude";
	private static final String	TAG_DATE											= "date";
	private static final String	TAG_NAME											= "name";
	private static final String	TAG_DURATION									= "duration";
	private static final String	TAG_LOCATION									= "location";
	private static final String	TAG_TOTAL											= "total";
	private static final String	TAG_HEARD_OF_STATUS						= "heard_of_event";
	// url to php script at website
	private static final String	CREATE_EVENT_URL							= PHP_SCRIPT_ADDRESS + "create_event.php";
	private static final String	GET_EVENTS_URL								= PHP_SCRIPT_ADDRESS + "get_events.php";
	private static final String	DELETE_EVENT_URL							= PHP_SCRIPT_ADDRESS + "delete_event.php";
	private static final String	UPDATE_EVENT_URL							= PHP_SCRIPT_ADDRESS + "update_event.php";
	private static final String	SAVE_HEARD_OF_STATUS_URL			= PHP_SCRIPT_ADDRESS + "save_heard_of_event_status.php";
	public static final String	INVALID_DATE_TEXT							= "THE SUBMITTED DATE HAS ALREADY PASSED!!";
	// integer FLAGS
	public static final int			DATE_ERROR										= 4;
	public static final int			EMPTY_FIELD_ERROR							= 5;

	

	/** saves an event on the server side **/
	public static int SaveEvent(Event anEvent)
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
		Boolean heard_of_event = anEvent.getHeard_of_this_event_status();
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
		params.add(new BasicNameValuePair("heard_of_event", "" + heard_of_event));
		// storing events
		JSONObject json = NetworkManager.makeHttpPostRequest(CREATE_EVENT_URL,  params);
		if (json != null)
		{

			try
			{
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					MESSAGE = json.getString(TAG_MESSAGE);
					return SUCCESS;
				}
				else
				{
					return FAILURE;
				}
			}
			catch (JSONException e)
			{

			}
		}
		return NO_CONNECTION;

	}


	/** returns all events within 10km of users location **/
	public static ArrayList<Event> getEventsIn10KmRadius(LatLng user_location)
	{
		try
		{
			
			JSONObject jsonObject = NetworkManager.makeHttpGetRequest(GET_EVENTS_URL);

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

					double longitude = events.getDouble(TAG_LONGITUDE);

					LatLng location = new LatLng(latitude, longitude);

					// check that these values are set
					if (user_location != null && user_location.latitude != 0 && user_location.longitude != 0)
					{
						// only add event if its with in 10km of user location
						if (distanceBtnIsLessThan10km(location, user_location))
						{
							addToArrayList(all_events_10km_radius, events);
						}

					}
					else
					{// by default just add every event to the arraylist
						addToArrayList(all_events_10km_radius, events);
					}
					MESSAGE = jsonObject.getString(TAG_MESSAGE);
				}
				return all_events_10km_radius;
			}
			else
			{// success is 0 and no results
				// Log.e("SUCESS", "No Tag Found");
				return null;
			}

		}
		catch (Exception e)
		{
			
			return null;
		}

	}


	private static void addToArrayList(ArrayList<Event> all_events_10km_radius, JSONObject events)
	{
		try
		{
			double latitude = events.getDouble(TAG_LATITUDE);

			double longitude = events.getDouble(TAG_LONGITUDE);

			String time = events.getString(TAG_TIME);

			String date = events.getString(TAG_DATE);

			String description = events.getString(TAG_DESCRIPTION);

			String name = events.getString(TAG_NAME);

			String duration = events.getString(TAG_DURATION);

			String event_location_in_words = events.getString(TAG_LOCATION);

			boolean heard_of_event = events.getBoolean(TAG_HEARD_OF_STATUS);
			//Log.e("HEARD OF EVENT", ""+event_location_in_words);
			int total_people_who_have_heard = events.getInt(TAG_TOTAL);
			
			int user_id = events.getInt("user_id");
			int event_id = events.getInt("event_id");
			String type_of_event = events.getString("type");
			Event anEvent = new Event(latitude, longitude, time, date, description, name, duration, event_location_in_words, user_id, event_id, type_of_event, heard_of_event, total_people_who_have_heard);
			all_events_10km_radius.add(anEvent);
		}
		catch (JSONException e)
		{

		}
	}


	/** deletes an event at the server **/
	public static int DeleteEvent(Event event)
	{
		String event_id = "" + event.getEvent_id();
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", event_id));

		JSONObject json = NetworkManager.makeHttpPostRequest(DELETE_EVENT_URL, params);
		// Log.e("DELETE EVENT", "" + json.toString());

		try
		{
			// Checking for SUCCESS TAG
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1)
			{
				MESSAGE = json.getString(TAG_MESSAGE);
				return SUCCESS;
			}
			else
			{
				return FAILURE;
			}
		}
		catch (Exception e)
		{
		}

		return NO_CONNECTION;
	}


	/** updates an event at the server side **/
	public static int UpdateEvent(Event anEvent)
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
		String heard_of_event = "" + anEvent.getHeard_of_this_event_status();
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
		params.add(new BasicNameValuePair("heard_of_event", heard_of_event));
		// storing events
		JSONObject json = NetworkManager.makeHttpPostRequest(UPDATE_EVENT_URL, params);
		// Log.e("UPDATE EVENT", "" + json.toString());
		if (json != null)
		{

			try
			{
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					MESSAGE = json.getString(TAG_MESSAGE);
					return SUCCESS;
				}
				else
				{
					return FAILURE;
				}
			}
			catch (JSONException e)
			{
			}
		}
		return NO_CONNECTION;
	}


	/** checks if the any 2 points are within 10km of each other **/
	private static boolean distanceBtnIsLessThan10km(LatLng latLng1, LatLng latLng2)
	{
		if (latLng1 == null || latLng2 == null)
		{
			throw new IllegalArgumentException("both latlng objects must not be null");
		}
		Location locationA = new Location("point A");

		locationA.setLatitude(latLng1.latitude);
		locationA.setLongitude(latLng1.longitude);

		Location locationB = new Location("point B");

		locationB.setLatitude(latLng2.latitude);
		locationB.setLongitude(latLng2.longitude);

		float distance = locationA.distanceTo(locationB);
		if (distance <= 10000)
		{
			return true;
		}
		return false;
	}


	/** checks if an event is valid and can be saved **/
	public static int EventIsValid(Event anEvent)
	{
		// check if the location in words is valid
		String event_location_in_words = anEvent.getEvent_location_in_words();
		if (Validator.isNullOrEmpty(event_location_in_words))
		{
			return EMPTY_FIELD_ERROR;
		}

		// check if the location (latlng) is valid
		LatLng event_location = anEvent.getLocation_of_event();
		if (Validator.isNullOrEmpty(event_location))
		{
			return EMPTY_FIELD_ERROR;
		}

		// check if the location in words is valid
		String event_name = anEvent.getName_of_event();
		if (Validator.isNullOrEmpty(event_name))
		{
			return EMPTY_FIELD_ERROR;
		}

		// check if the latitude is valid
		double latitude = anEvent.getLatitude();
		if (latitude <= 0)
		{
			return EMPTY_FIELD_ERROR;
		}

		// check if the longitude is valid
		double longitude = anEvent.getLongitude();
		if (longitude <= 0)
		{
			return EMPTY_FIELD_ERROR;
		}

		// check if the start_time is valid
		String start_time = anEvent.getTime();
		if (Validator.isNullOrEmpty(start_time))
		{
			return EMPTY_FIELD_ERROR;
		}

		// check if the date is valid
		String date = anEvent.getDate();
		if (Validator.isNullOrEmpty(date))
		{
			return EMPTY_FIELD_ERROR;
		}
		if (!Validator.isDateOkay(date))
		{
			return DATE_ERROR;
		}

		// check if the event description is valid
		String event_description = anEvent.getDescription_of_event();
		if (Validator.isNullOrEmpty(event_description))
		{
			return EMPTY_FIELD_ERROR;
		}

		// check if the duration of the event is valid
		String event_duration = anEvent.getDuration();
		if (Validator.isNullOrEmpty(event_duration))
		{
			return EMPTY_FIELD_ERROR;
		}

		// check if the type of event is valid
		String type_of_event = anEvent.getType_of_event();
		if (Validator.isNullOrEmpty(type_of_event))
		{
			return EMPTY_FIELD_ERROR;
		}

		// check if the user_id is valid
		int user_id = anEvent.getUser_id();
		if (user_id < -1)
		{
			return EMPTY_FIELD_ERROR;
		}

		// check if the event_id is valid
		int event_id = anEvent.getEvent_id();
		if (event_id < -1)
		{
			return EMPTY_FIELD_ERROR;
		}
		return SUCCESS;
	}


	public static boolean saveHeardOfStatus(int event_id)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", "" + event_id));
		// storing events
		JSONObject json = NetworkManager.makeHttpPostRequest(SAVE_HEARD_OF_STATUS_URL, params);
		if (json != null)
		{

			try
			{
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					MESSAGE = json.getString(TAG_MESSAGE);
					return true;
				}
				return false;
			}
			catch (JSONException e)
			{
				return false;
			}
		}
		return false;
	}



}
