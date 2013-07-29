package com.example.trial_map;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ActionBarActivity extends SherlockActivity
{
	private static final int	BACK	= R.id.menu_back;


	/** method called to create menu and its items**/
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


	/** handler for click on menu item**/
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
