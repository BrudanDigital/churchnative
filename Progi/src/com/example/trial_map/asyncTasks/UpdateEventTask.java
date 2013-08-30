package com.example.trial_map.asyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.trial_map.beans.Event;
import com.example.trial_map.managers.EventManager;
import com.example.trial_map.managers.Manager;
import com.example.trial_map.managers.NetworkManager;

//updates given event in background thread using network to send data
public class UpdateEventTask extends AsyncTask<Event, String, Integer>
{
	private final CharSequence	PROGRESS_DIALOG_TEXT	= "Saving Edit. Please wait...";
	private Integer							result;
	private ProgressDialog			pDialog;
	Activity										anActivity;
	String											text;
	int													duration							= Toast.LENGTH_LONG;

//constructor
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
			result = EventManager.UpdateEvent(anEvent[0]);
			return result;
		}
		return EventManager.NO_CONNECTION;
	}


	@Override
	protected void onPostExecute(Integer integer)
	{

		// dismiss the progress dialog
		pDialog.dismiss();

		switch (integer)
		{
		// if event was saved
			case EventManager.SUCCESS:
				// set result
				anActivity.setResult(Activity.RESULT_OK);
				// close activity
				anActivity.finish();
				break;
			// if we failed to save event coz of server side error
			default:
				Toast.makeText(anActivity, Manager.MESSAGE, duration).show();
				break;
			
		}

	}
}