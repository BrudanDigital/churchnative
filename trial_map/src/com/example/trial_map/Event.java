package com.example.trial_map;


import java.util.Date;
import com.google.android.gms.maps.model.LatLng;


//java bean class for an Event
public class Event
{
	private LatLng location_of_eventLatLng;
	private String time;
	private Date date;
	private String description_of_event;
	private EventOwner event_owner;
	
	public LatLng getLocation_of_eventLatLng()
	{
		return location_of_eventLatLng;
	}
	
	public void setLocation_of_eventLatLng(LatLng location_of_eventLatLng)
	{
		this.location_of_eventLatLng = location_of_eventLatLng;
	}
	
	public String getTime()
	{
		return time;
	}
	
	public void setTime(String time)
	{
		this.time = time;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public void setDate(Date date)
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
}
