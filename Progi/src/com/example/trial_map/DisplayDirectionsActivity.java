package com.example.trial_map;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trial_map.factories.NetworkManager;
import com.google.android.gms.maps.model.LatLng;

public class DisplayDirectionsActivity extends ActionBarActivity
{
	private TextView	directionsTextView;
	private Button		drawRouteBtn;
	LatLng						dest;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_directions);
		directionsTextView = (TextView) findViewById(R.id.directions_textView);
		drawRouteBtn = (Button) findViewById(R.id.directions_drawMapBtn);
		drawRouteBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				MainActivity.dest = dest;
				setResult(RESULT_OK);
				finish();
			}
		});
		GetDirectionsTask getDirectionsTask = new GetDirectionsTask();
		getDirectionsTask.execute();
	}


	/**
	 * @return returns a latlng object containing users current location
	 */
	private LatLng getCurrentLocation()
	{
		return new LatLng(MainActivity.user_latitude, MainActivity.user_longitude);
	}


	/**
	 * @return a latlng object containing the destination cordinates
	 */
	private LatLng getDestination()
	{
		Bundle aBundle = getIntent().getExtras();
		Double latitude = aBundle.getDouble("lat");
		Double longitude = aBundle.getDouble("long");
		return new LatLng(latitude, longitude);
	}


	/**
	 * async task that uses network to get directions in words to destination
	 *
	 */
	private class GetDirectionsTask extends AsyncTask<Void, String, ArrayList<String>>
	{
		private final String		PROGRESS_DIALOG_TEXT	= "Getting Directions...";
		private ProgressDialog	pDialog;


		protected void onPreExecute()
		{
			// create progress dialog and display it to user
			pDialog = new ProgressDialog(DisplayDirectionsActivity.this);
			pDialog.setMessage(PROGRESS_DIALOG_TEXT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}


		@Override
		protected ArrayList<String> doInBackground(Void... arg0)
		{
			try
			{
				dest = getDestination();
				LatLng origin = getCurrentLocation();
				String url = NetworkManager.getDirectionsUrl(origin, dest);
				Log.e("URL", url);
				String json = NetworkManager.downloadJSONdata(url);
				Log.e("DATA", json);
				JSONObject jsonObject = new JSONObject(json);
				ArrayList<String> directions = NetworkManager.DrivingDirectionsParser(jsonObject);
				return directions;
			}
			catch (Exception e)
			{

			}
			return null;
		}


		@Override
		protected void onPostExecute(ArrayList<String> directions)
		{
			if (directions != null)
			{
				super.onPostExecute(directions);
				Iterator<String> iterator = directions.iterator();
				String directionsString = null;
				while (iterator.hasNext())
				{
					if (directionsString != null)
					{
						directionsString = directionsString.concat((String) iterator.next() + ".\n");
						Log.e("DIRECTIONS", directionsString);
						continue;
					}

					directionsString = ((String) iterator.next()) + ".\n";
					Log.e("DIRECTIONS", directionsString);
				}
				directionsTextView.setText(Html.fromHtml(directionsString).toString().toUpperCase());
				pDialog.dismiss();
				return;
			}
			pDialog.dismiss();
			Toast.makeText(DisplayDirectionsActivity.this, "Failed To Get Directions", Toast.LENGTH_LONG).show();
		}
	}
}
