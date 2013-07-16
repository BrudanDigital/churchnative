package com.example.trial_map.factories;

public class HelperClass
{
	//checks if a string is empty or null
	public static boolean isNullOrEmpty(String string)
	{
		try
		{
			if (string == null||string.isEmpty() )
			{
				return true;
			}
		}
		catch (NullPointerException e)
		{
			return true;
		}
		return false;
	}
	
	//checks if an email is valid
	public static boolean isEmail(String email)
	{
		if (email.contains("@")&&email.contains("."))
		{
			return true;
		}
		return false;
	}
}
