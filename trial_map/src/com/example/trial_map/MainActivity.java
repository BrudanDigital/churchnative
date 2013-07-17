package com.example.trial_map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Dialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trial_map.beans.Event;
import com.example.trial_map.beans.EventOwner;
import com.example.trial_map.factories.EventsFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements LocationListener
{
	// constants
	private static final int					NEW_EVENT_ACTIVITY_RESULT_CODE		= 100;
	private static final int					DETAILS_ACTIVITY_RESULT_CODE			= 200;
	private static final int					LIST_EVENTS_ACTIVITY_RESULT_CODE	= 300;
	private static final int					LOGIN_ACTIVITY_RESULT_CODE				= 400;
	private static final int					GOOGLE_MAP_DEFAULT_ZOOM_LEVEL			= 13;
	private static final int					HOME_SCREEN												= R.layout.activity_main;
	private static final int					SHORT_DURATION										= Toast.LENGTH_SHORT;
	private static final int					LONG_DURATION											= Toast.LENGTH_LONG;
	private static final String				LOGIN_BUTTON_TEXT									= "Login";
	private static final String				LOGGED_OUT_BUTTON_TEXT						= "Log Out";
	private static final String				LOG_OUT_SUCCESS_TEXT							= "You Have Been Logged Out";
	private static final String				LOG_IN_SUCCESS_TEXT								= "Logged In as:";
	private static final String				EVENT_CREATED_TEXT								= "Event Created Successfully";
	private static final String				ILLEGAL_PARAMETER_TEXT						= "Parameters cannot be null";
	private static final CharSequence	FAILED_TO_GET_EVENTS_TEXT					= "Failed To Retrieve Any Events";
	private static final String				GOOGLE_MARKER_SNIPPET_TEXT				= "On:";
	// variables
	private ArrayList<Event>					eventsArrayList										= null;
	private HashMap<String, Event>		eventMarkerMap										= new HashMap<String, Event>();
	private GoogleMap									googleMap;
	private Location									location;
	private EventOwner								anEventOwner											= null;
	private Menu											menu															= null;

	// FIXME app should check for net b4 starting
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(HOME_SCREEN);

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS)
		{ // Google Play Services are not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();

		}
		else
		{ // Google Play Services are available

			// Getting reference to the SupportMapFragment of activity_main.xml
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

			// Getting GoogleMap object from the fragment
			googleMap = fm.getMap();

			// Enabling MyLocation Layer of Google Map
			googleMap.setMyLocationEnabled(true);

			// Getting LocationManager object from System Service
			// LOCATION_SERVICE
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// Creating a criteria object to retrieve provider
			Criteria criteria = new Criteria();

			// Getting the name of the best provider
			String provider = locationManager.getBestProvider(criteria, true);

			// Getting Current Location
			location = locationManager.getLastKnownLocation(provider);

			if (location != null)
			{
				onLocationChanged(location);

			}

			locationManager.requestLocationUpdates(provider, 20000, 0, this);
		}

	}

	protected boolean isRouteDisplayed()
	{
		return false;
	}

	@Override
	public void onLocationChanged(Location arg0)
	{
		// FIXME make this an asynchronous task coz ui freezes wen fetching data
		if (arg0 != null)
		{
			// get text view
			TextView tvLocation = (TextView) findViewById(R.id.tv_location);

			// Getting latitude of the current location
			double latitude = location.getLatitude();

			// Getting longitude of the current location
			double longitude = location.getLongitude();

			// Creating a LatLng object for the current location
			LatLng latLng = new LatLng(latitude, longitude);

			// googleMap.addMarker(new MarkerOptions().position(latLng).title(title));

			// Showing the current location in Google Map
			googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

			// Zoom in the Google Map
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(GOOGLE_MAP_DEFAULT_ZOOM_LEVEL));

			// display events using markers
			displayEventsIn10kmRadius();

			// add listener for clicks on info window that pops up when user clicks on
			// marker
			googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
			{

				@Override
				public void onInfoWindowClick(Marker marker)
				{

					// Code To display some more info about marker
					if (eventsArrayList != null)
					{// if the events array list is not empty
						// get the event linked to marker
						Event anEvent = getEventAssociatedWithMarker(marker);
						if (anEvent == null)
						{// if no event is found return now
							Log.e("INFO CLICK", "event is null");
							return;
						}
						// create a new sub activity
						Intent intent = new Intent(MainActivity.this, EventDetailsActivity.class);
						// use helper method to send object to next activity
						sendEvent(intent, anEvent);
						// start new sub activity
						startActivityForResult(intent, DETAILS_ACTIVITY_RESULT_CODE);

					}

				}

				private void sendEvent(Intent intent, Event anEvent)
				{
					Double latitude = anEvent.getLatitude();
					Log.e("sendEvent", "" + latitude);
					Double longitude = anEvent.getLongitude();
					Log.e("sendEvent", "" + longitude);
					String time = anEvent.getTime();
					Log.e("sendEvent", "" + time);
					String date = anEvent.getDate();
					Log.e("sendEvent", "" + date);
					String description = anEvent.getDescription_of_event();
					Log.e("sendEvent", "" + description);
					String name = anEvent.getName_of_event();
					Log.e("sendEvent", "" + name);
					String duration = anEvent.getDuration();
					String location_in_words = anEvent.getEvent_location_in_words();
					int user_id=anEvent.getUser_id();
					int event_id=anEvent.getEvent_id();
					String type=anEvent.getType_of_event();
					Bundle extras = new Bundle();
					extras.putDouble("latitude", latitude);
					extras.putDouble("longitude", longitude);
					extras.putString("time", time);
					extras.putString("date", date);
					extras.putString("description", description);
					extras.putString("name", name);
					extras.putString("duration", duration);
					extras.putString("location_in_words", location_in_words);
					extras.putInt("user_id", user_id);
					extras.putInt("event_id", event_id);
					extras.putString("type", type);
					intent.putExtras(extras);
				}

				private Event getEventAssociatedWithMarker(Marker marker)
				{
					// displayEventsIn10kmRadius();
					// if hash map is not empty
					if (eventMarkerMap != null)
					{
						// look up desired event from hash map
						String marker_id = marker.getId();
						Log.e("MARKER_ID", "" + marker_id);
						Event anEvent = eventMarkerMap.get(marker_id);
						Log.e("MARKER_ID", "" + anEvent);
						// return event
						return anEvent;
					}
					else
					{
						Log.e("HASHMAP", "hash map is null");
					}
					return null;

				}
			});

			// Setting latitude and longitude in the TextView tv_location
			tvLocation.setText("Latitude:" + latitude + ", Longitude:" + longitude);
		}
	}

	@Override
	public void onProviderDisabled(String arg0)
	{

	}

	@Override
	public void onProviderEnabled(String arg0)
	{

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{

	}

	// method called to create menu and its items
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// add menu options to the UI
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu, menu);
		// disable add event menu item
		MenuItem item = menu.findItem(R.id.menu_addEvent);
		item.setEnabled(false);
		// make menu global
		this.menu = menu;
		return true;
	}

	// handler for click on menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem menu_item)
	{
		final int login = R.id.menu_login;
		final int list_events = R.id.menu_listEvents;
		final int add_an_event = R.id.menu_addEvent;

		switch (menu_item.getItemId())
		{
			case login:
				String title = menu_item.getTitle().toString();
				if (title.equalsIgnoreCase(LOGIN_BUTTON_TEXT) && anEventOwner == null)
				{//user wants to login
					goToLoginScreen();
				}
				else
				{// user wants to logout
					anEventOwner = null;
					//make button say 'Login'
					menu_item.setTitle(LOGIN_BUTTON_TEXT);
					//display message
					Toast.makeText(MainActivity.this, LOG_OUT_SUCCESS_TEXT, SHORT_DURATION).show();
					//disable add event menu item
					MenuItem addEvent=menu.findItem(add_an_event);
					addEvent.setEnabled(false);
				}
				return true;
			case list_events:
				goToListEventsScreen();
				return true;
			case add_an_event:
				goToNewEventScreen();
				return true;

		}
		return super.onOptionsItemSelected(menu_item);

	}

	// creates new subactivity for logging in
	// start new event sub activity in order to for user to login
	private void goToLoginScreen()
	{

		Intent loginScreen = new Intent(MainActivity.this, LoginActivity.class);
		startActivityForResult(loginScreen, LOGIN_ACTIVITY_RESULT_CODE);
	}

	// creates new subactivity to list Events
	// start new event sub activity in order to list all events in 10km
	private void goToListEventsScreen()
	{
		// first check if there are any events
		if (EventsFactory.getEventsIn10KmRadius() != null)
		{
			// start subactivity to list events
			Intent listEventsScreen = new Intent(MainActivity.this, ListEventsActivity.class);
			startActivityForResult(listEventsScreen, LIST_EVENTS_ACTIVITY_RESULT_CODE);
		}
		else
		{// if none then inform user
			Toast.makeText(this, FAILED_TO_GET_EVENTS_TEXT, Toast.LENGTH_LONG).show();
		}

	}

	// creates new subactivity to create new event
	// start new event sub activity in order to create new event
	private void goToNewEventScreen()
	{
		
		if (anEventOwner==null)
		{//if he is not logged in [probably will never reach here but just to be safe]
			return;
		}
		Intent newEventScreen = new Intent(MainActivity.this, NewEventActivity.class);
		Bundle aBundle=new Bundle();
		aBundle.putInt("user_id", anEventOwner.getUser_id());
		newEventScreen.putExtras(aBundle);
		startActivityForResult(newEventScreen, NEW_EVENT_ACTIVITY_RESULT_CODE);
	}

	// FIXME make this method actually return events in 10km
	// gets and displays markers of events in 10km
	// gets and displays events within a 10km radius from were user is
	private void displayEventsIn10kmRadius()
	{
		try
		{// try to get events
			eventsArrayList = EventsFactory.getEventsIn10KmRadius();
			// if u fail to query database for events then return
			Iterator<Event> iterator = eventsArrayList.iterator();
			// create new hash map
			eventMarkerMap = new HashMap<String, Event>();
			// remove all markers from google map
			googleMap.clear();
			// iterate thru storing events
			while (iterator.hasNext())
			{
				Event anEvent = iterator.next();
				LatLng location = anEvent.getLocation_of_event();
				String title = anEvent.getDescription_of_event().toUpperCase();
				String snippet = GOOGLE_MARKER_SNIPPET_TEXT + anEvent.getDate();
				Marker aMarker = googleMap.addMarker(new MarkerOptions().position(location).title(title).snippet(snippet));
				// only store event if its not already stored
				String marker_id = aMarker.getId();
				if (!eventMarkerMap.containsKey(marker_id))
				{
					eventMarkerMap.put(marker_id, anEvent);
					// check to confirm that its stored
					if (eventMarkerMap.containsKey(marker_id))
					{
						Log.e("HASH TABLE", "stored event");
					}
				}
			}
		}
		catch (Exception e)
		{
			String text = "Failed To Retrieve Any Events";
			Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
		}
	}

	// FIXME add variable type_of_event to even
	// FIXME validation of data before submitting it in all activities
	// handle results returned by sub activities
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		// return form NewEventActivity
			case NEW_EVENT_ACTIVITY_RESULT_CODE:
				if (resultCode == RESULT_OK)
				{
					Toast.makeText(this, EVENT_CREATED_TEXT, LONG_DURATION).show();
				}
				break;
			// return from EventDetailsActivity
			case DETAILS_ACTIVITY_RESULT_CODE:
				break;
			// return from ListEventsActivity
			case LIST_EVENTS_ACTIVITY_RESULT_CODE:
				break;
			// return from loginActivity
			case LOGIN_ACTIVITY_RESULT_CODE:
				if (resultCode == RESULT_OK)
				{//user has successfully logged in
					// get returned eventOwner Object
					getEventOwner(data);
					// enable add event menu item
					MenuItem add_event = menu.findItem(R.id.menu_addEvent);
					add_event.setEnabled(true);
					// change title of login button
					MenuItem login = menu.findItem(R.id.menu_login);
					//make the button say 'Log Out'
					login.setTitle(LOGGED_OUT_BUTTON_TEXT);
					// inform user
					Toast.makeText(MainActivity.this, LOG_IN_SUCCESS_TEXT + anEventOwner.getEmail(), Toast.LENGTH_LONG).show();
				}
			default:
				break;
		}

		displayEventsIn10kmRadius();
	}

	// retrieves an event owner object
	private void getEventOwner(Intent data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException(ILLEGAL_PARAMETER_TEXT);
		}
		Bundle aBundle = data.getExtras();
		int user_id = aBundle.getInt("user_id");
		String email = aBundle.getString("email");
		String password = aBundle.getString("password");
		String name = aBundle.getString("company_name");
		String description = aBundle.getString("description");
		String location = aBundle.getString("location");
		anEventOwner = new EventOwner(user_id, email, password, name, location, description);
		Log.e("BUNDLE", "eventOwner recieved");

	}
}
