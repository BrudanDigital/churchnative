package com.example.trial_map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.R.integer;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.trial_map.beans.Event;
import com.example.trial_map.beans.EventOwner;
import com.example.trial_map.factories.EventsFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends SherlockFragmentActivity implements LocationListener
{
	// constants
	private static final int					NEW_EVENT_ACTIVITY_RESULT_CODE		= 100;
	private static final int					DETAILS_ACTIVITY_RESULT_CODE			= 200;
	private static final int					LIST_EVENTS_ACTIVITY_RESULT_CODE	= 300;
	private static final int					LOGIN_ACTIVITY_RESULT_CODE				= 400;
	private static final int					GOOGLE_MAP_DEFAULT_ZOOM_LEVEL			= 13;
	private static final int					HOME_SCREEN												= R.layout.main_activity;
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
		LoadMapTask loadMapTask=new LoadMapTask();
		loadMapTask.execute();
	}

	protected boolean isRouteDisplayed()
	{
		return false;
	}

	@Override
	public void onLocationChanged(Location arg0)
	{
		GetLocationTask getLocationTask = new GetLocationTask();
		getLocationTask.execute(arg0);
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
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		// add menu options to the UI
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.layout.menu_main_activity, menu);
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
				{// user wants to login
					goToLoginScreen();
				}
				else
				{// user wants to logout
					anEventOwner = null;
					// make button say 'Login'
					menu_item.setTitle(LOGIN_BUTTON_TEXT);
					// display message
					Toast.makeText(MainActivity.this, LOG_OUT_SUCCESS_TEXT, SHORT_DURATION).show();
					// disable add event menu item
					MenuItem addEvent = menu.findItem(add_an_event);
					addEvent.setEnabled(false);
				}
				return true;
			case list_events:
				StartIntent startIntent = new StartIntent();
				startIntent.execute();
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

	// creates new subactivity to create new event
	// start new event sub activity in order to create new event
	private void goToNewEventScreen()
	{

		if (anEventOwner == null)
		{// if he is not logged in [probably will never reach here but just to be
			// safe]
			return;
		}
		Intent newEventScreen = new Intent(MainActivity.this, NewEventActivity.class);
		Bundle aBundle = new Bundle();
		aBundle.putInt("user_id", anEventOwner.getUser_id());
		newEventScreen.putExtras(aBundle);
		startActivityForResult(newEventScreen, NEW_EVENT_ACTIVITY_RESULT_CODE);
	}

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
				{// user has successfully logged in
					// get returned eventOwner Object
					getEventOwner(data);
					// enable add event menu item
					MenuItem add_event = menu.findItem(R.id.menu_addEvent);
					add_event.setEnabled(true);
					// change title of login button
					MenuItem login = menu.findItem(R.id.menu_login);
					// make the button say 'Log Out'
					login.setTitle(LOGGED_OUT_BUTTON_TEXT);
					// inform user
					Toast.makeText(MainActivity.this, LOG_IN_SUCCESS_TEXT + anEventOwner.getEmail(), Toast.LENGTH_LONG).show();
				}
			default:
				break;
		}

		// displayEventsIn10kmRadius(googleMap);
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

	// task to events in background thread
	private class StartIntent extends AsyncTask<String, integer, Void>
	{
		private final CharSequence	PROGRESS_DIALOG_TEXT	= "Loading.Please Wait...";
		private ProgressDialog			pDialog;

		@Override
		protected void onPreExecute()
		{
			// create progress dialog and display it to user
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage(PROGRESS_DIALOG_TEXT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... maps)
		{

			// first check if there are any events
			if (EventsFactory.getEventsIn10KmRadius() != null)
			{
				// start subactivity to list events
				Intent listEventsScreen = new Intent(MainActivity.this, ListEventsActivity.class);
				pDialog.dismiss();
				startActivityForResult(listEventsScreen, LIST_EVENTS_ACTIVITY_RESULT_CODE);
			}
			else
			{// if none then inform user
				Toast.makeText(MainActivity.this, FAILED_TO_GET_EVENTS_TEXT, Toast.LENGTH_LONG).show();
			}

			return null;
		}

	}

	//Loads Map In Background thread inorder to make app more responsive
	private class LoadMapTask extends AsyncTask<String, Integer, Location>
	{
		private final CharSequence	PROGRESS_DIALOG_TEXT	= "Loading Map...";
		private ProgressDialog			pDialog;
		LocationManager							locationManager;
		String											provider;

		@Override
		protected void onPreExecute()
		{
			// Getting Google Play availability status
			int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

			// Showing status
			if (status != ConnectionResult.SUCCESS)
			{ // Google Play Services are not available

				int requestCode = 10;
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, MainActivity.this, requestCode);
				dialog.show();
				finish();
			}
			else
			{ // Google Play Services are available

				// Getting reference to the SupportMapFragment of activity_main.xml
				SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
				// create progress dialog and display it to user
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setMessage(PROGRESS_DIALOG_TEXT);
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();

				// Getting GoogleMap object from the fragment
				googleMap = fm.getMap();

				// Enabling MyLocation Layer of Google Map
				googleMap.setMyLocationEnabled(true);

			}
		}

		@Override
		protected Location doInBackground(String... arg0)
		{
			// Getting LocationManager object from System Service
			// LOCATION_SERVICE
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// Creating a criteria object to retrieve provider
			Criteria criteria = new Criteria();

			// Getting the name of the best provider
			provider = locationManager.getBestProvider(criteria, true);

			// Getting Current Location
			location = locationManager.getLastKnownLocation(provider);
			return location;
		}

		@Override
		protected void onPostExecute(Location location)
		{
			pDialog.dismiss();
			if (location != null)
			{
				onLocationChanged(location);
			}

			locationManager.requestLocationUpdates(provider, 20000, 0, MainActivity.this);
		}
	}

	//handles location changes in background inorder to make app more responsive
	private class GetLocationTask extends AsyncTask<Location, Integer, LatLng>
	{
		@Override
		protected LatLng doInBackground(Location... arg0)
		{
			if (arg0 != null)
			{
				// Getting latitude of the current location
				double latitude = location.getLatitude();

				// Getting longitude of the current location
				double longitude = location.getLongitude();

				// Creating a LatLng object for the current location
				LatLng latLng = new LatLng(latitude, longitude);
				// try to get events
				eventsArrayList = EventsFactory.getEventsIn10KmRadius();
				return latLng;
			}
			return null;
		}

		@Override
		protected void onPostExecute(LatLng latLng)
		{
			if (latLng != null)
			{
				// Showing the current location in Google Map
				googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

				// Zoom in the Google Map
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(GOOGLE_MAP_DEFAULT_ZOOM_LEVEL));


				// add listener for clicks on info window that pops up when user clicks
				// on
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
						int user_id = anEvent.getUser_id();
						int event_id = anEvent.getEvent_id();
						String type = anEvent.getType_of_event();
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
				if (eventsArrayList != null)
				{
					
					// if u fail to query database for events then return
					Iterator<Event> iterator = eventsArrayList.iterator();
					eventMarkerMap = new HashMap<String, Event>();
					// remove all markers from google map
					googleMap.clear();
					// iterate thru storing events
					while (iterator.hasNext())
					{
						Event anEvent = iterator.next();
						String type=anEvent.getType_of_event();
						LatLng location = anEvent.getLocation_of_event();
						String title = anEvent.getDescription_of_event().toUpperCase();
						String snippet = GOOGLE_MARKER_SNIPPET_TEXT + anEvent.getDate();
						MarkerOptions myMarkerOptions=new MarkerOptions();
						myMarkerOptions.position(location);
						myMarkerOptions.title(title);
						myMarkerOptions.snippet(snippet);
						int resource_id=0;
						if (type.equalsIgnoreCase("meeting"))
						{
							resource_id=R.drawable.meeting;
						}
						else if (type.equalsIgnoreCase("party"))
						{
							resource_id=R.drawable.party;
						}
						else if (type.equalsIgnoreCase("Social"))
						{
							resource_id=R.drawable.social;
						}
						else if (type.equalsIgnoreCase("religious"))
						{
							resource_id=R.drawable.religious;
						}
						else if (type.equalsIgnoreCase("programming"))
						{
							resource_id=R.drawable.programming;
						}
						else if (type.equalsIgnoreCase("cinema"))
						{
							resource_id=R.drawable.cinema;
						}
						else if (type.equalsIgnoreCase("drink up"))
						{
							resource_id=R.drawable.drink_up;
						}
						else if (type.equalsIgnoreCase("music festival"))
						{
							resource_id=R.drawable.music_festival;
						}
						else if (type.equalsIgnoreCase("strike"))
						{
							resource_id=R.drawable.strike;
						}
						myMarkerOptions.icon(BitmapDescriptorFactory.fromResource(resource_id));
						Marker aMarker = googleMap.addMarker(myMarkerOptions);
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
					return;
				}
			}
			else
			{
				String text = "Failed To Retrieve Any Events";
				Toast.makeText(MainActivity.this, text, LONG_DURATION).show();
			}

		}

	}

}
