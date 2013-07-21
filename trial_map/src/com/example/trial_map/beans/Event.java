package com.example.trial_map.beans;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

//java bean class for an Event
public class Event implements Serializable
{

	private static final long	serialVersionUID	= -3770730160649638144L;
	private String						event_location_in_words;
	private LatLng						event_location;
	private String						event_name;
	private double						latitude;
	private double						longitude;
	private String						start_time;
	private String						date;
	private String						event_description;
	private String						event_duration;
	private String 						type_of_event;
	private int								user_id;
	private int								event_id;

	public Event(Double latitude, Double longitude, String time, String date, String description_of_event, String name_of_event, String duration_of_event, String event_location_in_words,int user_id,int event_id,String type_of_event)
	{
		super();
		this.start_time = time;
		this.date = date;
		this.latitude = latitude;
		this.longitude = longitude;
		this.event_description = description_of_event;
		this.event_name = name_of_event;
		this.event_duration = duration_of_event;
		this.event_location_in_words = event_location_in_words;
		this.event_location = new LatLng(latitude, longitude);
		this.user_id=user_id;
		this.event_id=event_id;
		this.type_of_event=type_of_event;

	}

	public LatLng getLocation_of_event()
	{
		return event_location;
	}

	public void setLocation_of_event(LatLng location_of_event)
	{
		this.event_location = location_of_event;
	}

	public String getTime()
	{
		return start_time;
	}

	public void setTime(String time)
	{
		this.start_time = time;
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
		return event_description;
	}

	public void setDescription_of_event(String description_of_event)
	{
		this.event_description = description_of_event;
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

	public String getDuration()
	{
		return event_duration;
	}

	public void setDuration(String duration)
	{
		this.event_duration = duration;
	}

	public String getName_of_event()
	{
		return event_name;
	}

	public void setName_of_event(String name_of_event)
	{
		this.event_name = name_of_event;
	}

	public String getEvent_location_in_words()
	{
		return event_location_in_words;
	}

	public void setEvent_location_in_words(String event_location_in_words)
	{
		this.event_location_in_words = event_location_in_words;
	}

	public int getUser_id()
	{
		return user_id;
	}

	public void setUser_id(int user_id)
	{
		this.user_id = user_id;
	}

	public int getEvent_id()
	{
		return event_id;
	}

	public void setEvent_id(int event_id)
	{
		this.event_id = event_id;
	}

	public String getType_of_event()
	{
		return type_of_event;
	}

	public void setType_of_event(String type_of_event)
	{
		this.type_of_event = type_of_event;
	}

}
