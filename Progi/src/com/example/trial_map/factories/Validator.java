package com.example.trial_map.factories;

public class Validator
{
	
	/**checks if a object is null or if its possible empty**/
	public static boolean isNullOrEmpty(Object anObject)
	{
		try
		{
			if (anObject == null)
			{
				return true;
			}
			if (anObject instanceof String)
			{
				String string=(String)anObject;
				if (string.isEmpty())
				{
					return true;
				}
			}
		}
		catch (NullPointerException e)
		{
			return true;
		}
		return false;
	}

	

	/** checks if an email is valid**/
	public static boolean isValidEmail(String email)
	{
		if (email.contains("@") && email.contains("."))
		{
			return true;
		}
		return false;
	}
}
