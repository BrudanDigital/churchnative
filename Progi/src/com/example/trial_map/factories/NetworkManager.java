package com.example.trial_map.factories;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkManager
{
	private static final String	OUTPUT							= "json";
	private static final String	DIRECTIONS_API_URL	= "https://maps.googleapis.com/maps/api/directions/" + OUTPUT + "?";
	static InputStream					is									= null;
	static JSONObject						jObj								= null;
	static String								json								= "";


	/** constructor**/
	public NetworkManager()
	{

	}


	/**function get json from url by making HTTP POST or GET mehtod**/
	public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params)
	{

		// Making HTTP request
		try
		{

			// check for request method
			if (method == "POST")
			{
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			}
			else if (method == "GET")
			{
				// request method is GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line);
			}
			is.close();
			json = sb.toString();
		}
		catch (Exception e)
		{
			//Log.e("Buffer Error", "[" + json + "]");
		}

		// try parse the string to a JSON object
		try
		{
			jObj = new JSONObject(json);
		}
		catch (JSONException e)
		{
			//Log.e("JSON Parser", "[" + json + "]");

		}

		// return JSON String
		return jObj;

	}


	/**
	 * Receives a JSONObject and returns a list of lists containing latitude and
	 * longitude
	 */
	public static List<List<HashMap<String, String>>> DirectionsParser(JSONObject jObject)
	{
		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
		String jStatus = null;
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;
		JSONObject jDistance = null;
		JSONObject jDuration = null;

		try
		{
			jStatus = jObject.getString("status");
			if (jStatus.equalsIgnoreCase("ok"))
			{
				jRoutes = jObject.getJSONArray("routes");
				/** Traversing all routes */
				for (int i = 0; i < jRoutes.length(); i++)
				{
					jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
					List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
					/** Traversing all legs */
					for (int j = 0; j < jLegs.length(); j++)
					{
						/** Getting distance from the json data */
						jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
						HashMap<String, String> hmDistance = new HashMap<String, String>();
						hmDistance.put("distance", jDistance.getString("text"));

						/** Getting duration from the json data */
						jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
						HashMap<String, String> hmDuration = new HashMap<String, String>();
						hmDuration.put("duration", jDuration.getString("text"));

						/** Adding distance object to the path */
						path.add(hmDistance);

						/** Adding duration object to the path */
						path.add(hmDuration);

						jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

						/** Traversing all steps */
						for (int k = 0; k < jSteps.length(); k++)
						{
							String polyline = "";
							polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
							List<LatLng> list = decodePoly(polyline);

							/** Traversing all points */
							for (int l = 0; l < list.size(); l++)
							{
								HashMap<String, String> hm = new HashMap<String, String>();
								hm.put("lat", Double.toString((list.get(l)).latitude));
								hm.put("lng", Double.toString((list.get(l)).longitude));
								path.add(hm);
							}
						}
					}
					routes.add(path);
				}
			}
			else
			{
				return null;
			}

		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
		}

		return routes;
	}


	/**
	 * Receives a JSONObject and returns a list with driving directions to destination
	 */
	public static ArrayList<String> DrivingDirectionsParser(JSONObject jObject)
	{
		ArrayList<String> directions = new ArrayList<String>();
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;
		String jStatus = null;
		try
		{
			jStatus = jObject.getString("status");
			if (jStatus.equalsIgnoreCase("ok"))
			{
				jRoutes = jObject.getJSONArray("routes");
				/** Traversing all routes */
				for (int i = 0; i < jRoutes.length(); i++)
				{
					jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
					/** Traversing all legs */
					for (int j = 0; j < jLegs.length(); j++)
					{
						jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

						/** Traversing all steps */
						for (int k = 0; k < jSteps.length(); k++)
						{
							String jdirections = ((JSONObject) jSteps.get(k)).getString("html_instructions");
							directions.add(jdirections);
						}
					}
				}
			}
			else
			{
				return null;
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return directions;
	}


	public static String getDirectionsUrl(LatLng origin, LatLng dest)
	{
		// Origin of route
		String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Building the url to the web service
		String url = DIRECTIONS_API_URL + parameters;

		return url;
	}


	/** A method to download json data from url */
	public static String downloadJSONdata(String strUrl) throws IOException
	{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try
		{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		}
		catch (Exception e)
		{
			//Log.d("Exception while downloading url", e.toString());
		}
		finally
		{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}


	/**
	 * Method to decode polyline points Courtesy :
	 * jeffreysambells.com/2010/05/27/decoding
	 * -polylines-from-google-maps-direction-api-with-java
	 * */
	public static List<LatLng> decodePoly(String encoded)
	{

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len)
		{
			int b, shift = 0, result = 0;
			do
			{
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			}
			while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do
			{
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			}
			while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng(((lat / 1E5)), ((lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}


	public JSONObject makeHttpGetRequest(String url)
	{
		try
		{

			// request method is GET
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line);
			}
			is.close();
			json = sb.toString();
		}
		catch (Exception e)
		{
			//Log.e("Buffer Error", "[" + json + "]");
		}

		// try parse the string to a JSON object
		try
		{
			jObj = new JSONObject(json);
		}
		catch (JSONException e)
		{
			//Log.e("JSON Parser", "[" + json + "]");

		}

		// return JSON String
		return jObj;

	}


	/** Receives a JSONObject and returns a list */
	public List<HashMap<String, String>> parseGooglePlaces(JSONObject jObject)
	{

		JSONArray jPlaces = null;
		try
		{
			/** Retrieves all the elements in the 'places' array */
			jPlaces = jObject.getJSONArray("predictions");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		/**
		 * Invoking getPlaces with the array of json object where each json object
		 * represent a place
		 */
		return getPlaces(jPlaces);
	}


	private List<HashMap<String, String>> getPlaces(JSONArray jPlaces)
	{
		int placesCount = jPlaces.length();
		List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> place = null;

		/** Taking each place, parses and adds to list object */
		for (int i = 0; i < placesCount; i++)
		{
			try
			{
				/** Call getPlace with place JSON object to parse the place */
				place = getPlace((JSONObject) jPlaces.get(i));
				placesList.add(place);

			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

		return placesList;
	}


	/** Parsing the Place JSON object */
	private HashMap<String, String> getPlace(JSONObject jPlace)
	{

		HashMap<String, String> place = new HashMap<String, String>();

		String id = "";
		String reference = "";
		String description = "";

		try
		{

			description = jPlace.getString("description");
			id = jPlace.getString("id");
			reference = jPlace.getString("reference");

			place.put("description", description);
			place.put("_id", id);
			place.put("reference", reference);

		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return place;
	}


	public static boolean isInternetAvailable(Context aContext)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) aContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null)
		{
			NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
			if (info != null)
			{
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
