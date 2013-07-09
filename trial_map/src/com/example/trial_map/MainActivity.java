package com.example.trial_map;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Dialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements LocationListener
{

	private GoogleMap	googleMap;
	private Location	location;
	private int			defaultZoomLevel		= 13;
	private int			homeScreen				= R.layout.activity_main;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(homeScreen);

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
		// TODO Auto-generated method stub
		TextView tvLocation = (TextView) findViewById(R.id.tv_location);

		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		
		
		
		//googleMap.addMarker(new MarkerOptions().position(latLng).title(title));

		// Showing the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(defaultZoomLevel));

		//display events
		displayEventsIn10kmRadius();
		
		// Setting latitude and longitude in the TextView tv_location
		tvLocation.setText("Latitude:" + latitude + ", Longitude:" + longitude);

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
		MenuInflater menuInflater=getMenuInflater();
		menuInflater.inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menu_item)
	{
		
		switch (menu_item.getItemId())
		{
			case R.id.menu_addEvent:
				Toast.makeText(MainActivity.this, "add event is selected", Toast.LENGTH_SHORT).show();	
				goToNewEventScreen();
				return true;
			case  R.id.menu_settings:
				Toast.makeText(MainActivity.this, "settings selected", Toast.LENGTH_SHORT).show();
				return true;

		}
		return super.onOptionsItemSelected(menu_item);

	}

	private void goToNewEventScreen()
	{
		// TODO Auto-generated method stub
		Intent newEventScreen=new Intent(MainActivity.this,NewEventActivity.class);
		startActivityForResult(newEventScreen,100);
	}

	private void displayEventsIn10kmRadius()
	{
		try
		{//try to get events 
		ArrayList<Event> eventsArrayList=new ArrayList<Event>();
		eventsArrayList=EventsFactory.getEventsIn10KmRadius();
		//if u fail to query database for events then return
		Iterator<Event> iterator=eventsArrayList.iterator();
		while(iterator.hasNext())
		{
			Event anEvent=iterator.next();
			LatLng location=anEvent.getLocation_of_event();
			String title=""+anEvent.getDescription_of_event()+":on "+anEvent.getDate();
			String snippet="start time:"+anEvent.getTime();
			googleMap.addMarker(new MarkerOptions().position(location).title(title).snippet(snippet));
		}
		}
		catch(Exception e)
		{
			String text="Failed To Retrieve Any Events";
			Toast.makeText(MainActivity.this,text , Toast.LENGTH_LONG).show();
		}
	}
}
