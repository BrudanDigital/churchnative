package com.example.trial_map;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.trial_map.beans.ActionItem;
import com.example.trial_map.beans.Event;
import com.example.trial_map.factories.EventsFactory;
import com.example.trial_map.widgets.CustomArrayAdapter;
import com.example.trial_map.widgets.QuickAction;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author nsubugak This class displays all events in 10km as a list for user to
 *         select from
 */
public class ListEventsActivity extends SherlockListActivity
{
	private static final int						DETAILS_ACTIVITY_RESULT_CODE		= 100;
	private static final int						BACK														= R.id.menu_back;
	protected static final int					EDIT_EVENT_RESULT_CODE					= 100;
	private static final CharSequence		UPDATE_EVENT_OK_TEXT						= "Event Updated SuccessFully";
	private static final int						SHORT_DURATION									= Toast.LENGTH_SHORT;
	private static final CharSequence		DELETE_DIALOG_MSG								= "Do You Really Want To Delete This Item";
	private static final CharSequence		DELETE_DIALOG_TITLE							= "Really";
	private static final CharSequence		DIALOG_POSITIVE_BTN_TXT					= "Yes";
	private static final CharSequence		DIALOG_NEGATIVE_BTN_TXT					= "No";
	private static final String					ACTION_DELETE_TITLE							= "Delete";
	private static final String					ACTION_EDIT_TITLE								= "Edit";
	private static final String					ACTION_GET_DIRECTIONS_TITLE			= "Get Diretions";
	private static final String					ACTION_DETAILS_TITLE						= "Details";
	private static final int						GET_DIRECTIONS_ICON							= R.drawable.get_directions;
	private static final int						EDIT_EVENT_ICON									= R.drawable.edit_event;
	private static final int						EDIT_EVENT_DISABLED_ICON				= R.drawable.edit_event_disabled;
	private static final int						EVENT_DETAILS_ICON							= R.drawable.event_details;
	private static final int						DELETE_EVENT_ICON								= R.drawable.delete_event;
	private static final int						DELETE_EVENT_DISABLED_ICON			= R.drawable.delete_event_disabled;
	protected static final CharSequence	DELETE_EVENT_SUCCESS_TEXT				= "Event Delete SuccessFully";
	protected static final int					LONG_DURATION										= Toast.LENGTH_LONG;
	private static final int						DISPLAY_DIRECTIONS_RESULT_CODE	= 400;
	private static LatLng								user_location										= new LatLng(MainActivity.user_latitude, MainActivity.user_longitude);
	private ArrayList<Event>						events_ArrayList								= EventsFactory.getEventsIn10KmRadius(user_location);
	private View												view														= null;
	private int													index_of_selected_event					= -1;
	private QuickAction									mQuickAction										= null;
	private GetEventDetailsTask					getEventDetailsTask;
	private EditEventDetailsTask				editEventDetailsTask;
	private int													USER_ID;
	private ActionItem									action_edit;
	private ActionItem									action_directions;
	private ActionItem									action_delete;
	private ActionItem									action_details;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// make background black
		getWindow().getDecorView().setBackgroundColor(Color.BLACK);

		USER_ID = getUserId();

		disableQuickActionItems();

		// set list adapter to my customer adapter
		setListAdapter(new CustomArrayAdapter(this, getEventsAsStringArray(), events_ArrayList));

	}


	/**
	 * makes the delete and edit quick actions unclickable
	 */
	private void disableQuickActionItems()
	{

		// Delete action item
		action_delete = new ActionItem();
		// set text for delete action item
		action_delete.setTitle(ACTION_DELETE_TITLE);
		action_delete.setEnabled(false);
		action_delete.setIcon(getResources().getDrawable(DELETE_EVENT_DISABLED_ICON));

		// Edit action item
		action_edit = new ActionItem();
		// set text for edit action item
		action_edit.setTitle(ACTION_EDIT_TITLE);
		action_edit.setEnabled(false);
		action_edit.setIcon(getResources().getDrawable(EDIT_EVENT_DISABLED_ICON));

		initializeQuickActionBar();
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
		mQuickAction.addActionItem(action_delete);
		mQuickAction.addActionItem(action_edit);
		mQuickAction.addActionItem(action_directions);

		// setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener()
		{
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
				else if (pos == 1)// if delete event is clicked
				{
					if (heCreatedTheEventInQuestion())
					{
						// show alert dialog
						showTheDoYouReallyWantToDeleteThisDialog();
					}

				}
				else if (pos == 2)// if edit event is clicked
				{
					if (heCreatedTheEventInQuestion())
					{
						// go to the edit activity
						editEventDetailsTask = new EditEventDetailsTask();
						editEventDetailsTask.execute();
					}

				}
				else if (pos == 3)// if get directions is clicked
				{ // start display directions activity
					Intent intent = new Intent(ListEventsActivity.this, DisplayDirectionsActivity.class);
					LatLng dest = new LatLng(events_ArrayList.get(index_of_selected_event).getLatitude(), events_ArrayList.get(index_of_selected_event).getLongitude());
					sendEventLocation(intent, dest);
					startActivityForResult(intent, DISPLAY_DIRECTIONS_RESULT_CODE);
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
		action_delete = new ActionItem();
		// set text for delete action item
		action_delete.setTitle(ACTION_DELETE_TITLE);
		// set icon for delete action item
		action_delete.setIcon(getResources().getDrawable(DELETE_EVENT_ICON));

		// Edit action item
		action_edit = new ActionItem();
		// set text for edit action item
		action_edit.setTitle(ACTION_EDIT_TITLE);
		// set icon for edit action item
		action_edit.setIcon(getResources().getDrawable(EDIT_EVENT_ICON));

		initializeQuickActionBar();
	}


	/**
	 * @return true if current user created event false other wise checks to see
	 *         if the current user created a the currently selected event
	 */
	private boolean heCreatedTheEventInQuestion()
	{
		Log.e("User_id", "" + USER_ID);
		Log.e("Event_user_id", "" + events_ArrayList.get(index_of_selected_event).getUser_id());
		if (USER_ID == -1)
		{
			return false;
		}
		else if (events_ArrayList.get(index_of_selected_event).getUser_id() == USER_ID)
		{
			return true;
		}
		return false;
	}


	/*
	 * handler for click on an list view item by user
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		// make index of selected event global
		index_of_selected_event = position;
		if (heCreatedTheEventInQuestion())
		{
			enableQuickActionItems();
		}
		else
		{
			disableQuickActionItems();
		}
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


	/*
	 * handle results returned by sub activities
	 */
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

		}
		if (requestCode == EDIT_EVENT_RESULT_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				Toast.makeText(this, UPDATE_EVENT_OK_TEXT, SHORT_DURATION).show();
				refreshListView();

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
	 * @param intent
	 *          the intent to be used to send the event
	 * @param dest
	 *          the destination to be sent with the intent sends the destination
	 *          using the given intent
	 */
	private void sendEventLocation(Intent intent, LatLng dest)
	{
		Double latitude = dest.latitude;
		Double longitude = dest.longitude;
		Bundle aBundle = new Bundle();
		aBundle.putDouble("lat", latitude);
		aBundle.putDouble("long", longitude);
		intent.putExtras(aBundle);
	}


	/**
	 * @return the current users id if exists or -1 if not found
	 */
	public int getUserId()
	{
		Bundle aBundle = getIntent().getExtras();
		if (aBundle != null)
		{
			try
			{
				int USER_ID = aBundle.getInt("user_id");
				return USER_ID;
			}
			catch (Exception e)
			{
			}
		}
		return -1;
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
		int user_id;
		Bundle aBundle = getIntent().getExtras();
		if (aBundle != null)
		{
			user_id = aBundle.getInt("user_id");
		}
		user_id = anEvent.getUser_id();
		int event_id = anEvent.getEvent_id();
		String type = anEvent.getType_of_event();
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
		// add bundle to intent
		intent.putExtras(extras);
	}


	/**
	 * @return an array list containing names of all events returns event
	 *         descriptions in string array
	 */
	public ArrayList<String> getEventsAsStringArray()
	{

		if (events_ArrayList == null)
		{
			return null;
		}
		Iterator<Event> iterator = events_ArrayList.iterator();
		ArrayList<String> eventStrings = new ArrayList<String>();
		while (iterator.hasNext())
		{
			Log.e("GET EVENTS", "events gotten");
			Event event = (Event) iterator.next();
			eventStrings.add(event.getName_of_event());
		}
		return eventStrings;
	}


	/**
	 * displays dialog asking user if he really wants to delete the event
	 */
	private void showTheDoYouReallyWantToDeleteThisDialog()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListEventsActivity.this);

		// set title
		alertDialogBuilder.setTitle(DELETE_DIALOG_TITLE);

		// set dialog message
		alertDialogBuilder.setMessage(DELETE_DIALOG_MSG).setCancelable(false).setPositiveButton(DIALOG_POSITIVE_BTN_TXT, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				int delete_status = EventsFactory.DeleteEvent(events_ArrayList.get(index_of_selected_event));
				if (delete_status == 1)
				{

					Toast.makeText(ListEventsActivity.this, DELETE_EVENT_SUCCESS_TEXT, LONG_DURATION).show();
					refreshListView();

				}
				else
				{
					String text = "Failed to Delete Event";
					int duration = Toast.LENGTH_LONG;
					Toast.makeText(ListEventsActivity.this, text, duration).show();
				}
				// events_ArrayList.remove(index_of_selected_event);
			}

		}).setNegativeButton(DIALOG_NEGATIVE_BTN_TXT, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockListActivity#onCreateOptionsMenu(android
	 * .view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// show action bar
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		// add menu options to the UI
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.layout.menu_custom, menu);
		return true;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockListActivity#onOptionsItemSelected(android
	 * .view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem menu_item)
	{

		switch (menu_item.getItemId())
		{
			case BACK:
				setResult(RESULT_CANCELED);
				finish();
				return true;
		}
		return super.onOptionsItemSelected(menu_item);

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
			pDialog = new ProgressDialog(ListEventsActivity.this);
			pDialog.setMessage(PROGRESS_DIALOG_TEXT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}


		@Override
		protected Void doInBackground(Void... arg0)
		{
			// create a new sub activity
			Intent intent = new Intent(ListEventsActivity.this, EventDetailsActivity.class);
			// Use helper method to send event to next activity
			sendEvent(intent, events_ArrayList.get(index_of_selected_event));
			// start new sub activity
			pDialog.dismiss();
			startActivityForResult(intent, DETAILS_ACTIVITY_RESULT_CODE);
			return null;
		}
	}


	/**
	 * this class starts the edit event activity in background while displaying
	 * progress dialog
	 */
	private class EditEventDetailsTask extends AsyncTask<Void, String, Void>
	{
		private final CharSequence	PROGRESS_DIALOG_TEXT	= "Getting Details...";
		private ProgressDialog			pDialog;


		@Override
		protected void onPreExecute()
		{
			// create progress dialog and display it to user
			pDialog = new ProgressDialog(ListEventsActivity.this);
			pDialog.setMessage(PROGRESS_DIALOG_TEXT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}


		@Override
		protected Void doInBackground(Void... arg0)
		{
			// create a new sub activity
			Intent intent = new Intent(ListEventsActivity.this, EditEventActivity.class);
			// Use helper method to send event to next activity
			sendEvent(intent, events_ArrayList.get(index_of_selected_event));
			// start new sub activity
			pDialog.dismiss();
			startActivityForResult(intent, EDIT_EVENT_RESULT_CODE);
			return null;
		}
	}

}
