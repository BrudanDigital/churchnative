package com.example.trial_map;

import android.content.Context;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ActionBarActivity extends SherlockActivity
{
	private static final int	BACK						= R.id.menu_back;
	private static boolean		isInForeGround	= false;


	protected void onResume()
	{
		super.onResume();
		isInForeGround = true;
	}


	@Override
	protected void onPause()
	{
		super.onPause();
		isInForeGround = false;
	}


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

}
