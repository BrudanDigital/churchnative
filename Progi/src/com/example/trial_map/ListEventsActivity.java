package com.example.trial_map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.example.trial_map.beans.ActionItem;
import com.example.trial_map.beans.Event;
import com.example.trial_map.factories.EventsFactory;
import com.example.trial_map.factories.NetworkManager;
import com.example.trial_map.interfaces.DateComparator;
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
	private static final int						SUBMENU_GROUP_ID								= 1;
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
	public static ArrayList<String>			directionsArrayList							= null;
	// Resources resources = getResources();
	String[]														type_array											= { "meeting", "party", "social", "religious", "programming", "cinema", "drink up", "music festival", "strike" };
	private static boolean							isInForeGround									= false;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// make background black
		getWindow().getDecorView().setBackgroundColor(Color.BLACK);

		USER_ID = getUserId();

		disableQuickActionItems();

		// set list adapter to my customer adapter
		setListAdapter(new CustomArrayAdapter(this, getEventsAsStringArray(), getEventsArrayList()));

	}


	protected void onResume()
	{
		super.onResume();
		isInForeGround = true;
	}


	@Override
	protected void onPause()
	{
		super.onPause();
		isInForeGround = false;
	}


	public static void displayToast(Context aContext, String text, int duration)
	{
		if (isInForeGround)
		{
			Toast.makeText(aContext, text, duration).show();
		}
	}


	private ArrayList<Event> getEventsArrayList()
	{
		if (MainActivity.type_of_event != null)
		{
			return MainActivity.sortedArrayList;
		}
		return events_ArrayList;
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
					LatLng origin = new LatLng(MainActivity.user_latitude, MainActivity.user_longitude);
					LatLng dest = new LatLng(getSelectedEvent(index_of_selected_event).getLatitude(), getSelectedEvent(index_of_selected_event).getLongitude());
					MainActivity.dest=dest;
					GetDirectionsTask getDirectionsTask = new GetDirectionsTask();
					LatLng[] latLngs = { origin, dest };
					getDirectionsTask.execute(latLngs);
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
		// getSelectedEvent(index_of_selected_event).getUser_id());
		if (USER_ID == -1)
		{
			return false;
		}
		else if (getSelectedEvent(index_of_selected_event).getUser_id() == USER_ID)
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
		isInForeGround=true;
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
				displayToast(this, (String) UPDATE_EVENT_OK_TEXT, SHORT_DURATION);
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
			throw new IllegalStateException("events_ArrayList cant be null");
		}
		if (MainActivity.type_of_event != null )
		{
			return sortByType(MainActivity.type_of_event);
		}
		return sortByDateOfEvent();

	}


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
		//MainActivity.sort_by_date = true;
		return sortedArrayList;
	}


	public ArrayList<String> sortByType(String type_of_event)
	{

		if (events_ArrayList == null)
		{
			return null;
		}
		ArrayList<String> sortedArrayList = new ArrayList<String>();
		ArrayList<Event> sortedEventsArrayList = new ArrayList<Event>();
		Iterator<Event> iterator = events_ArrayList.iterator();
		while (iterator.hasNext())
		{
			Event event = iterator.next();
			if (event.getType_of_event().equalsIgnoreCase(type_of_event))
			{
				String data = capitaliseFirstLetterOfEachWord(event.getName_of_event()) + "\nOn:" + event.getDate();
				sortedArrayList.add(data);
				sortedEventsArrayList.add(event);
			}
		}
		MainActivity.sortedArrayList = sortedEventsArrayList;
		return sortedArrayList;
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
			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				int delete_status = EventsFactory.DeleteEvent(getSelectedEvent(index_of_selected_event));
				if (delete_status == 1)
				{

					displayToast(ListEventsActivity.this, (String) DELETE_EVENT_SUCCESS_TEXT, LONG_DURATION);
					refreshListView();

				}
				else
				{
					String text = "Failed to Delete Event";
					int duration = Toast.LENGTH_LONG;
					displayToast(ListEventsActivity.this, text, duration);
				}
			}

		}).setNegativeButton(DIALOG_NEGATIVE_BTN_TXT, new DialogInterface.OnClickListener()
		{
			@Override
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


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// show action bar
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		// add menu options to the UI
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.layout.menu_list_events, menu);
		SubMenu subMenu = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE, "Sort By Type");
		for (int i = 0; i < type_array.length; i++)
		{
			// add(group_id,unique_id,order,title)
			subMenu.add(SUBMENU_GROUP_ID, i + 1, i, type_array[i]);
		}

		return true;
	}


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


	private void resetMainActivityVariables()
	{
		MainActivity.sortedArrayList = null;
		MainActivity.type_of_event = null;
		//MainActivity.sort_by_date = false;
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
			sendEvent(intent, getSelectedEvent(index_of_selected_event));
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
			sendEvent(intent, getSelectedEvent(index_of_selected_event));
			// start new sub activity
			pDialog.dismiss();
			startActivityForResult(intent, EDIT_EVENT_RESULT_CODE);
			return null;
		}
	}

	public static String capitaliseFirstLetterOfEachWord(String aString)
	{
		if (aString==null)
		{
			throw new IllegalArgumentException("Parameter Cant be Null");
		}
		
		boolean prevWasWhiteSp = true;
    char[] chars = aString.toCharArray();
    for (int i = 0; i < chars.length; i++) {
        if (Character.isLetter(chars[i])) {
            if (prevWasWhiteSp) {
                chars[i] = Character.toUpperCase(chars[i]);    
            }
            prevWasWhiteSp = false;
        } else {
            prevWasWhiteSp = Character.isWhitespace(chars[i]);
        }
    }
    return new String(chars);
	}

	/**
	 * async task that uses network to get directions in words to destination
	 * 
	 */
	private class GetDirectionsTask extends AsyncTask<LatLng, String, ArrayList<String>>
	{
		private final String		PROGRESS_DIALOG_TEXT	= "Getting Directions...";
		private ProgressDialog	pDialog;


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
		protected ArrayList<String> doInBackground(LatLng... latLngs)
		{
			try
			{
				LatLng dest = latLngs[0];
				LatLng origin = latLngs[1];
				// get url to google
				String url = NetworkManager.getDirectionsUrl(origin, dest);
				// download the directions as json
				String json = NetworkManager.downloadJSONdata(url);
				// get the json data as an object
				JSONObject jsonObject = new JSONObject(json);
				// get the directions
				ArrayList<String> directions = NetworkManager.DrivingDirectionsParser(jsonObject);
				return directions;
			}
			catch (Exception e)
			{

			}
			return null;
		}


		@Override
		protected void onPostExecute(ArrayList<String> directions)
		{
			pDialog.dismiss();
			directionsArrayList = directions;
			Intent intent = new Intent(ListEventsActivity.this, DisplayDirectionsActivity.class);
			startActivityForResult(intent, DISPLAY_DIRECTIONS_RESULT_CODE);

		}
	}


	public Event getSelectedEvent(int index_of_selected_event)
	{
		if (MainActivity.type_of_event != null)
		{
			return MainActivity.sortedArrayList.get(index_of_selected_event);
		}
		return events_ArrayList.get(index_of_selected_event);

	}
}
