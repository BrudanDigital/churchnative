package com.example.trial_map;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.ProgressDialog;
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

//This class displays all events in 10km as a list for user to select from
public class ListEventsActivity extends SherlockListActivity
{
	private static final int	DETAILS_ACTIVITY_RESULT_CODE	= 100;
	ArrayList<Event>					events_ArrayList							= EventsFactory.getEventsIn10KmRadius();
	private View							view													= null;
	private int								index_of_selected_event				= -1;
	QuickAction								mQuickAction									= null;
	GetEventDetailsTask				getEventDetailsTask;
	private static final int	BACK													= R.id.menu_back;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// make background black
		getWindow().getDecorView().setBackgroundColor(Color.BLACK);
		// Add action item
		ActionItem action_details = new ActionItem();

		action_details.setTitle("Details");
		action_details.setIcon(getResources().getDrawable(R.drawable.event_details));

		// Accept action item
		ActionItem action_delete = new ActionItem();

		action_delete.setTitle("Delete");
		action_delete.setIcon(getResources().getDrawable(R.drawable.delete_event));

		// Upload action item
		ActionItem action_edit = new ActionItem();

		action_edit.setTitle("Edit");
		action_edit.setIcon(getResources().getDrawable(R.drawable.edit_event));

		mQuickAction = new QuickAction(this);

		mQuickAction.addActionItem(action_details);
		mQuickAction.addActionItem(action_delete);
		mQuickAction.addActionItem(action_edit);

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
				{ // Accept item selected
					Toast.makeText(ListEventsActivity.this, "DELETE item selected", Toast.LENGTH_SHORT).show();
				}
				else if (pos == 2)// if edit event is clicked
				{ // Upload item selected
					Toast.makeText(ListEventsActivity.this, "EDIT selected", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// set list adapter to my customer adapter
		setListAdapter(new CustomArrayAdapter(this, getEventsAsStringArray(),events_ArrayList));
	}

	// handler for click on an list view item by user
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		mQuickAction.show(v);
		mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
		// if there exist another view already[user clicked on item then clicked on
		// another]
		if (view != null)
		{
			// return original views colour to black
			view.setBackgroundColor(Color.BLACK);
		}
		// make view global
		view = v;
		v.setBackgroundColor(getResources().getColor(R.color.orange));
		index_of_selected_event = position;
	}

	// handle results returned by sub activities
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == DETAILS_ACTIVITY_RESULT_CODE)
		{
			if (view != null)
			{
				view.setBackgroundColor(Color.BLACK);
				view = null;
			}
		}
	}

	// breaks event into primitives so data can be sent to activity
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
		int user_id = anEvent.getUser_id();
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

//returns event descriptions in string array
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

	// method called to create menu and its items
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

	// handler for click on menu item
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
			pDialog.setCancelable(false);
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

}
