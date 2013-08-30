package com.example.trial_map.managers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class LocationManager extends Manager
{
	
	private static final String				TAG_RESULTS							= "results";
	private static final String				TAG_STATUS							= "status";
	
	/** returns the latitude and longitude of location chosen by user **/
	public static LatLng getLatLngOfLocationInputByUser(String location_in_words)
	{
		// replace all commas in the input with a + sign
		location_in_words = location_in_words.replaceAll(",", "+");
		// replace all spaces in the input with a %20 sign
		location_in_words = location_in_words.replaceAll(" ", "%20");
		// finally generate address parameter
		String address = "address=" + location_in_words;
		// set required sensor parameter
		String sensor = "sensor=true";
		// set parameters into 1 string
		String paramaters = sensor + "&" + address;
		// generate url
		String url = GEOCODE_ADDRESS + "?" + paramaters;

		new NetworkManager();
		// make the request to google
		JSONObject jsonObject = NetworkManager.makeHttpGetRequest(url);

		try
		{// to get latitude and longitude
			double latitude;
			double longitude;
			String status = jsonObject.getString(TAG_STATUS);
			if (status.equalsIgnoreCase("ok"))
			{
				// Getting Array of results
				JSONArray results = jsonObject.getJSONArray(TAG_RESULTS);
				// get results object from result array
				JSONObject result_components = results.getJSONObject(0);
				// get geometry object from json results
				JSONObject geometery = result_components.getJSONObject("geometry");
				// get location object from json geometry
				JSONObject location = geometery.getJSONObject("location");
				// finally read the latitude and longitude values from object
				latitude = location.getDouble("lat");
				longitude = location.getDouble("lng");
				// return new latlng object
				return new LatLng(latitude, longitude);
			}
			else
			{// there are no results from google places
				MESSAGE="FAILED TO GEOCODE GIVEN LOCATION";
				return null;
			}

		}
		catch (JSONException e)
		{
			MESSAGE=NO_CONNECTION_MESSAGE;
			return null;
			// Log.e("JSON Parser", "" + e.getMessage());
		}
		
	}
	
	
}
