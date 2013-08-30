package com.example.trial_map.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class JSONManager extends Manager 
{

	/**
	 * Method to decode polyline points Courtesy :
	 * jeffreysambells.com/2010/05/27/decoding
	 * -polylines-from-google-maps-direction-api-with-java
	 * */
	public static List<LatLng> decodePoly(String encoded)
	{
		if (encoded==null)
		{
			return null;
		}
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
				MESSAGE="Directions Found";
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
				MESSAGE="No Directions To Location Found";
				return null;
			}

		}
		catch (Exception e)
		{
			MESSAGE=NO_CONNECTION_MESSAGE;
			e.printStackTrace();
		}
		

		return routes;
	}


	/**
	 * Receives a JSONObject and returns a list with driving directions to
	 * destination
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
				MESSAGE="Directions Found";
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
				MESSAGE="No Directions Found";
				return null;
			}
		}
		catch (Exception e)
		{
			MESSAGE=NO_CONNECTION_MESSAGE;
			e.printStackTrace();
		}

		return directions;
	}

}
