package com.example.trial_map.beans;

public class Contact
{

	private String	name					= null;
	private String	phone_number	= null;


	public Contact(String name, String phone_number)
	{
		super();
		this.name = name;
		this.phone_number = phone_number;
	}


	public Contact()
	{
	}


	public String getPhone_number()
	{
		return phone_number;
	}


	public void setPhone_number(String phone_number)
	{
		this.phone_number = phone_number;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}
}
