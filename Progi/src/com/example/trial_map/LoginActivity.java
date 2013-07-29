package com.example.trial_map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trial_map.asyncTasks.LoginTask;
import com.example.trial_map.factories.Validator;

/**this class logs a user in**/
public class LoginActivity extends Activity
{
	private EditText	email_editText;
	private EditText	password_editText;
	private Button		login_button;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// make dialog fill screen
		LayoutParams params = getWindow().getAttributes();
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(params);

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
				String email = email_editText.getText().toString().trim();
				String password = password_editText.getText().toString().trim();
				// validate data
				if (UserInputisValid(email,password))
				{
					// contact server in background thread
					String[] params = { email, password };
					LoginTask loginTask = new LoginTask(LoginActivity.this);
					loginTask.execute(params);
				}
				else
				{
					Toast.makeText(LoginActivity.this, "InValid Email Or Password", Toast.LENGTH_LONG).show();
				}

			}

			private boolean UserInputisValid(String email,String password)
			{
				if (Validator.isNullOrEmpty(email)||Validator.isNullOrEmpty(password))
				{
					return false;
				}
				if (!Validator.isValidEmail(email))
				{
					return false;
				}
				return true;
			}
		});
	}

}
