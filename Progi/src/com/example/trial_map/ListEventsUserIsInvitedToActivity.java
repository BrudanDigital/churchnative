package com.example.trial_map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.trial_map.adapters.CustomArrayAdapter;
import com.example.trial_map.adapters.DateComparator;
import com.example.trial_map.asyncTasks.GetDirectionsTask;
import com.example.trial_map.beans.ActionItem;
import com.example.trial_map.beans.Contact;
import com.example.trial_map.beans.Event;
import com.example.trial_map.managers.ContactsManager;
import com.example.trial_map.managers.Manager;
import com.example.trial_map.widgets.QuickAction;
import com.google.android.gms.maps.model.LatLng;

public class ListEventsUserIsInvitedToActivity extends ActionBarListActivity
{
	private static final int				DETAILS_ACTIVITY_RESULT_CODE		= 100;
	private static final int				DISPLAY_DIRECTIONS_RESULT_CODE	= 400;
	private static final int				BACK														= R.id.menu_back;
	protected static final int			EDIT_EVENT_RESULT_CODE					= 100;
	private static final int				SHORT_DURATION									= Toast.LENGTH_SHORT;
	protected static final int			LONG_DURATION										= Toast.LENGTH_LONG;
	private static final String			ACTION_WHO_ELSE_TITLE						= "Who Else Is Invited";
	private static final String			ACTION_GET_DIRECTIONS_TITLE			= "Get Diretions";
	private static final String			ACTION_DETAILS_TITLE						= "Details";
	private static final int				GET_DIRECTIONS_ICON							= R.drawable.get_directions;
	private static final int				EVENT_DETAILS_ICON							= R.drawable.event_details;
	private static final int				ACTION_WHO_ELSE_ICON						= R.drawable.who_else;
	private ArrayList<Event>				events_ArrayList								= MainActivity.transientArrayList;
	private View										view														= null;
	private int											index_of_selected_event					= -1;
	private QuickAction							mQuickAction										= null;
	private GetEventDetailsTask			getEventDetailsTask;
	private ActionItem							action_directions;
	private ActionItem							action_who_else;
	private ActionItem							action_details;
	public static ArrayList<String>	directionsArrayList							= null;

	String[]												type_array											= { "meeting", "party", "social", "religious", "programming", "cinema", "drink up", "music festival", "strike" };
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setBackgroundColor(Color.BLACK);
		enableQuickActionItems();

		// set list adapter to my customer adapter
		ListEventsUserIsInvitedToActivity.this.setListAdapter(new CustomArrayAdapter(ListEventsUserIsInvitedToActivity.this, getEventsAsStringArray(), getEventsArrayList()));
	
	}

	/** returns an array of events to displayed in list View **/
	private ArrayList<Event> getEventsArrayList()
	{
		if (MainActivity.type_of_event != null)
		{
			return MainActivity.sortedArrayList;
		}
		return this.events_ArrayList;
	}


	/**
	 * initializes the quick action bar and its items
	 */
	private void initializeQuickActionBar()
	{
		// Add details action item
		action_details = new ActionItem();
		// set text for details action item
		action_details.setTitle(ACTION_DETAILS_TITLE);
		// set icon for details action item
		action_details.setIcon(getResources().getDrawable(EVENT_DETAILS_ICON));

		// Get Route action
		action_directions = new ActionItem();
		// set text for action item
		action_directions.setTitle(ACTION_GET_DIRECTIONS_TITLE);
		// set icon for action item
		action_directions.setIcon(getResources().getDrawable(GET_DIRECTIONS_ICON));

		mQuickAction = new QuickAction(this);

		mQuickAction.addActionItem(action_details);
		mQuickAction.addActionItem(action_who_else);
		mQuickAction.addActionItem(action_directions);

		// setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener()
		{
			@Override
			public void onItemClick(int pos)
			{
				if (index_of_selected_event == -1)
				{
					return;
				}
				if (pos == 0)// if details is clicked
				{
					getEventDetailsTask = new GetEventDetailsTask();
					getEventDetailsTask.execute();
				}
				else if (pos == 1)// if who else is clicked
				{
					Event selectedEvent = getSelectedEvent(index_of_selected_event);
					GetAllContactsInvitedToEventTask getAllContactsInvitedToEventTask = new GetAllContactsInvitedToEventTask();
					getAllContactsInvitedToEventTask.execute(selectedEvent);
				}
				else if (pos == 2)// if get directions is clicked
				{ // start display directions activity
					LatLng dest = new LatLng(getSelectedEvent(index_of_selected_event).getLatitude(), getSelectedEvent(index_of_selected_event).getLongitude());
					GetDirectionsTask getDirectionsTask = new GetDirectionsTask(ListEventsUserIsInvitedToActivity.this);
					getDirectionsTask.execute(dest);
				}
			}
		});
	}


	/**
	 * makes the delete and edit quick actions clickable
	 */
	private void enableQuickActionItems()
	{
		// Delete action item
		action_who_else = new ActionItem();
		// set text for delete action item
		action_who_else.setTitle(ACTION_WHO_ELSE_TITLE);
		// set icon for delete action item
		action_who_else.setIcon(getResources().getDrawable(ACTION_WHO_ELSE_ICON));
		initializeQuickActionBar();
	}


	/*
	 * handler for click on an list view item by user
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		// make index of selected event global
		index_of_selected_event = position;
		enableQuickActionItems();

		mQuickAction.show(v);
		mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
		// if there exist another view already[user clicked on item then clicked on
		// another]
		if (view != null)
		{
			// return original view colour to black
			view.setBackgroundColor(Color.BLACK);
		}
		// make view global
		view = v;
		v.setBackgroundColor(getResources().getColor(R.color.orange));

	}


	/** handles results returned by sub activities **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (view != null)
		{// return color of background back to black
			view.setBackgroundColor(Color.BLACK);
			view = null;
		}
		if (requestCode == DETAILS_ACTIVITY_RESULT_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				displayToast(getApplicationContext(), "SAVED", SHORT_DURATION);
			}
		}
		if (requestCode == DISPLAY_DIRECTIONS_RESULT_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				setResult(RESULT_OK);
				finish();
			}
		}
	}


	/**
	 * restarts the current activity efficiently to display changes in the
	 * database
	 */
	private void refreshListView()
	{
		// reload this screen again
		Intent intent = getIntent();
		finish();
		startActivity(intent);

	}


	/**
	 * @param intent
	 *          the intent to be used to send the event
	 * @param anEvent
	 *          event to be sent sends an event to next activity given an intent
	 */
	private void sendEvent(Intent intent, Event anEvent)
	{
		// get object state
		Double latitude = anEvent.getLatitude();
		Double longitude = anEvent.getLongitude();
		String time = anEvent.getTime();
		String date = anEvent.getDate();
		String description = anEvent.getDescription_of_event();
		String name = anEvent.getName_of_event();
		String duration = anEvent.getDuration();
		String location_in_words = anEvent.getEvent_location_in_words();
		int user_id;
		Bundle aBundle = getIntent().getExtras();
		if (aBundle != null)
		{
			user_id = aBundle.getInt("user_id");
		}
		user_id = anEvent.getUser_id();
		int event_id = anEvent.getEvent_id();
		String type = anEvent.getType_of_event();
		Boolean heard_of_event = anEvent.getHeard_of_this_event_status();
		int total_people_who_have_heard = anEvent.getTotal_people_who_have_heard();
		// create bundle to store all the object state
		Bundle extras = new Bundle();
		// add state to bundle
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
		// add bundle to intent
		intent.putExtras(extras);
	}


	/**
	 * @return an array list containing names of all events returns event
	 *         descriptions in string array
	 */
	public ArrayList<String> getEventsAsStringArray()
	{

		if (this.events_ArrayList == null)
		{
			throw new IllegalStateException("events_ArrayList cant be null");
		}
		return sortByDateOfEvent();

	}


	/** returns an array of the events sorted by date created **/
	private ArrayList<String> sortByDateOfEvent()
	{
		if (events_ArrayList == null)
		{
			return null;
		}
		Collections.sort(events_ArrayList, new DateComparator());
		ArrayList<String> sortedArrayList = new ArrayList<String>();
		Iterator<Event> iterator = events_ArrayList.iterator();
		while (iterator.hasNext())
		{
			Event event = iterator.next();
			String data = capitaliseFirstLetterOfEachWord(event.getName_of_event()) + "\nOn:" + event.getDate();
			sortedArrayList.add(data);
		}
		// MainActivity.sort_by_date = true;
		return sortedArrayList;
	}

	
	/** Called when creating menu items **/
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// show action bar
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		// add menu options to the UI
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.layout.menu_list_events, menu);
		return true;
	}

	
	private void showPopUp(ArrayList<Contact> contactsList)
	{
		final CharSequence[] items = ContactsManager.getContactNames(contactsList);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("INVITED PEOPLE");
		builder.setItems(items, null).setPositiveButton("OK", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialogInterface, int arg1)
			{
				dialogInterface.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}


	/** handler for clicks on menu items **/
	@Override
	public boolean onOptionsItemSelected(MenuItem menu_item)
	{

		switch (menu_item.getItemId())
		{
			case BACK:
				resetMainActivityVariables();
				setResult(RESULT_CANCELED);
				finish();
				return true;
			case 1:
				MainActivity.type_of_event = "meeting";
				refreshListView();
				return true;
			case 2:
				MainActivity.type_of_event = "party";
				refreshListView();
				return true;
			case 3:
				MainActivity.type_of_event = "social";
				refreshListView();
				return true;
			case 4:
				MainActivity.type_of_event = "religious";
				refreshListView();
				return true;
			case 5:
				MainActivity.type_of_event = "programming";
				refreshListView();
				return true;
			case 6:
				MainActivity.type_of_event = "cinema";
				refreshListView();
				return true;
			case 7:
				MainActivity.type_of_event = "drink up";
				refreshListView();
				return true;
			case 8:
				MainActivity.type_of_event = "music festival";
				refreshListView();
				return true;
			case 9:
				MainActivity.type_of_event = "strike";
				refreshListView();
				return true;

		}
		return super.onOptionsItemSelected(menu_item);

	}


	/** resets the static flags in main activity to null **/
	private void resetMainActivityVariables()
	{
		MainActivity.sortedArrayList = null;
		MainActivity.type_of_event = null;
	}


	/**
	 * this class starts the event details activity in background while displaying
	 * progress dialog
	 * 
	 */
	private class GetEventDetailsTask extends AsyncTask<Void, String, Void>
	{
		private final CharSequence	PROGRESS_DIALOG_TEXT	= "Getting Details...";
		private ProgressDialog			pDialog;


		@Override
		protected void onPreExecute()
		{
			// create progress dialog and display it to user
			pDialog = new ProgressDialog(ListEventsUserIsInvitedToActivity.this);
			pDialog.setMessage(PROGRESS_DIALOG_TEXT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}


		@Override
		protected Void doInBackground(Void... arg0)
		{
			// create a new sub activity
			Intent intent = new Intent(ListEventsUserIsInvitedToActivity.this, EventDetailsActivity.class);
			// Use helper method to send event to next activity
			sendEvent(intent, getSelectedEvent(index_of_selected_event));
			// start new sub activity
			pDialog.dismiss();
			startActivityForResult(intent, DETAILS_ACTIVITY_RESULT_CODE);
			return null;
		}
	}


	/** capitalizes the first letter of each word in a given string **/
	public static String capitaliseFirstLetterOfEachWord(String aString)
	{
		if (aString == null)
		{
			throw new IllegalArgumentException("Parameter Cant be Null");
		}

		boolean prevWasWhiteSp = true;
		char[] chars = aString.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			if (Character.isLetter(chars[i]))
			{
				if (prevWasWhiteSp)
				{
					chars[i] = Character.toUpperCase(chars[i]);
				}
				prevWasWhiteSp = false;
			}
			else
			{
				prevWasWhiteSp = Character.isWhitespace(chars[i]);
			}
		}
		return new String(chars);
	}

	private class GetAllContactsInvitedToEventTask extends AsyncTask<Event, Void, ArrayList<Contact>>
	{

		@Override
		protected void onPreExecute()
		{
			PROGRESS_DIALOG_TEXT = "Getting Invited Contacts...";
			showProgressDialog();
		}


		@Override
		protected ArrayList<Contact> doInBackground(Event... events)
		{
			Event anEvent = events[0];

			if (anEvent == null)
			{
				throw new IllegalArgumentException("EVENT CANT BE NULL");
			}

			ArrayList<Contact> contactsList = ContactsManager.getAllContactsInvitedToAnEvent(anEvent);
			return contactsList;

		}


		@Override
		protected void onPostExecute(ArrayList<Contact> contactsList)
		{
			super.onPostExecute(contactsList);
			closeProgressDialog();
			displayToast(getApplicationContext(), Manager.MESSAGE, LONG_DURATION);
			if (contactsList.size() <= 0)
			{
				//Log.e("SIZE", "NO CONTACTS");
				return;
			}
			// display dialog showing who is invited
			showPopUp(contactsList);
		}
	}



	/** returns the event selected by the user from the list view **/
	public Event getSelectedEvent(int index_of_selected_event)
	{
		if (MainActivity.type_of_event != null)
		{
			return MainActivity.sortedArrayList.get(index_of_selected_event);
		}
		return events_ArrayList.get(index_of_selected_event);

	}

}
