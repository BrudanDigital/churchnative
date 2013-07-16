package com.example.trial_map.factories;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.example.trial_map.beans.EventOwner;

public class EventOwnerFactory
{
	private static final String	LOGIN_URL	= "";

	public static EventOwner getEventOwner(String[] parameters)
	{
		JSONParser jsonParser = new JSONParser();
		
		List<NameValuePair> url_parameters = new ArrayList<NameValuePair>();
		url_parameters.add(new BasicNameValuePair("email", parameters[0]));
		url_parameters.add(new BasicNameValuePair("password", parameters[1]));
		//JSONObject jsonObject=jsonParser.makeHttpRequest(LOGIN_URL,"POST",url_parameters);
		return new EventOwner(1, "trial", "trial", "trial", "trial","trial",true);
	}
}
