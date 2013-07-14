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
	private static final int				NEW_EVENT_ACTIVITY_RESULT_CODE		= 100;
	private static final int				LIST_EVENTS_ACTIVITY_RESULT_CODE	= 300;
	private static final int				DETAILS_ACTIVITY_RESULT_CODE			= 200;
	private static final int				DEFAULT_ZOOM_LEVEL								= 13;
	private static final int				HOME_SCREEN												= R.layout.activity_main;

	private ArrayList<Event>				eventsArrayList										= null;
	private HashMap<String, Event>	eventMarkerMap										= new HashMap<String, Event>();
	private GoogleMap								googleMap;
	private Location								location;

	// FIXME app should check for net b4 starting
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(HOME_SCREEN);

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS)
		{ // Google Play Services are not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		}
		else
		{ // Google Play Services are available

			// Getting reference to the SupportMapFragment of activity_main.xml
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

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
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL));

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
						Intent intent = new Intent(MainActivity.this,
								EventDetailsActivity.class);
						// use helper class to send object to next activity
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
					Bundle extras = new Bundle();
					extras.putDouble("latitude", latitude);
					extras.putDouble("longitude", longitude);
					extras.putString("time", time);
					extras.putString("date", date);
					extras.putString("description", description);
					extras.putString("name", name);
					extras.putString("duration", duration);
					extras.putString("location_in_words", location_in_words);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// add menu options to the UI
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menu_item)
	{

		switch (menu_item.getItemId())
		{
			case R.id.menu_addEvent:
				Toast.makeText(MainActivity.this, "add event is selected",
						Toast.LENGTH_SHORT).show();
				goToNewEventScreen();
				return true;
			case R.id.menu_listEvents:
				Toast.makeText(MainActivity.this, "list events selected",
						Toast.LENGTH_SHORT).show();
				goToListEventsScreen();
				return true;

		}
		return super.onOptionsItemSelected(menu_item);

	}

	private void goToListEventsScreen()
	{
		Intent listEventsScreen = new Intent(MainActivity.this,
				ListEventsActivity.class);
		startActivityForResult(listEventsScreen,LIST_EVENTS_ACTIVITY_RESULT_CODE);

	}

	// start new event sub activity in order to create new event
	private void goToNewEventScreen()
	{
		Intent newEventScreen = new Intent(MainActivity.this,
				NewEventActivity.class);
		startActivityForResult(newEventScreen, NEW_EVENT_ACTIVITY_RESULT_CODE);
	}

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
				String title = "" + anEvent.getDescription_of_event().toUpperCase();
				String snippet = "On:" + anEvent.getDate();
				Marker aMarker = googleMap.addMarker(new MarkerOptions()
						.position(location).title(title).snippet(snippet));
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

	// handle results returned by sub activities
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == NEW_EVENT_ACTIVITY_RESULT_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				Toast.makeText(this, "Event Created Successfully", Toast.LENGTH_LONG)
						.show();
			}
		}
		else if (requestCode == DETAILS_ACTIVITY_RESULT_CODE)
		{
			Toast.makeText(this, "returning", Toast.LENGTH_SHORT).show();
		}
		displayEventsIn10kmRadius();
	}
}
