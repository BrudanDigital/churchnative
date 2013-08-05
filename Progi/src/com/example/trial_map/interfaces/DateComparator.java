package com.example.trial_map.interfaces;

import java.util.Comparator;

import com.example.trial_map.beans.Event;

public class DateComparator implements Comparator<Event>
{
	private static final int	BEFORE			= -1;
	private static final int	EQUALS			= 0;
	private static final int	AFTER				= 1;
	private String						class_date	= null;
	private int								class_day		= 0;
	private int								class_month	= 0;
	private int								class_year	= 0;


	/** returns true if the first date is greater than the second date **/
	private boolean isGreaterThan(String class_date, String a_date)
	{

		String[] a_date_parts = a_date.split("/");

		int a_day = Integer.parseInt(a_date_parts[0]);
		int a_month = Integer.parseInt(a_date_parts[1]);
		int a_year = Integer.parseInt(a_date_parts[2]);

		if (class_year > a_year)
		{// if the year is greater then it means the actual date is greater
			return true;
		}
		else if (class_year == a_year)
		{// if year is the same
			if (class_month > a_month)
			{// but the month is greater
				return true;
			}
			else if (class_month == a_month)
			{// if months are the same
				if (class_day > a_day)
				{// but the day is greater
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public int compare(Event anEvent, Event anotherEvent)
	{
		this.class_date = anEvent.getDate();
		String[] class_date_parts = class_date.split("/");
		class_day = Integer.parseInt(class_date_parts[0]);
		class_month = Integer.parseInt(class_date_parts[1]);
		class_year = Integer.parseInt(class_date_parts[2]);
		String a_date = anotherEvent.getDate();
		if (class_date.equalsIgnoreCase(a_date))
		{
			return EQUALS;
		}
		else if (isGreaterThan(class_date, a_date))
		{
			return AFTER;
		}
		else
			return BEFORE;
	}

}
