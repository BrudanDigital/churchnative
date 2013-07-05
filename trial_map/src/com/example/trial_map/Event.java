package com.example.trial_map;


import java.util.Date;

import com.google.android.gms.maps.model.LatLng;


//java bean class for an Event
public class Event
{
	private LatLng location_of_event;
	private double latitude;
	private double longitude;
	private String time;
	private String date;
	private String description_of_event;
	private EventOwner event_owner;
	
	public LatLng getLocation_of_event()
	{
		return location_of_event;
	}
	
	public void setLocation_of_event(LatLng location_of_event)
	{
		this.location_of_event = location_of_event;
	}
	
	public String getTime()
	{
		return time;
	}
	
	public void setTime(String time)
	{
		this.time = time;
	}
	
	public Event(LatLng location_of_event, String time, String date,String description_of_event)
	{
		super();
		this.location_of_event = location_of_event;
		this.time = time;
		this.date = date;
		this.setLatitude(location_of_event.latitude);
		this.setLongitude(location_of_event.longitude);
		this.description_of_event = description_of_event;
		
	}

	public String getDate()
	{
		return date;
	}
	
	public void setDate(String date)
	{
		this.date = date;
	}
	
	public String getDescription_of_event()
	{
		return description_of_event;
	}
	
	public void setDescription_of_event(String description_of_event)
	{
		this.description_of_event = description_of_event;
	}
	
	public EventOwner getEvent_owner()
	{
		return event_owner;
	}
	
	public void setEvent_owner(EventOwner event_owner)
	{
		this.event_owner = event_owner;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
}
