package com.example.trial_map.asyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.trial_map.beans.Event;
import com.example.trial_map.factories.EventsFactory;
import com.example.trial_map.factories.NetworkManager;

public class UpdateEventTask extends AsyncTask<Event, String, Integer>
{
	private final CharSequence	PROGRESS_DIALOG_TEXT	= "Saving Edit. Please wait...";
	private Integer							result;
	private ProgressDialog			pDialog;
	Activity										anActivity;
	String											text;
	int													duration							= Toast.LENGTH_LONG;

	public UpdateEventTask(Activity anActivity)
	{
		this.anActivity = anActivity;
	}

	@Override
	protected void onPreExecute()
	{
		// create progress dialog and display it to user
		pDialog = new ProgressDialog(anActivity);
		pDialog.setMessage(PROGRESS_DIALOG_TEXT);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}

	@Override
	protected Integer doInBackground(Event... anEvent)
	{
		if (NetworkManager.isInternetAvailable(anActivity))
		{
		// save the event
			result = EventsFactory.UpdateEvent(anEvent[0]);
			return result;
		}
		return EventsFactory.NO_CONNECTION;
	}

	@Override
	protected void onPostExecute(Integer integer)
	{

		// dismiss the progress dialog
		pDialog.dismiss();

		switch (integer)
		{
		// if event was saved
			case EventsFactory.SUCCESS:
				// set result
				anActivity.setResult(Activity.RESULT_OK);
				// close activity
				anActivity.finish();
				break;
			// if we failed to save event coz of server side error
			case EventsFactory.FAILURE:
				text = "Failed To Save Event";
				Toast.makeText(anActivity, text, duration).show();
				break;
			// if there is no Internet connection to server
			case EventsFactory.NO_CONNECTION:
				text = "Sorry but there is no connection to the server";
				Toast.makeText(anActivity, text, duration).show();
				break;
		}

	}
}