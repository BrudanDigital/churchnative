package com.example.trial_map.factories;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

import android.util.Log;

public class JSONParser
{

	static InputStream	is		= null;
	static JSONObject		jObj	= null;
	static String				json	= "";

	// constructor
	public JSONParser()
	{

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params)
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
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,
					"iso-8859-1"), 8);
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
			Log.e("Buffer Error", "[" + json + "]");
		}

		// try parse the string to a JSON object
		try
		{
			jObj = new JSONObject(json);
		}
		catch (JSONException e)
		{
			Log.e("JSON Parser", "[" + json + "]");

		}

		// return JSON String
		return jObj;

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
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			sb.append(line);
		}
		is.close();
		json = sb.toString();
		Log.e("JSON results", "[" + json + "]");
	}
	catch (Exception e)
	{
		Log.e("Buffer Error", "[" + json + "]");
	}

	// try parse the string to a JSON object
	try
	{
		jObj = new JSONObject(json);
	}
	catch (JSONException e)
	{
		Log.e("JSON Parser", "[" + json + "]");

	}

	// return JSON String
	return jObj;

	}
	
	/** Receives a JSONObject and returns a list */
	public List<HashMap<String, String>> parse(JSONObject jObject)
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
}
