package com.example.trial_map.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.google.android.gms.maps.model.LatLng;

public class NetworkManager extends Manager
{
	private static final String	OUTPUT							= "json";
	private static final String	DIRECTIONS_API_URL	= GOOGLE_DIRECTIONS_URL + OUTPUT + "?";
	static InputStream					input_stream				= null;
	static JSONObject						json_Object					= null;
	static String								json_string_data		= "";


	/** constructor **/
	public NetworkManager()
	{

	}


	/** function get json from url by making HTTP POST request **/
	public static JSONObject makeHttpPostRequest(String url, List<NameValuePair> params)
	{

		// Making HTTP request
		try
		{

			// request method is POST
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			input_stream = httpEntity.getContent();

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
			BufferedReader reader = new BufferedReader(new InputStreamReader(input_stream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line);
			}
			input_stream.close();
			json_string_data = sb.toString();
			//Log.e("json", "[" + json_string_data + "]");
		}
		catch (Exception e)
		{
			//Log.e("Buffer Error", "[" + json_string_data + "]");
		}

		// try parse the string to a JSON object
		try
		{
			json_Object = new JSONObject(json_string_data);
		}
		catch (JSONException e)
		{
			//Log.e("JSON Parser", "[" + json_string_data + "]");

		}

		// return JSON String
		return json_Object;

	}


	/**
	 * returns a url to the google directions server based on the latlng positions
	 * submitted
	 **/
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
	public static String downloadJSONdata(String strUrl)
	{

		HttpURLConnection urlConnection = null;
		try
		{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			input_stream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(input_stream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}

			json_string_data = sb.toString();

			br.close();

		}
		catch (Exception e)
		{
			MESSAGE = "Exception while downloading url";
			return null;
			// Log.d("Exception while downloading url", e.toString());
		}
		finally
		{
			try
			{
				input_stream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				urlConnection.disconnect();
			}

		}
		return json_string_data;
	}


	/** function get json from url by making HTTP GET request **/
	public static JSONObject makeHttpGetRequest(String url)
	{
		try
		{
			// request method is GET
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			input_stream = httpEntity.getContent();

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
			BufferedReader reader = new BufferedReader(new InputStreamReader(input_stream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line);
			}
			json_string_data = sb.toString();
			//Log.e("Buffer Error", "[" + json_string_data + "]");
		}
		catch (Exception e)
		{
			//Log.e("Buffer Error", e.getMessage());
		}
		finally
		{
			try
			{
				input_stream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		// try parse the string to a JSON object
		try
		{
			json_Object = new JSONObject(json_string_data);
		}
		catch (JSONException e)
		{
			//Log.e("JSON Parser", e.getMessage());

		}

		// return JSON String
		return json_Object;

	}


	/** function that checks if Internet is available on the device **/

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
