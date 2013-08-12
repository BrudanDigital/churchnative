package com.example.trial_map.factories;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.trial_map.MainActivity;
import com.example.trial_map.beans.EventOwner;
/**This Class carries out operations on EventOwner objects**/
public class EventOwnerFactory
{
	/** url to php script at website **/
	private static final String	PHP_SCRIPT_ADDRESS			= MainActivity.WEBSITE_URL;
	private static final String	LOGIN_URL								= PHP_SCRIPT_ADDRESS + "login.php";
	private static final String	TAG_SUCCESS							= "success";
	private static final String	TAG_EVENT_OWNER					= "event_owner";
	private static final String	TAG_ID									= "user_id";
	private static final String	TAG_EMAIL								= "email";
	private static final String	TAG_PASSWORD						= "password";
	private static final String	TAG_COMPANY_NAME				= "company_name";
	private static final String	TAG_LOCATION						= "company_location";
	private static final String	TAG_DESCRIPTION					= "description_of_services";
	private static final String	ILLEGAL_PARAMETER_TEXT	= "the paramters cannot be null";


	/** retrieves an eventOwner who has the given email and password combination **/
	public static EventOwner getEventOwner(String user_email, String user_password)
	{
		if (user_email == null || user_password == null)
		{
			throw new IllegalArgumentException(ILLEGAL_PARAMETER_TEXT);
		}
		NetworkManager jsonParser = new NetworkManager();
		try
		{
			List<NameValuePair> url_parameters = new ArrayList<NameValuePair>();
			url_parameters.add(new BasicNameValuePair("email", user_email));
			url_parameters.add(new BasicNameValuePair("password", user_password));
			JSONObject jsonObject = jsonParser.makeHttpRequest(LOGIN_URL, "POST", url_parameters);
			// Checking for SUCCESS TAG
			int success = jsonObject.getInt(TAG_SUCCESS);
			if (success == 1)
			{
				// Getting Array of events
				JSONArray events_array = jsonObject.getJSONArray(TAG_EVENT_OWNER);
				// looping through All Events returned and storing each separately
				JSONObject event_owner = events_array.getJSONObject(0);
				// Storing each json item in variable
				int user_id = event_owner.getInt(TAG_ID);
				String email = event_owner.getString(TAG_EMAIL);
				String password = event_owner.getString(TAG_PASSWORD);
				String company_name = event_owner.getString(TAG_COMPANY_NAME);
				String company_location = event_owner.getString(TAG_LOCATION);
				String description_of_services = event_owner.getString(TAG_DESCRIPTION);
				EventOwner anEventOwner = new EventOwner(user_id, email, password, company_name, company_location, description_of_services);
				return anEventOwner;
			}
			else
			{// success is 0 and no results
				return null;
			}
		}
		catch (Exception e)
		{

		}
		return null;
	}


	/** creates a new eventOwner at web server **/
	public static boolean createEventOwner(EventOwner anEventOwner)
	{
		return false;
	}
}
