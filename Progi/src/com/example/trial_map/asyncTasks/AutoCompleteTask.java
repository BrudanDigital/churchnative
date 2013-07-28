package com.example.trial_map.asyncTasks;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import com.example.trial_map.factories.NetworkManager;

public class AutoCompleteTask extends AsyncTask<String, Void, SimpleAdapter>
{

	private static final String	COUNTRY								= "ug";
	private static final String	PLACES_API_BASE				= "https://maps.googleapis.com/maps/api/place/autocomplete/";
	private static final String	GOOGLE_PLACES_API_KEY	= "AIzaSyCN1vdOEKhXyHSM0IvanKE6FYFoUaWjAPA";
	Activity anActivity=null;
	AutoCompleteTextView anAutoCompleteTextView;
	
	public AutoCompleteTask(Activity activity,AutoCompleteTextView autoCompleteTextView)
	{
		this.anActivity=activity;
		this.anAutoCompleteTextView=autoCompleteTextView;
	}
	
	@Override
	protected SimpleAdapter doInBackground(String... place)
	{
		// For storing data from web service
		String data = "";
		//list for storing places
		List<HashMap<String, String>> places = null;
		try
		{

			String url = getGooglePlaesUrl(place[0]);
			// Fetching the data from we service
			data = NetworkManager.downloadJSONdata(url);
			
			//parser for json
			NetworkManager jsonParser = new NetworkManager();

			JSONObject jObject = new JSONObject(data);

			// Getting the parsed data as a List construct
			places = jsonParser.parseGooglePlaces(jObject);
			
			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(anActivity.getBaseContext(), places, android.R.layout.simple_list_item_1, from, to);

			return adapter;
		}
		catch (Exception e)
		{

		}
		return null;
	}

	@Override
	protected void onPostExecute(SimpleAdapter simpleAdapter)
	{
		if (simpleAdapter==null)
		{
			return;
		}
		super.onPostExecute(simpleAdapter);
		anAutoCompleteTextView.setAdapter(simpleAdapter);
	}

	private String getGooglePlaesUrl(String place)
	{
		if (place == null)
		{
			throw new IllegalArgumentException("place cannot be null");
		}
		try
		{
			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=" + GOOGLE_PLACES_API_KEY;

			String input = "";

			input = "input=" + URLEncoder.encode(place, "utf-8");

			String country = "components=country:" + COUNTRY;
			// Sensor enabled
			String sensor = "sensor=true";

			// Building the parameters to the web service
			String parameters = input + "&" + country + "&" + sensor + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = PLACES_API_BASE + output + "?" + parameters;

			return url;
		}
		catch (Exception e)
		{
			Log.e("PLACES URL", e.getMessage());
			return null;
		}
	}
}