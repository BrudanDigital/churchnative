package com.example.trial_map.managers;

import com.example.trial_map.MainActivity;

public abstract class Manager
{
	public static final int				SUCCESS									= 1;
	public static final int				FAILURE									= 0;
	public static final int				NO_CONNECTION						= 2;
	public static final String		NO_CONNECTION_MESSAGE		= "Sorry No Working Internet Connection Found!!!";
	public static final String		GOOGLE_DIRECTIONS_URL		= MainActivity.GOOGLE_DIRECTIONS_URL;
	public static final String		GOOGLE_PLACES_URL				= null;
	public static String					PHP_SCRIPT_ADDRESS			= MainActivity.WEBSITE_URL;
	public static String					MESSAGE									= "No Success Message";
	public static final String		TAG_SUCCESS							= "success";
	public static final String		TAG_MESSAGE							= "message";
	protected static final String	GEOCODE_ADDRESS					= "https://maps.googleapis.com/maps/api/geocode/json";
	protected static String				ILLEGAL_PARAMETER_TEXT	= "Parameters Cant Be NULL";


	/** capitalizes the first letter of each word in a given string **/
	public static String capitaliseFirstLetterOfEachWord(String aString)
	{
		if (aString == null)
		{
			throw new IllegalArgumentException("Parameter Cant be Null");
		}

		boolean prevWasWhiteSp = true;
		char[] chars = aString.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			if (Character.isLetter(chars[i]))
			{
				if (prevWasWhiteSp)
				{
					chars[i] = Character.toUpperCase(chars[i]);
				}
				prevWasWhiteSp = false;
			}
			else
			{
				prevWasWhiteSp = Character.isWhitespace(chars[i]);
			}
		}
		return new String(chars);
	}
}
