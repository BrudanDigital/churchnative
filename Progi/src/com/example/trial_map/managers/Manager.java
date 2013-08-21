package com.example.trial_map.managers;

import com.example.trial_map.MainActivity;

public abstract class Manager
{
	public static final int			SUCCESS								= 1;
	public static final int			FAILURE								= 0;
	public static final int			NO_CONNECTION					= 2;
	public static final String	NO_CONNECTION_MESSAGE	= "Sorry No Working Internet Connection Found!!!";
	public static final String	GOOGLE_DIRECTIONS_URL	= MainActivity.GOOGLE_DIRECTIONS_URL;
	public static final String	GOOGLE_PLACES_URL			= null;
	public static String				PHP_SCRIPT_ADDRESS		= MainActivity.WEBSITE_URL;
	public static String				MESSAGE								= "No Success Message";
	public static final String	TAG_SUCCESS						= "success";
	public static final String	TAG_MESSAGE						= "message";
}
