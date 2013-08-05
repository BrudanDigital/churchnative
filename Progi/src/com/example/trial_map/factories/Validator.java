package com.example.trial_map.factories;

import java.util.Calendar;

public class Validator
{

	/** checks if a object is null or if its possible empty **/
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
				String string = (String) anObject;
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


	public static boolean isDateOkay(String date)
	{
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		String[] parts = date.split("/");
		int date_day = Integer.parseInt(parts[0]);
		int date_month = Integer.parseInt(parts[1]);
		int date_year = Integer.parseInt(parts[2]);
		if (date_year > year)
		{
			return true;
		}
		if (date_year == year)
		{
			if (date_month > month)
			{
				return true;
			}
			if (date_month == month)
			{
				if (date_day >= day)
				{
					return true;
				}
				return false;
			}
			return false;
		}
		return false;
	}


	/** checks if an email is valid **/
	public static boolean isValidEmail(String email)
	{
		if (email.contains("@") && email.contains("."))
		{
			return true;
		}
		return false;
	}
}
