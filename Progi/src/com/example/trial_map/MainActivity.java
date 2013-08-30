package com.example.trial_map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.trial_map.asyncTasks.DrawRouteTask;
import com.example.trial_map.beans.ActionItem;
import com.example.trial_map.beans.Contact;
import com.example.trial_map.beans.Event;
import com.example.trial_map.beans.EventOwner;
import com.example.trial_map.beans.User;
import com.example.trial_map.managers.ContactsManager;
import com.example.trial_map.managers.EventManager;
import com.example.trial_map.managers.Manager;
import com.example.trial_map.managers.NetworkManager;
import com.example.trial_map.widgets.QuickAction;
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

//FIXME app should allow users to rate events heard of this y/n

/**
 * @author nsubugak This is the main activity that displays map and other
 *         options to user
 */
public class MainActivity extends SherlockFragmentActivity implements LocationListener
{
	// important constants from xml file
	private Resources									res																									= null;
	public static String							WEBSITE_URL																					= null;
	public static String							GOOGLE_DIRECTIONS_URL																= "https://maps.googleapis.com/maps/api/directions/";
	public static String							GOOGLE_PLACES_URL																		= "https://maps.googleapis.com/maps/api/place/";
	// constants
	private static final int					NEW_EVENT_ACTIVITY_RESULT_CODE											= 100;
	private static final int					DETAILS_ACTIVITY_RESULT_CODE												= 200;
	private static final int					LIST_EVENTS_ACTIVITY_RESULT_CODE										= 300;
	private static final int					LOGIN_ACTIVITY_RESULT_CODE													= 400;
	private static final int					LIST_EVENTS_USER_IS_INVITED_TO_ACTIVITY_RESULT_CODE	= 500;
	private static final int					GOOGLE_MAP_DEFAULT_ZOOM_LEVEL												= 13;
	private static final int					HOME_SCREEN																					= R.layout.main_activity;
	private static final int					SHORT_DURATION																			= Toast.LENGTH_SHORT;
	private static final int					LONG_DURATION																				= Toast.LENGTH_LONG;
	private static final String				LOGIN_BUTTON_TEXT																		= "Login";
	private static final String				LOG_OUT_BUTTON_TEXT																	= "Log Out";
	private static final String				LOG_OUT_SUCCESS_TEXT																= "You Have Been Logged Out";
	private static final String				LOG_IN_SUCCESS_TEXT																	= "Logged In as:";
	private static final String				EVENT_CREATED_TEXT																	= "Event Created Successfully";
	private static final String				ILLEGAL_PARAMETER_TEXT															= "Parameters cannot be null";
	private static final CharSequence	FAILED_TO_GET_EVENTS_TEXT														= "Failed To Retrieve Any Events";
	private static final String				GOOGLE_MARKER_SNIPPET_TEXT													= "On:";
	private static final CharSequence	ALERT_DIALOG_TITLE																	= "Ops!! Sorry!!";
	private static final CharSequence	ALERT_DIALOG_MSG																		= "This Application Requires An Internet Connection";
	private static final CharSequence	ALERT_BUTTON_TEXT																		= "Okay";
	private static final long					VIBRATION_DURATION																	= 2000;
	private static final int					LOGIN																								= R.id.menu_login;
	private static final int					LIST_EVENTS																					= R.id.menu_listEvents;
	private static final int					SEE_EVENTS_U_ARE_INVITED_TO													= R.id.menu_seeEventsToWhichUareInvited;
	private static final int					ADD_AN_EVENT																				= R.id.menu_addEvent;
	private static final String				ACTION_DRAW_ROUTE_TITLE															= "Draw Route On Map";
	private static final int					DRAW_ROUTE_ICON																			= R.drawable.get_directions;
	private static final String				ACTION_DETAILS_TITLE																= "Details";
	private static final int					EVENT_DETAILS_ICON																	= R.drawable.event_details;
	private static final String				ACTION_GET_DIRECTIONS_TITLE													= "Get Directions";
	private static final int					GET_DIRECTIONS_ICON																	= R.drawable.get_directions;

	public static double							user_latitude																				= 0;
	public static double							user_longitude																			= 0;
	public static ArrayList<Event>		sortedArrayList																			= null;
	public static String							type_of_event																				= null;
	public static boolean							isInForeGround																			= false;

	// variables
	private boolean										first_time																					= true;
	private ArrayList<Event>					eventsArrayList																			= null;
	public static ArrayList<Event>		transientArrayList																	= null;
	private HashMap<String, Event>		eventMarkerMap																			= new HashMap<String, Event>();
	private GoogleMap									googleMap;
	private Location									location;
	public static User								theUser																							= null;
	private Menu											menu																								= null;
	private Vibrator									myVib;
	private ActionItem								action_drawRoute;
	private ActionItem								action_details;
	private QuickAction								mQuickAction;
	public static LatLng							dest																								= null;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		super.onCreate(savedInstanceState);

		try
		{
			if (NetworkManager.isInternetAvailable(this))
			{
				setContentView(HOME_SCREEN);
				initializeStaticVariables();
				DrawMapTask drawMapTask = new DrawMapTask();
				drawMapTask.execute();
				initializeQuickActionBar(this);
			}
			else
			{
				showNoInternetFoundAlertDialog();
			}
		}
		catch (NullPointerException e)
		{
			restartActivity();
		}
		catch (Exception e)
		{
		}

	}


	private void restartActivity()
	{
		Intent intent = new Intent(MainActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}


	/**
	 * DO_NOT_DELETE this reads variables from strings xml file and initializes
	 * them so that other classes can use them
	 **/
	private void initializeStaticVariables()
	{
		res = getResources();
		WEBSITE_URL = res.getString(R.string.website_url);
		Manager.PHP_SCRIPT_ADDRESS = WEBSITE_URL;
	}


	/** Called when the activity is brought to foreground again **/
	@Override
	protected void onResume()
	{
		super.onResume();
		// enable toast messages by setting this to true
		isInForeGround = true;
	}


	/** Called When user navigates away from app **/
	@Override
	protected void onPause()
	{
		super.onPause();
		// disable toast messages by setting this to false
		isInForeGround = false;
	}


	/** displays toast messages if this activity in the foreground **/
	public static void displayToast(Context aContext, String text, int duration)
	{
		if (isInForeGround)
		{
			Toast.makeText(aContext, text, duration).show();
		}
	}


	protected boolean isRouteDisplayed()
	{
		return false;
	}


	/** Called when user changes location by moving phone **/
	@Override
	public void onLocationChanged(Location location)
	{
		if (googleMap != null && location != null)
		{ // update the events u are displaying
			OnLocationChangedTask onLocationChangedTask = new OnLocationChangedTask();
			onLocationChangedTask.execute(location);
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


	/** method called to create menu and its items **/
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


	/** handler for click on menu item **/
	@Override
	public boolean onOptionsItemSelected(MenuItem menu_item)
	{

		switch (menu_item.getItemId())
		{
			case LOGIN:
				String title = menu_item.getTitle().toString();
				if (title.equalsIgnoreCase(LOGIN_BUTTON_TEXT))
				{// user wants to login
					goToLoginScreen();
				}
				else
				{// user logging out
					logOut(menu_item, menu);
				}
				return true;
			case LIST_EVENTS:
				StartListingAllEventsTask startListingEventsTask = new StartListingAllEventsTask();
				startListingEventsTask.execute();
				return true;
			case ADD_AN_EVENT:
				goToNewEventScreen();
				return true;
			case SEE_EVENTS_U_ARE_INVITED_TO:
				if (getUserContact(theUser) != null)
				{
					theUser = getUserContact(theUser);
					GetEventsUserIsInvitedToEventsTask getEventsUserIsInvitedToEventsTask = new GetEventsUserIsInvitedToEventsTask();
					getEventsUserIsInvitedToEventsTask.execute(theUser);
				}
				else
				{
					showGetUserContactDialog();
					displayToast(this, Manager.MESSAGE, LONG_DURATION);

				}
				return true;

		}
		return super.onOptionsItemSelected(menu_item);

	}


	private void gotoListEventsToWhichUserIsInvited()
	{
		Intent listEventsScreen = new Intent(MainActivity.this, ListEventsUserIsInvitedToActivity.class);
		startActivityForResult(listEventsScreen, LIST_EVENTS_USER_IS_INVITED_TO_ACTIVITY_RESULT_CODE);
	}


	/**
	 * 
	 * 
	 * this logs the user out
	 */
	private void logOut(MenuItem menu_item, Menu menu)
	{
		// user wants to logout
		theUser = null;
		// make button say 'Login'
		menu_item.setTitle(LOGIN_BUTTON_TEXT);
		// display message
		displayToast(MainActivity.this, LOG_OUT_SUCCESS_TEXT, SHORT_DURATION);
		// disable add event menu item
		MenuItem addEvent = menu.findItem(R.id.menu_addEvent);
		addEvent.setEnabled(false);
	}


	/**
	 * creates a new subactivity for logging in
	 */
	private void goToLoginScreen()
	{
		Intent loginScreen = new Intent(MainActivity.this, LoginActivity.class);
		startActivityForResult(loginScreen, LOGIN_ACTIVITY_RESULT_CODE);
	}


	/**
	 * creates a new subactivity to create new event
	 */
	private void goToNewEventScreen()
	{

		if (theUser == null)
		{// if he is not logged in [probably will never reach here but just to be
			// safe]
			return;
		}
		Intent newEventScreen = new Intent(MainActivity.this, NewEventActivity.class);
		Bundle aBundle = new Bundle();
		aBundle.putInt("user_id", ((EventOwner) theUser).getUser_id());
		newEventScreen.putExtras(aBundle);
		startActivityForResult(newEventScreen, NEW_EVENT_ACTIVITY_RESULT_CODE);
	}


	/** handle results returned by sub activities **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		isInForeGround = true;
		switch (requestCode)
		{
		// return form NewEventActivity
			case NEW_EVENT_ACTIVITY_RESULT_CODE:
				if (resultCode == RESULT_OK)
				{
					displayToast(this, EVENT_CREATED_TEXT, LONG_DURATION);
				}
				break;
			// return from EventDetailsActivity
			case DETAILS_ACTIVITY_RESULT_CODE:
				break;
			// return from ListEventsActivity
			case LIST_EVENTS_ACTIVITY_RESULT_CODE:
				if (resultCode == RESULT_OK)
				{
					if (dest == null)
					{
						throw new IllegalArgumentException(ILLEGAL_PARAMETER_TEXT);
					}
					LatLng origin = new LatLng(user_latitude, user_longitude);
					// Getting URL to the Google Directions API
					String url = NetworkManager.getDirectionsUrl(origin, dest);
					// start background thread
					DrawRouteTask drawRouteTask = new DrawRouteTask(this, googleMap);
					// draw the route
					drawRouteTask.execute(url);
				}
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
					login.setTitle(LOG_OUT_BUTTON_TEXT);
					// inform user
					String success_text = LOG_IN_SUCCESS_TEXT + " " + ((EventOwner) theUser).getEmail();
					displayToast(MainActivity.this, success_text, Toast.LENGTH_LONG);
				}
			default:
				break;
		}

	}


	/**
	 * @param data
	 *          intent containing the eventOwner object retrieves an event owner
	 *          object
	 */
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
		String company_name = aBundle.getString("company_name");
		String description = aBundle.getString("description");
		String location = aBundle.getString("location");
		String number = aBundle.getString("number");
		String name = aBundle.getString("name");
		Contact usersContact = new Contact(name, number);
		theUser = new EventOwner(user_id, email, password, company_name, location, description, usersContact);

	}


	/**
	 * displays alert dialog if no Internet found
	 */
	private void showNoInternetFoundAlertDialog()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

		// set title
		alertDialogBuilder.setTitle(ALERT_DIALOG_TITLE);

		// set dialog message
		alertDialogBuilder.setMessage(ALERT_DIALOG_MSG).setCancelable(false).setPositiveButton(ALERT_BUTTON_TEXT, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				// if this button is clicked, close
				// current activity
				MainActivity.this.finish();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}


	/**
	 * this class starts the list events activity in the background
	 */
	private class StartListingAllEventsTask extends AsyncTask<String, Integer, Boolean>
	{
		private final CharSequence	PROGRESS_DIALOG_TEXT	= "Getting Events.Please Wait...";
		private ProgressDialog			pDialog;
		private Intent							listEventsScreen;


		@Override
		protected void onPreExecute()
		{
			// create progress dialog and display it to user
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage(PROGRESS_DIALOG_TEXT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}


		@Override
		protected Boolean doInBackground(String... args)
		{

			// first check if there are any events
			transientArrayList = EventManager.getEventsIn10KmRadius(new LatLng(user_latitude, user_longitude));
			listEventsScreen = new Intent(MainActivity.this, ListEventsActivity.class);
			if (theUser != null)
			{
				sendUserId(theUser, listEventsScreen);
			}
			try
			{
				if (transientArrayList.size() <= 0)
				{
					return false;
				}
			}
			catch (NullPointerException e)
			{
				return false;
			}

			return true;

		}


		private void sendUserId(User aUser, Intent intent)
		{
			try
			{
				EventOwner anEventOwner=(EventOwner)aUser;
				int user_id = anEventOwner.getUser_id();
				Bundle extras = new Bundle();
				extras.putInt("user_id", user_id);
				intent.putExtras(extras);
			}
			catch (Exception e)
			{
				
			}
			
		}


		@Override
		protected void onPostExecute(Boolean bool)
		{
			super.onPostExecute(bool);
			pDialog.dismiss();
			if (bool)
			{
				startActivityForResult(listEventsScreen, LIST_EVENTS_ACTIVITY_RESULT_CODE);
			}
			else
			{
				displayToast(MainActivity.this, Manager.MESSAGE, LONG_DURATION);
			}
		}
	}


	/**
	 * this class starts the list events activity in the background
	 */
	private class GetEventsUserIsInvitedToEventsTask extends AsyncTask<User, Integer, Boolean>
	{
		private final CharSequence	PROGRESS_DIALOG_TEXT	= "Getting Events.Please Wait...";
		private ProgressDialog			pDialog;


		@Override
		protected void onPreExecute()
		{
			// create progress dialog and display it to user
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage(PROGRESS_DIALOG_TEXT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}


		@Override
		protected Boolean doInBackground(User... users)
		{
			User aUser = users[0];
			if (aUser == null)
			{
				throw new IllegalArgumentException(ILLEGAL_PARAMETER_TEXT);
			}

			transientArrayList = EventManager.getEventsToWhichUserIsInvited(aUser);
			if (transientArrayList.size() <= 0)
			{
				return false;
			}
			return true;

		}


		@Override
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result)
			{
				gotoListEventsToWhichUserIsInvited();
				return;
			}
			displayToast(getApplicationContext(), Manager.MESSAGE, LONG_DURATION);
		}

	}


	private void initializeQuickActionBar(Context aContext)
	{
		// Delete action item
		action_drawRoute = new ActionItem();
		// set text for delete action item
		action_drawRoute.setTitle(ACTION_DRAW_ROUTE_TITLE);
		// set icon for delete action item
		action_drawRoute.setIcon(getResources().getDrawable(DRAW_ROUTE_ICON));
		// Add details action item
		action_details = new ActionItem();
		// set text for details action item
		action_details.setTitle(ACTION_DETAILS_TITLE);
		// set icon for details action item
		action_details.setIcon(getResources().getDrawable(EVENT_DETAILS_ICON));

		// Get Route action
		ActionItem action_directions = new ActionItem();
		// set text for action item
		action_directions.setTitle(ACTION_GET_DIRECTIONS_TITLE);
		// set icon for action item
		action_directions.setIcon(getResources().getDrawable(GET_DIRECTIONS_ICON));

		mQuickAction = new QuickAction(aContext);

		mQuickAction.addActionItem(action_details);
		mQuickAction.addActionItem(action_drawRoute);
		mQuickAction.addActionItem(action_directions);

		// setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener()
		{
			@Override
			public void onItemClick(int pos)
			{
				if (pos == 0)// if details is clicked
				{

				}
				else if (pos == 1)// if delete event is clicked
				{
					if (true)
					{
						// show alert dialog
						// showTheDoYouReallyWantToDeleteThisDialog();
					}

				}
				else if (pos == 2)// if edit event is clicked
				{
					if (true)
					{

					}

				}
				else if (pos == 3)// if get directions is clicked
				{
				}
			}
		});
	}


	protected User getUserContact(User aUser)
	{
		if (aUser != null)
		{
			return aUser;
		}
		SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		// SharedPreferences.Editor editor=preferences.edit();
		String name = preferences.getString("name", null);
		String number = preferences.getString("number", null);
		if (name == null || number == null)
		{
			return null;
		}
		aUser = new User(new Contact(name, number));
		return aUser;
	}


	protected void showGetUserContactDialog()
	{
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(getBaseContext());
		View promptsView = li.inflate(R.layout.get_user_contact_prompt, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompts.xml to alert dialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userPhoneNumber = (EditText) promptsView.findViewById(R.id.getUserContact_userPhoneNumber);
		final EditText userName = (EditText) promptsView.findViewById(R.id.getUserContact_username);

		// set dialog message
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// get user input and set it to result
				// edit text
				String user_name = userName.getText().toString();
				String phone_number = userPhoneNumber.getText().toString();
				Contact usersContact = new Contact(user_name, phone_number);
				// theUser = new User(usersContact);
				ContactsManager.saveUsersContactLocaly(usersContact, MainActivity.this);
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}


	/**
	 * Loads Map In Background thread inorder to make app more responsive
	 * 
	 */
	private class DrawMapTask extends AsyncTask<String, Integer, Location>
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

				// create progress dialog and display it to user
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setMessage(PROGRESS_DIALOG_TEXT);
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();

				// Getting reference to the SupportMapFragment of activity_main.xml
				SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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


	/**
	 * handles location changes in background inorder to make app more responsive
	 * 
	 */
	private class OnLocationChangedTask extends AsyncTask<Location, Integer, LatLng>
	{
		@Override
		protected LatLng doInBackground(Location... arg0)
		{
			if (arg0 != null)
			{
				// Getting latitude of the current location
				user_latitude = location.getLatitude();

				// Getting longitude of the current location
				user_longitude = location.getLongitude();

				// Creating a LatLng object for the current location
				LatLng latLng = new LatLng(user_latitude, user_longitude);
				// try to get events
				eventsArrayList = EventManager.getEventsIn10KmRadius(latLng);
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

				if (first_time)
				{
					myVib.vibrate(VIBRATION_DURATION);
					displayToast(MainActivity.this, "Welcome", LONG_DURATION);
					first_time = false;
				}

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

						// get event data
						String type = anEvent.getType_of_event();
						LatLng location = anEvent.getLocation_of_event();
						String title = anEvent.getName_of_event().toUpperCase();
						String snippet = GOOGLE_MARKER_SNIPPET_TEXT + anEvent.getDate();

						// set marker options
						MarkerOptions myMarkerOptions = new MarkerOptions();
						myMarkerOptions.position(location);
						myMarkerOptions.title(title);
						myMarkerOptions.snippet(snippet);
						int resource_id = getIconBasedOnTypeOfEvent(type);
						myMarkerOptions.icon(BitmapDescriptorFactory.fromResource(resource_id));

						// add marker to map
						Marker aMarker = googleMap.addMarker(myMarkerOptions);
						// only store event in hash table if its not already stored
						String marker_id = aMarker.getId();
						if (!eventMarkerMap.containsKey(marker_id))
						{
							eventMarkerMap.put(marker_id, anEvent);
						}
					}
					googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
					{

						@Override
						public void onInfoWindowClick(Marker marker)
						{

							// Code To Handle a click on the info window
							if (eventsArrayList != null)
							{// if the events array list is not empty
								// get the event linked to marker
								Event anEvent = getEventAssociatedWithMarker(marker);
								if (anEvent == null)
								{// if no event is found return now
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

							Double longitude = anEvent.getLongitude();

							String time = anEvent.getTime();

							String date = anEvent.getDate();

							String description = anEvent.getDescription_of_event();

							String name = anEvent.getName_of_event();

							String duration = anEvent.getDuration();

							String location_in_words = anEvent.getEvent_location_in_words();
							int user_id = anEvent.getUser_id();
							int event_id = anEvent.getEvent_id();
							String type = anEvent.getType_of_event();
							Boolean heard_of_event = anEvent.getHeard_of_this_event_status();
							int total_people_who_have_heard = anEvent.getTotal_people_who_have_heard();
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
							extras.putBoolean("heard_of_event", heard_of_event);
							extras.putInt("total_people_who_have_heard", total_people_who_have_heard);
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

								Event anEvent = eventMarkerMap.get(marker_id);

								// return event
								return anEvent;
							}
							return null;

						}
					});
					return;
				}
				else
				{
					displayToast(MainActivity.this, (String) FAILED_TO_GET_EVENTS_TEXT, LONG_DURATION);
				}

			}
		}


		private int getIconBasedOnTypeOfEvent(String type)
		{
			int resource_id = 0;
			if (type.equalsIgnoreCase("meeting"))
			{
				resource_id = R.drawable.meeting;
			}
			else if (type.equalsIgnoreCase("party"))
			{
				resource_id = R.drawable.party;
			}
			else if (type.equalsIgnoreCase("Social"))
			{
				resource_id = R.drawable.social;
			}
			else if (type.equalsIgnoreCase("religious"))
			{
				resource_id = R.drawable.religious;
			}
			else if (type.equalsIgnoreCase("programming"))
			{
				resource_id = R.drawable.programming;
			}
			else if (type.equalsIgnoreCase("cinema"))
			{
				resource_id = R.drawable.cinema;
			}
			else if (type.equalsIgnoreCase("drink up"))
			{
				resource_id = R.drawable.drink_up;
			}
			else if (type.equalsIgnoreCase("music festival"))
			{
				resource_id = R.drawable.music_festival;
			}
			else if (type.equalsIgnoreCase("strike"))
			{
				resource_id = R.drawable.strike;
			}
			return resource_id;
		}
	}

}
