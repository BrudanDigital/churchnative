package com.example.trial_map.beans;

public class User
{
	private Contact users_contact;

	public User(Contact users_contact)
	{
		super();
		this.users_contact = users_contact;
	}

	public Contact getUsers_contact()
	{
		return users_contact;
	}

	public void setUsers_contact(Contact users_contact)
	{
		this.users_contact = users_contact;
	}


	
}
