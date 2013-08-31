package com.example.trial_map;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.trial_map.asyncTasks.GetDirectionsTask;
import com.example.trial_map.beans.Contact;
import com.example.trial_map.beans.Event;
import com.example.trial_map.managers.ContactsManager;
import com.example.trial_map.managers.EventManager;
import com.example.trial_map.managers.Manager;
import com.google.android.gms.maps.model.LatLng;

public class EventDetailsActivity extends ActionBarActivity
{
	private static final int			EVENT_DETAILS_XML						= R.layout.event_details;
	private static final int			INVITE_CONTACTS							= R.id.menu_inviteContacts;
	private static final int			SEE_INVITED_CONTACTS				= R.id.menu_seeInvitedContacts;
	private static final int			GET_DIRECTIONS							= R.id.menu_getDirections;
	private static CharSequence		DIALOG_POSITIVE_BUTTON			= "Ok";
	private static CharSequence		DIALOG_NEGATIVE_BUTTON_TXT	= "Cancel";
	protected static final String	SELECT_MODE									= "SELECT";
	protected static final String	EDIT_MODE										= "EDIT";

	private static CharSequence		DIALOG_TITLE_TXT						= "Pick Contacts To Invite";

	// widgets
	private TextView							location_TextView;
	private TextView							name_TextView;
	private TextView							time_TextView;
	private TextView							duration_TextView;
	private TextView							date_TextView;
	private TextView							description_TextView;
	private TextView							type_TextView;
	private LinearLayout					layout;
	private RadioGroup						aRadioGroup;
	private RadioButton						yesRadioButton;
	private RadioButton						noRadioButton;
	private Event									anEvent;
	// private TextView totals_textView;
	// arraylist to keep the selected contacts
	ArrayList<Contact>						selectedContacts						= null;

	private AlertDialog						dialog											= null;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(EVENT_DETAILS_XML);
		// make background black
		getWindow().getDecorView().setBackgroundColor(Color.BLACK);

		// get objects of the widgets
		location_TextView = (TextView) findViewById(R.id.details_locationValue);
		name_TextView = (TextView) findViewById(R.id.details_title);
		// totals_textView = (TextView) findViewById(R.id.totals_title);
		type_TextView = (TextView) findViewById(R.id.details_typeValue);
		time_TextView = (TextView) findViewById(R.id.details_timeValue);
		duration_TextView = (TextView) findViewById(R.id.details_durationValue);
		date_TextView = (TextView) findViewById(R.id.details_dateValue);
		description_TextView = (TextView) findViewById(R.id.details_descriptionValue);
		layout = (LinearLayout) findViewById(R.id.heard_of_event_linearLayout);

		// get event picked by user
		anEvent = getEvent(getIntent());
		if (anEvent == null)
		{
			throw new IllegalStateException("Event Cant Be Null");
		}
		if (MainActivity.theUser != null)
		{
			aUser = MainActivity.theUser;
		}

		// display details of event
		showEventDetails(anEvent);
		displayLinearLayoutContent(layout);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// show action bar
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		// add menu options to the UI
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.layout.menu_invite_contacts, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem menu_item)
	{

		switch (menu_item.getItemId())
		{
			case BACK:
				setResult(RESULT_CANCELED);
				finish();
				return true;
			case INVITE_CONTACTS:
				GetAllContactsTask getAllContactsTask = new GetAllContactsTask();
				getAllContactsTask.execute();
				return true;
			case SEE_INVITED_CONTACTS:
				GetContactsInvitedToEventTask getContactsInvitedToEventTask = new GetContactsInvitedToEventTask();
				getContactsInvitedToEventTask.execute();
				return true;
			case GET_DIRECTIONS:
				LatLng destination = new LatLng(anEvent.getLatitude(), anEvent.getLongitude());
				GetDirectionsTask getDirectionsTask = new GetDirectionsTask(this);
				getDirectionsTask.execute(destination);
		}
		return super.onOptionsItemSelected(menu_item);
	}


	private void showSelectContactsDialog(final ArrayList<Contact> contactsList, final String MODE)
	{

		final CharSequence[] items = ContactsManager.getContactNames(contactsList);
		selectedContacts = new ArrayList<Contact>();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(DIALOG_TITLE_TXT);

		builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener()
		{
			// indexSelected contains the index of item (of which checkbox checked)
			@Override
			public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked)
			{
				if (isChecked)
				{
					// If the user checked the item, add it to the selected items
					// write your code when user checked the checkbox
					selectedContacts.add(contactsList.get(indexSelected));

				}
				else if (selectedContacts.contains(indexSelected))
				{
					// Else, if the item is already in the array, remove it
					// write your code when user Unchecked the checkbox
					selectedContacts.remove(Integer.valueOf(indexSelected));
				}
			}
		})
		// Set the action buttons
				.setPositiveButton(DIALOG_POSITIVE_BUTTON, new DialogInterface.OnClickListener()
				{
					@SuppressWarnings("unchecked")
					@Override
					public void onClick(DialogInterface dialogInterface, int id)
					{
						if (MODE.equalsIgnoreCase(SELECT_MODE))
						{// if user clicks invite contacts
							if (selectedContacts.size() <= 0)
							{
								displayToast(EventDetailsActivity.this, "Pliz Pick Contacts To Invite", LONG_DURATION);
								return;
							}
							if (we_already_have_user_number() != null)
							{
								// if we already have the users contact then we proceed to save
								// the invited contacts
								aUser = we_already_have_user_number();
								SaveInvitedContactsTask saveInvitedContactsTask = new SaveInvitedContactsTask();
								saveInvitedContactsTask.execute(selectedContacts);
							}
							else
							{// else we don't have the users contact
								// get the users contact then save
								showGetUserContactDialog();
								if (we_already_have_user_number() != null)
								{
									aUser = we_already_have_user_number();
									SaveInvitedContactsTask saveInvitedContactsTask = new SaveInvitedContactsTask();
									saveInvitedContactsTask.execute(selectedContacts);
								}

							}
						}
						else if (MODE.equalsIgnoreCase(EDIT_MODE))
						{// if user clicks see invited contacts
							dialogInterface.dismiss();
						}

					}
				}).setNegativeButton(DIALOG_NEGATIVE_BUTTON_TXT, new DialogInterface.OnClickListener()
				{
					@SuppressWarnings("unchecked")
					@Override
					public void onClick(DialogInterface dialogInterface, int id)
					{

						if (MODE.equalsIgnoreCase(SELECT_MODE))
						{// click cancel on invite contacts
							// Your code when user clicked on Cancel
							dialogInterface.dismiss();
						}
						else if (MODE.equalsIgnoreCase(EDIT_MODE))
						{// clicks un-invite on see invited contacts
							if (selectedContacts.size() <= 0)
							{
								displayToast(EventDetailsActivity.this, "Pliz Pick Contacts To Un-Invite", LONG_DURATION);
								return;
							}
							UnInviteSelectedContactsTask unInviteSelectedContactsTask = new UnInviteSelectedContactsTask();
							if (we_already_have_user_number() != null)
							{
								// if we already have the users contact then we proceed to save
								// the invited contacts
								aUser = we_already_have_user_number();
								unInviteSelectedContactsTask.execute(selectedContacts);
							}
							else
							{// else we don't have the users contact
								// get the users contact then save
								showGetUserContactDialog();
								if (we_already_have_user_number() != null)
								{
									aUser = we_already_have_user_number();
									unInviteSelectedContactsTask.execute(selectedContacts);
								}

							}

						}

					}
				});

		dialog = builder.create();// AlertDialog dialog; create like
															// this outside onClick
		dialog.show();
	}


	private void displayLinearLayoutContent(LinearLayout layout)
	{
		layout.removeAllViews();
		GetHeardOfEventStatus getHeardOfEventStatus = new GetHeardOfEventStatus();
		getHeardOfEventStatus.execute(layout);
	}


	/** returns event passed as parameters from previous activity **/
	private Event getEvent(Intent intent)
	{

		Bundle aBundle = intent.getExtras();
		if (aBundle != null)
		{
			Double latitude = aBundle.getDouble("latitude");
			Double longitude = aBundle.getDouble("longitude");
			String time = aBundle.getString("time");
			String date = aBundle.getString("date");
			String description = aBundle.getString("description");
			String name = aBundle.getString("name");
			String duration = aBundle.getString("duration");
			String location_in_words = aBundle.getString("location_in_words");
			int user_id = aBundle.getInt("user_id");
			int event_id = aBundle.getInt("event_id");
			String type = aBundle.getString("type");
			Boolean heard_of_event = aBundle.getBoolean("heard_of_event");
			int total_people_who_have_heard = aBundle.getInt("total_people_who_have_heard");
			return new Event(latitude, longitude, time, date, description, name, duration, location_in_words, user_id, event_id, type, heard_of_event, total_people_who_have_heard);
		}
		return null;
	}


	/** fills widgets with event details **/
	private void showEventDetails(Event anEvent)
	{
		location_TextView.setText(capitalizeFirstletter(anEvent.getEvent_location_in_words()));
		// int people_who_know_of_this_event =
		// anEvent.getTotal_people_who_have_heard();
		String name = anEvent.getName_of_event();
		// String total = "[" + people_who_know_of_this_event +
		// " people Have Heard Of This Event]";
		// totals_textView.setText(total.toUpperCase());
		name_TextView.setText(name.toUpperCase());
		type_TextView.setText(capitalizeFirstletter(anEvent.getType_of_event()));
		time_TextView.setText(anEvent.getTime());
		duration_TextView.setText(anEvent.getDuration());
		date_TextView.setText(anEvent.getDate());
		description_TextView.setText(capitalizeFirstletter(anEvent.getDescription_of_event()));

	}


	/** capitalizes the first letter of each word in given string **/
	private String capitalizeFirstletter(String aString)
	{
		return Manager.capitaliseFirstLetterOfEachWord(aString);
	}


	private class GetAllContactsTask extends AsyncTask<Void, Void, ArrayList<Contact>>
	{
		@Override
		protected void onPreExecute()
		{
			PROGRESS_DIALOG_TEXT = "Getting Contacts...";
			showProgressDialog();
		}


		@Override
		protected ArrayList<Contact> doInBackground(Void... arg0)
		{
			ArrayList<Contact> contactsList = ContactsManager.getAllContacts(EventDetailsActivity.this);
			;
			return contactsList;
		}


		@Override
		protected void onPostExecute(ArrayList<Contact> contactsList)
		{
			super.onPostExecute(contactsList);
			closeProgressDialog();
			DIALOG_TITLE_TXT = "Pick Contacts To Invite";
			DIALOG_POSITIVE_BUTTON = "Invite";
			DIALOG_NEGATIVE_BUTTON_TXT = "Cancel";
			showSelectContactsDialog(contactsList, SELECT_MODE);
		}
	}


	private class GetContactsInvitedToEventTask extends AsyncTask<Void, Void, ArrayList<Contact>>
	{

		@Override
		protected void onPreExecute()
		{
			PROGRESS_DIALOG_TEXT = "Getting Invited Contacts...";
			showProgressDialog();
		}


		@Override
		protected ArrayList<Contact> doInBackground(Void... arg0)
		{

			if (anEvent == null)
			{
				throw new IllegalArgumentException("EVENT CANT BE NULL");
			}

			if (we_already_have_user_number() != null)
			{
				aUser = we_already_have_user_number();
				ArrayList<Contact> contactsList = ContactsManager.getContactsInvitedToEvent(anEvent, aUser);
				return contactsList;
			}
			else
			{
				showGetUserContactDialog();
				aUser = we_already_have_user_number();
				ArrayList<Contact> contactsList = ContactsManager.getContactsInvitedToEvent(anEvent, aUser);
				return contactsList;
			}
		}


		@Override
		protected void onPostExecute(ArrayList<Contact> contactsList)
		{
			super.onPostExecute(contactsList);
			closeProgressDialog();
			displayToast(getApplicationContext(), Manager.MESSAGE, LONG_DURATION);
			if (contactsList.size() <= 0)
			{
				return;
			}
			DIALOG_TITLE_TXT = "Pick Contacts To UnInvite";
			DIALOG_POSITIVE_BUTTON = "Ok";
			DIALOG_NEGATIVE_BUTTON_TXT = "Un-Invite";
			showSelectContactsDialog(contactsList, EDIT_MODE);

		}
	}


	private class SaveInvitedContactsTask extends AsyncTask<ArrayList<Contact>, Void, Integer>
	{

		@Override
		protected void onPreExecute()
		{
			PROGRESS_DIALOG_TEXT = "Saving Invited Contacts...";
			showProgressDialog();
		}


		@Override
		protected Integer doInBackground(ArrayList<Contact>... contacts)
		{
			if (aUser == null)
			{
				throw new IllegalArgumentException("aUser cant be Null");
			}
			return ContactsManager.saveInvitedContacts(contacts[0], aUser, anEvent);
		}


		@Override
		protected void onPostExecute(Integer result)
		{
			super.onPostExecute(result);
			closeProgressDialog();
			switch (result)
			{
				case Manager.SUCCESS:
					displayToast(getApplicationContext(), Manager.MESSAGE, SHORT_DURATION);
					break;

				default:
					displayToast(getApplicationContext(), Manager.MESSAGE, LONG_DURATION);
					break;
			}

		}
	}


	private class UnInviteSelectedContactsTask extends AsyncTask<ArrayList<Contact>, Void, Integer>
	{

		@Override
		protected void onPreExecute()
		{
			PROGRESS_DIALOG_TEXT = "Saving Invited Contacts...";
			showProgressDialog();
		}


		@Override
		protected Integer doInBackground(ArrayList<Contact>... contacts)
		{
			if (aUser == null)
			{
				throw new IllegalArgumentException("aUser cant be Null");
			}
			return ContactsManager.deleteInvitedContacts(contacts[0], anEvent, aUser);
		}


		@Override
		protected void onPostExecute(Integer result)
		{
			super.onPostExecute(result);
			closeProgressDialog();
			switch (result)
			{
				case Manager.SUCCESS:
					displayToast(getApplicationContext(), Manager.MESSAGE, SHORT_DURATION);
					break;

				default:
					displayToast(getApplicationContext(), Manager.MESSAGE, LONG_DURATION);
					break;
			}

		}
	}


	private class GetHeardOfEventStatus extends AsyncTask<LinearLayout, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground(LinearLayout... layouts)
		{
			LinearLayout layout = layouts[0];
			layout.removeAllViews();
			if (anEvent.getHeard_of_this_event_status())
			{
				return true;
			}
			return false;
		}


		@Override
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);
			// displayToast(EventDetailsActivity.this, "result=" + result,
			// LONG_DURATION);
			if (!result)
			{
				displayRadioButtons(layout);
				return;
			}
			displayAlreadyVotedMessage(layout);
		}


		private void displayRadioButtons(LinearLayout layout)
		{
			aRadioGroup = new RadioGroup(getApplicationContext());
			yesRadioButton = new RadioButton(getApplicationContext());
			noRadioButton = new RadioButton(getApplicationContext());
			Button aButton = new Button(getApplicationContext());

			aButton.setText("SUBMIT");
			aButton.setHeight(10);
			aButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
				{
					SaveHeardOfEventStatusTask saveHeardOfEventStatusTask = new SaveHeardOfEventStatusTask();
					saveHeardOfEventStatusTask.execute();
					anEvent.setHeard_of_this_event_status(true);
				}
			});
			yesRadioButton.setText("YES");
			yesRadioButton.setChecked(true);
			noRadioButton.setText("NO");

			aRadioGroup.addView(yesRadioButton);
			aRadioGroup.addView(noRadioButton);

			layout.addView(aRadioGroup);
			layout.addView(aButton);

		}


		public void displayAlreadyVotedMessage(LinearLayout layout)
		{
			TextView aTextView = new TextView(getApplicationContext());
			aTextView.setText("You Know About This Event");
			aTextView.setTextColor(Color.YELLOW);
			aTextView.setGravity(Gravity.CENTER);
			layout.addView(aTextView);

		}
	}


	private class SaveHeardOfEventStatusTask extends AsyncTask<Void, Void, Boolean>
	{

		@Override
		protected void onPreExecute()
		{
			PROGRESS_DIALOG_TEXT = "Saving...";
			showProgressDialog();
		}


		@Override
		protected Boolean doInBackground(Void... params)
		{
			if (noRadioButton.isChecked())
			{
				return true;
			}
			if (EventManager.saveHeardOfStatus(anEvent.getEvent_id()))
			{
				return true;
			}
			return false;
		}


		@Override
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);
			closeProgressDialog();
			if (result)
			{
				setResult(RESULT_OK);
				finish();
				return;
			}
			displayToast(getApplicationContext(), "Failed to Save Status", LONG_DURATION);
		}
	}
}
