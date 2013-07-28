package com.example.trial_map.asyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.trial_map.beans.EventOwner;
import com.example.trial_map.factories.EventOwnerFactory;

public class LoginTask extends AsyncTask<String, Void, Integer>
{
	private static final String	ILLEGAL_PARAMETER_TEXT		= "Neither Intent Nor EventOwner Can Be Null";
	private static final int		LONG_DURATION							= Toast.LENGTH_LONG;
	private static final int		FAILURE										= 0;
	private static final int		SUCEESS										= 1;
	private final CharSequence	LOGIN_ERROR_MSG						= "Cannot Login:Email Or Password Is Wrong";
	private final CharSequence	LOGIN_PROGRESS_DIALOG_MSG	= "Logging In. Please wait...";
	private ProgressDialog			pDialog;
	Activity										anActivity;
	private EventOwner					anEventOwner;

	public LoginTask(Activity anActivity)
	{
		this.anActivity = anActivity;
	}

	@Override
	protected void onPreExecute()
	{
		// create progress dialog and display it to user
		pDialog = new ProgressDialog(anActivity);
		pDialog.setMessage(LOGIN_PROGRESS_DIALOG_MSG);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}

	@Override
	protected Integer doInBackground(String... params)
	{
		anEventOwner = EventOwnerFactory.getEventOwner(params[0], params[1]);
		if (anEventOwner == null)
		{
			return FAILURE;
		}
		else
		{
			return SUCEESS;
		}
	}

	@Override
	protected void onPostExecute(Integer result)
	{
		super.onPostExecute(result);
		// dismiss dialog box
		pDialog.dismiss();

		if (result == FAILURE)
		{// if wrong email or password then inform user
			Toast.makeText(anActivity, LOGIN_ERROR_MSG, LONG_DURATION).show();
			return;
		}
		// else return an okay result
		Intent intent = new Intent();
		sendEventOwner(intent, anEventOwner);
		anActivity.setResult(Activity.RESULT_OK, intent);
		anActivity.finish();
	}

	private void sendEventOwner(Intent intent, EventOwner anEventOwner)
	{
		if (intent == null || anEventOwner == null)
		{
			throw new IllegalArgumentException(ILLEGAL_PARAMETER_TEXT);
		}
		int user_id = anEventOwner.getUser_id();
		String email = anEventOwner.getEmail();
		String password = anEventOwner.getPassword();
		String company_name = anEventOwner.getCompany_name();
		String company_location = anEventOwner.getCompany_location();
		String description = anEventOwner.getDescription_of_services();
		Bundle data = new Bundle();
		data.putInt("user_id", user_id);
		data.putString("email", email);
		data.putString("password", password);
		data.putString("company_name", company_name);
		data.putString("location", company_location);
		data.putString("description", description);
		intent.putExtras(data);
	}
}
