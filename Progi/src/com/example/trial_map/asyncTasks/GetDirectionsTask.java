package com.example.trial_map.asyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

/**
 * async task that uses network to get directions in words to destination
 * 
 */
public  class GetDirectionsTask extends AsyncTask<LatLng, String, Intent>
{
	private final String		PROGRESS_DIALOG_TEXT	= "Getting Directions...";
	private ProgressDialog	pDialog;
	Activity activity;

	public GetDirectionsTask(Activity anActivity)
	{
		this.activity=anActivity;
	}

	@Override
	protected void onPreExecute()
	{
		// create progress dialog and display it to user
		pDialog = new ProgressDialog(activity);
		pDialog.setMessage(PROGRESS_DIALOG_TEXT);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}


	@Override
	protected Intent doInBackground(LatLng... latLngs)
	{
		if (latLngs==null)
		{
			throw new IllegalArgumentException("PARAMETER CANT BE NULL");
		}
			LatLng dest = latLngs[0];		
			String navigation_url="google.navigation:ll="+dest.latitude+","+dest.longitude;
			Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(navigation_url));		
			return intent;
	}


	@Override
	protected void onPostExecute(Intent anIntent)
	{
		pDialog.dismiss();
		activity.startActivity(anIntent);
		activity.finish();

	}
}