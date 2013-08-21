package com.example.trial_map;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**This is a SuperClass for most activities that share these common methods**/
/**This class cannot be instantiated but can be extended**/
/** it adds the sherlock action bar to activities **/
public abstract class ActionBarActivity extends SherlockActivity
{
	protected static final int	BACK									= R.id.menu_back;
	protected static boolean		isInForeGround				= false;
	public static final int			SHORT_DURATION				= Toast.LENGTH_SHORT;
	public static final int			LONG_DURATION					= Toast.LENGTH_LONG;
	protected static String			PROGRESS_DIALOG_TEXT	= "Getting Details Input...";
	protected ProgressDialog		pDialog;


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
		pDialog = new ProgressDialog(this);
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
			pDialog=null;
		}
	}
}
