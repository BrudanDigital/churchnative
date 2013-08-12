package com.example.trial_map;

import android.app.ProgressDialog;
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

import com.example.trial_map.beans.Event;
import com.example.trial_map.factories.EventsFactory;

public class EventDetailsActivity extends ActionBarActivity
{
	private static final int	eventDetailsScreen	= R.layout.event_details;
	// widgets
	private TextView					location_TextView;
	private TextView					name_TextView;
	private TextView					time_TextView;
	private TextView					duration_TextView;
	private TextView					date_TextView;
	private TextView					description_TextView;
	private TextView					type_TextView;
	private LinearLayout			layout;
	private RadioGroup				aRadioGroup;
	private RadioButton				yesRadioButton;
	private RadioButton				noRadioButton;
	private Event							anEvent;
	private TextView	totals_textView;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(eventDetailsScreen);
		// make background black
		getWindow().getDecorView().setBackgroundColor(Color.BLACK);

		// get objects of the widgets
		location_TextView = (TextView) findViewById(R.id.details_locationValue);
		name_TextView = (TextView) findViewById(R.id.details_title);
		totals_textView = (TextView) findViewById(R.id.totals_title);
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

		// display details of event
		showEventDetails(anEvent);
		displayLinearLayoutContent(layout);

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
			return new Event(latitude, longitude, time, date, description, name, duration, location_in_words, user_id, event_id, type);
		}
		return null;
	}


	/** fills widgets with event details **/
	private void showEventDetails(Event anEvent)
	{
		location_TextView.setText(capitalizeFirstletter(anEvent.getEvent_location_in_words()));
		int people_who_know_of_this_event=EventsFactory.getTotalOfPeopleWhoHaveHeard(anEvent.getEvent_id());
		String name=anEvent.getName_of_event();
		String total="["+people_who_know_of_this_event+" people Have Heard Of This Event]";
		totals_textView.setText(total.toUpperCase());
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
		return ListEventsActivity.capitaliseFirstLetterOfEachWord(aString);
	}


	private class GetHeardOfEventStatus extends AsyncTask<LinearLayout, Void, Boolean>
	{
		// private final CharSequence GET_STATUS_PROGRESS_DIALOG_MSG = "Getting St";
		private ProgressDialog	pDialog;
		
		@Override
		protected void onPreExecute()
		{
			// create progress dialog and display it to user
			pDialog = new ProgressDialog(EventDetailsActivity.this);
			// pDialog.setMessage(GET_STATUS_PROGRESS_DIALOG_MSG);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}


		@Override
		protected Boolean doInBackground(LinearLayout... layouts)
		{
			LinearLayout layout = layouts[0];
			layout.removeAllViews();
			if (EventsFactory.UserHasHeardOfEvent(anEvent.getEvent_id()))
			{
				return true;
			}
			return false;
		}


		@Override
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);
			pDialog.dismiss();
			displayToast(EventDetailsActivity.this, "result=" + result, LONG_DURATION);
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
		private final CharSequence	SAVE_STATUS_PROGRESS_DIALOG_MSG	= "Saving...";
		private ProgressDialog			pDialog;


		@Override
		protected void onPreExecute()
		{
			pDialog = new ProgressDialog(EventDetailsActivity.this);
			pDialog.setMessage(SAVE_STATUS_PROGRESS_DIALOG_MSG);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}


		@Override
		protected Boolean doInBackground(Void... params)
		{
			if (noRadioButton.isChecked())
			{
				return true;
			}
			if (EventsFactory.saveHeardOfStatus(anEvent.getEvent_id()))
			{
				return true;
			}
			return false;
		}


		@Override
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);
			pDialog.dismiss();
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
