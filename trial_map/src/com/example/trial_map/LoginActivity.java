package com.example.trial_map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trial_map.beans.EventOwner;
import com.example.trial_map.factories.EventOwnerFactory;

public class LoginActivity extends Activity
{
	private static final int	SUCEESS				= 1;
	private static final int	FAILURE				= 0;
	private static EventOwner	anEventOwner	= null;
	private EditText					email_editText;
	private EditText					password_editText;
	private Button						login_button;
	

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// getting object of widgets
		email_editText = (EditText) findViewById(R.id.email_editText);
		password_editText = (EditText) findViewById(R.id.password_editText);
		login_button = (Button) findViewById(R.id.login_button);

		// add listener to button
		login_button.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				// get input
				String email = email_editText.getText().toString();
				String password = password_editText.getText().toString();
				// validate data

				// contact server in background thread
				String[] params = { email, password };
				LoginTask loginTask = new LoginTask();
				loginTask.execute(params);

			}
		});
	}

	private class LoginTask extends AsyncTask<String, Void, Integer>
	{
		private static final String	ILLEGAL_PARAMETER_TEXT		= "Neither Intent Nor EventOwner Can Be Null";
		private static final int		LONG_DURATION							= Toast.LENGTH_LONG;
		private final CharSequence	LOGIN_ERROR_MSG						= "Email Or Password Is Worng";
		private final CharSequence	LOGIN_PROGRESS_DIALOG_MSG	= "Logging In. Please wait...";
		private ProgressDialog			pDialog;

		@Override
		protected void onPreExecute()
		{
			// create progress dialog and display it to user
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage(LOGIN_PROGRESS_DIALOG_MSG);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Integer doInBackground(String... params)
		{
			anEventOwner = EventOwnerFactory.getEventOwner(params);
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
				Toast.makeText(LoginActivity.this, LOGIN_ERROR_MSG, LONG_DURATION).show();
				return;
			}
			// else return an okay result
			Intent intent = new Intent();
			sendEventOwner(intent, anEventOwner);
			setResult(RESULT_OK, intent);
			finish();
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
			boolean logged_in = anEventOwner.isLogged_in();
			Bundle data = new Bundle();
			data.putInt("user_id", user_id);
			data.putString("email", email);
			data.putString("password", password);
			data.putString("company_name", company_name);
			data.putString("location", company_location);
			data.putString("description", description);
			data.putBoolean("logged_in", logged_in);
			intent.putExtras(data);
		}
	}
}
