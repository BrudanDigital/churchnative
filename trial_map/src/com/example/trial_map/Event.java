package com.example.trial_map;


<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Date;
=======
>>>>>>> trial
=======
>>>>>>> trial
import com.google.android.gms.maps.model.LatLng;


//java bean class for an Event
public class Event
{
<<<<<<< HEAD
<<<<<<< HEAD
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
=======
=======
>>>>>>> trial
	private LatLng event_location;
	private String event_name;
	private double latitude;
	private double longitude;
	private String start_time;
	private String date;
	private String event_description;
	private String event_duration;
	private EventOwner event_owner;
	
	public Event(LatLng location_of_event, String time, String date,String description_of_event,String name_of_event,String duration_of_event)
	{
		super();
		this.event_location = location_of_event;
		this.start_time = time;
		this.date = date;
		this.setLatitude(location_of_event.latitude);
		this.setLongitude(location_of_event.longitude);
		this.event_description = description_of_event;
		this.event_name=name_of_event;
		this.event_duration=duration_of_event;
		
	}
	
	public LatLng getLocation_of_event()
	{
		return event_location;
	}
	
	public void setLocation_of_event(LatLng location_of_event)
	{
		this.event_location = location_of_event;
<<<<<<< HEAD
>>>>>>> trial
=======
>>>>>>> trial
	}
	
	public String getTime()
	{
<<<<<<< HEAD
<<<<<<< HEAD
		return time;
=======
		return start_time;
>>>>>>> trial
=======
		return start_time;
>>>>>>> trial
	}
	
	public void setTime(String time)
	{
<<<<<<< HEAD
<<<<<<< HEAD
		this.time = time;
	}
	
	public Date getDate()
=======
=======
>>>>>>> trial
		this.start_time = time;
	}
	
	

	public String getDate()
<<<<<<< HEAD
>>>>>>> trial
=======
>>>>>>> trial
	{
		return date;
	}
	
<<<<<<< HEAD
<<<<<<< HEAD
	public void setDate(Date date)
=======
	public void setDate(String date)
>>>>>>> trial
=======
	public void setDate(String date)
>>>>>>> trial
	{
		this.date = date;
	}
	
	public String getDescription_of_event()
	{
<<<<<<< HEAD
<<<<<<< HEAD
		return description_of_event;
=======
		return event_description;
>>>>>>> trial
=======
		return event_description;
>>>>>>> trial
	}
	
	public void setDescription_of_event(String description_of_event)
	{
<<<<<<< HEAD
<<<<<<< HEAD
		this.description_of_event = description_of_event;
=======
		this.event_description = description_of_event;
>>>>>>> trial
=======
		this.event_description = description_of_event;
>>>>>>> trial
	}
	
	public EventOwner getEvent_owner()
	{
		return event_owner;
	}
	
	public void setEvent_owner(EventOwner event_owner)
	{
		this.event_owner = event_owner;
	}
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> trial

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
<<<<<<< HEAD
>>>>>>> trial
=======
>>>>>>> trial
}
