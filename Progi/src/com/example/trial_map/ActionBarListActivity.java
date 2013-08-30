package com.example.trial_map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.trial_map.beans.Contact;
import com.example.trial_map.beans.User;
import com.example.trial_map.managers.ContactsManager;

/**This is a SuperClass for most activities that share these common methods**/
/**This class cannot be instantiated but can be extended**/
/** it adds the sherlock action bar to activities **/
public abstract class ActionBarListActivity extends SherlockListActivity
{
	protected static final int	BACK									= R.id.menu_back;
	protected static boolean		isInForeGround				= false;
	public static final int			SHORT_DURATION				= Toast.LENGTH_SHORT;
	public static final int			LONG_DURATION					= Toast.LENGTH_LONG;
	protected static String			PROGRESS_DIALOG_TEXT	= "Getting Details Input...";
	protected ProgressDialog		pDialog;
	User												aUser;


	/** called when app is resuming **/
	protected void onResume()
	{
		super.onResume();
		isInForeGround = true;
	}


	/** Called when user navigates away from activity **/
	@Override
	protected void onPause()
	{
		super.onPause();
		isInForeGround = false;
	}


	/** displays Toast messages only when activity is in foreground **/
	public static void displayToast(Context aContext, String text, int duration)
	{
		if (isInForeGround)
		{
			Toast.makeText(aContext, text, duration).show();
		}
	}


	/** method called to create menu and its items **/
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// show action bar
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		// add menu options to the UI
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.layout.menu_item_back, menu);
		return true;
	}


	/** handler for click on menu item **/
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


	/** shows a progress dialog to the user **/
	protected void showProgressDialog()
	{
		// create progress dialog and display it to user
		pDialog = new ProgressDialog(ActionBarListActivity.this);
		pDialog.setMessage(PROGRESS_DIALOG_TEXT);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}


	/** closes an open progress dialog **/
	protected void closeProgressDialog()
	{
		if (pDialog != null)
		{
			pDialog.dismiss();
			pDialog = null;
		}
	}


	protected boolean we_already_have_user_number(User aUser)
	{
		if (MainActivity.theUser != null)
		{
			aUser = MainActivity.theUser;
			return true;
		}
		SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		String name = preferences.getString("name", null);
		String number = preferences.getString("number", null);
		if (name == null || number == null)
		{
			return false;
		}
		aUser = new User(new Contact(name, number));
		return true;
	}


	protected void showGetUserContactDialog()
	{
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(getBaseContext());
		View promptsView = li.inflate(R.layout.get_user_contact_prompt, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompts.xml to alert dialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userPhoneNumber = (EditText) promptsView.findViewById(R.id.getUserContact_userPhoneNumber);
		final EditText userName = (EditText) promptsView.findViewById(R.id.getUserContact_username);

		// set dialog message
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// get user input and set it to result
				// edit text
				String user_name = userName.getText().toString();
				String phone_number = userPhoneNumber.getText().toString();
				Contact usersContact = new Contact(user_name, phone_number);
				aUser = new User(usersContact);
				ContactsManager.saveUsersContactLocaly(usersContact,ActionBarListActivity.this);
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}


	}
