package com.example.trial_map.beans;


public class EventOwner
{
	private int			user_id;
	private String	email;
	private String	password;
	private String	company_name;
	private String	company_location;
	private String  description_of_services;
	private boolean logged_in;
	
	public EventOwner(){}
	
	public EventOwner(int user_id, String email, String password,
			String company_name, String company_location,String  description_of_services_offered,boolean logged_in)
	{
		super();
		this.user_id = user_id;
		this.email = email;
		this.password = password;
		this.company_name = company_name;
		this.company_location = company_location;
		this.description_of_services=description_of_services_offered;
		this.logged_in=logged_in;
	}

	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	public String getCompany_name()
	{
		return company_name;
	}
	public void setCompany_name(String company_name)
	{
		this.company_name = company_name;
	}
	public String getCompany_location()
	{
		return company_location;
	}
	public void setCompany_location(String company_location)
	{
		this.company_location = company_location;
	}
	public int getUser_id()
	{
		return user_id;
	}
	public void setUser_id(int user_id)
	{
		this.user_id = user_id;
	}

	public String getDescription_of_services()
	{
		return description_of_services;
	}

	public void setDescription_of_services(String description_of_services)
	{
		this.description_of_services = description_of_services;
	}

	public boolean isLogged_in()
	{
		return logged_in;
	}

	public void setLogged_in(boolean logged_in)
	{
		this.logged_in = logged_in;
	}

}
