package com.example.trial_map.asyncTasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.trial_map.factories.NetworkManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class DrawRouteTask extends AsyncTask<String, String, List<List<HashMap<String, String>>>>
{
	private static final String									PROGRESS_DIALOG_TEXT		= "Drawing Route";
	private static final String									ILLEGAL_PARAMETER_TEXT	= "PARAMETER CANNOT BE NULL";
	private ProgressDialog											pDialog									= null;
	private JSONObject													jObject									= null;
	private List<List<HashMap<String, String>>>	routes									= null;
	Activity																		anActivity							= null;
	GoogleMap																		aMap										= null;


	public DrawRouteTask(Activity anActivity, GoogleMap map)
	{
		this.anActivity = anActivity;
		aMap = map;
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


	// Downloading data in non-ui thread
	@Override
	protected List<List<HashMap<String, String>>> doInBackground(String... url)
	{

		// For storing data from web service
		String data = "";
		try
		{
			// Fetching the data from web service
			data = NetworkManager.downloadJSONdata(url[0]);
			System.out.println(data);
			// get JsonObject from data
			jObject = new JSONObject(data);
			// Starts parsing the object to get route
			routes = NetworkManager.DirectionsParser(jObject);

		}
		catch (Exception e)
		{

		}
		return routes;
	}


	// draws route on the map using downloaded directions
	@Override
	protected void onPostExecute(List<List<HashMap<String, String>>> route)
	{
		super.onPostExecute(route);
		if (route != null)
		{
			drawRoute(route);
			pDialog.dismiss();
			return;
		}
		// no route has been found[most likely network issues]
		pDialog.dismiss();
		Toast.makeText(anActivity, "Failed To Get Directions!![check your internet connections]", Toast.LENGTH_LONG).show();

	}


	/**
	 * @param route
	 *          to be drawn on the map draws route on the map using downloaded
	 *          directions
	 */
	private void drawRoute(List<List<HashMap<String, String>>> route)
	{
		if (route == null)
		{
			throw new IllegalArgumentException(ILLEGAL_PARAMETER_TEXT);
		}
		ArrayList<LatLng> points = null;
		PolylineOptions lineOptions = null;
		String distance = "";
		String duration = "";

		if (route.size() < 1)
		{
			return;
		}

		// Traversing through all the routes
		for (int i = 0; i < route.size(); i++)
		{
			points = new ArrayList<LatLng>();
			lineOptions = new PolylineOptions();

			// Fetching i-th route
			List<HashMap<String, String>> path = route.get(i);

			// Fetching all the points in i-th route
			for (int j = 0; j < path.size(); j++)
			{
				HashMap<String, String> point = path.get(j);

				if (j == 0)
				{ // Get distance from the list
					distance = point.get("distance");
					continue;
				}
				else if (j == 1)
				{ // Get duration from the list
					duration = point.get("duration");
					continue;
				}
				double lat = Double.parseDouble(point.get("lat"));
				double lng = Double.parseDouble(point.get("lng"));
				LatLng position = new LatLng(lat, lng);
				points.add(position);
			}

			// Adding all the points in the route to LineOptions
			lineOptions.addAll(points);
			lineOptions.width(4);
			lineOptions.color(Color.RED);
		}

		System.out.println("Distance:" + distance + ", Duration:" + duration);

		// Drawing polyline in the Google Map for the i-th route
		aMap.addPolyline(lineOptions);

	}
}