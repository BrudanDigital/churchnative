package com.example.trial_map.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;

import com.example.trial_map.beans.Event;

import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.trial_map.beans.Contact;
import com.example.trial_map.beans.User;

public class ContactsManager extends Manager
{
	private static final String	SAVE_INVITED_CONTACTS			= PHP_SCRIPT_ADDRESS + "save_invited_contacts.php";
	private static final String	GET_INVITED_CONTACTS			= PHP_SCRIPT_ADDRESS + "get_invited_contacts.php";
	private static final String	GET_ALL_INVITED_CONTACTS	= PHP_SCRIPT_ADDRESS + "get_all_invited_contacts.php";
	private static final String	DELETE_INVITED_CONTACTS		= PHP_SCRIPT_ADDRESS + "delete_invited_contacts.php";
	private static final String	TAG_NAME									= "name";
	private static final String	TAG_NUMBER								= "number";
	private static final String	TAG_CONTACTS							= "contacts";


	/** returns an arraylist containing all contacts in Phone Book **/
	public static ArrayList<Contact> getAllContacts(Activity anActivityContext)
	{
		ArrayList<Contact> contactList = new ArrayList<Contact>();
		String[] PROJECTION = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER, };
		String SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
		Cursor contacts = anActivityContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, null, null);

		if (contacts.getCount() > 0)
		{
			while (contacts.moveToNext())
			{
				Contact aContact = new Contact();
				int nameFieldColumnIndex = 0;
				int numberFieldColumnIndex = 0;

				String contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));

				nameFieldColumnIndex = contacts.getColumnIndex(PhoneLookup.DISPLAY_NAME);
				if (nameFieldColumnIndex > -1)
				{
					aContact.setName(contacts.getString(nameFieldColumnIndex));
				}

				PROJECTION = new String[] { Phone.NUMBER };
				final Cursor phone = anActivityContext.getContentResolver().query(Phone.CONTENT_URI, PROJECTION, Data.CONTACT_ID + "=?", new String[] { String.valueOf(contactId) }, null);
				if (phone.moveToFirst())
				{
					while (!phone.isAfterLast())
					{
						numberFieldColumnIndex = phone.getColumnIndex(Phone.NUMBER);
						if (numberFieldColumnIndex > -1)
						{
							aContact.setPhone_number(phone.getString(numberFieldColumnIndex));
							phone.moveToNext();
							TelephonyManager mTelephonyMgr;
							mTelephonyMgr = (TelephonyManager) anActivityContext.getSystemService(Context.TELEPHONY_SERVICE);
							if (!mTelephonyMgr.getLine1Number().contains(aContact.getPhone_number()))
							{
								contactList.add(aContact);
							}
						}
					}
				}
				phone.close();
			}

			contacts.close();
		}

		return contactList;
	}


	/** returns the contacts invited to an event by the given user **/
	public static ArrayList<Contact> getContactsInvitedToEvent(Event anEvent, User theUser)
	{
		Contact usersContact = theUser.getUsers_contact();
		ArrayList<Contact> contactsList = new ArrayList<Contact>();
		List<NameValuePair> url_parameters = new ArrayList<NameValuePair>();
		url_parameters.add(new BasicNameValuePair("inviter_name", usersContact.getName()));
		url_parameters.add(new BasicNameValuePair("inviter_number", usersContact.getPhone_number()));
		url_parameters.add(new BasicNameValuePair("event_id", "" + anEvent.getEvent_id()));
		JSONObject jsonObject = NetworkManager.makeHttpPostRequest(GET_INVITED_CONTACTS, url_parameters);
		
			try
			{
				// Checking for SUCCESS TAG
				int success = jsonObject.getInt(TAG_SUCCESS);
				MESSAGE = jsonObject.getString(TAG_MESSAGE);
				if (success == 1)
				{
					// Getting Array of contacts
					JSONArray contacts_array = jsonObject.getJSONArray(TAG_CONTACTS);
					// looping through All Events returned and storing each separately
					for (int i = 0; i < contacts_array.length(); i++)
					{
						JSONObject contact = contacts_array.getJSONObject(i);
						String name = contact.getString(TAG_NAME);
						String number = contact.getString(TAG_NUMBER);
						Contact aContact = new Contact(name, number);
						contactsList.add(aContact);
					}
				}
			}
			catch (NullPointerException e)
			{
				MESSAGE = NO_CONNECTION_MESSAGE;
				Log.e("JSON Error", e.getMessage());
			}
			catch (JSONException e)
			{
				Log.e("JSON Error", e.getMessage());
			}
		
		return contactsList;
	}


	/** returns all contacts invited to an event by anyone **/
	public static ArrayList<Contact> getAllContactsInvitedToAnEvent(Event anEvent)
	{
		ArrayList<Contact> contactsList = new ArrayList<Contact>();
		List<NameValuePair> url_parameters = new ArrayList<NameValuePair>();
		url_parameters.add(new BasicNameValuePair("event_id", "" + anEvent.getEvent_id()));
		JSONObject jsonObject = NetworkManager.makeHttpPostRequest(GET_ALL_INVITED_CONTACTS, url_parameters);
		
			try
			{
				// Checking for SUCCESS TAG
				int success = jsonObject.getInt(TAG_SUCCESS);
				MESSAGE = jsonObject.getString(TAG_MESSAGE);
				if (success == 1)
				{
					// Getting Array of contacts
					JSONArray contacts_array = jsonObject.getJSONArray(TAG_CONTACTS);
					// looping through All Contacts returned and storing each separately
					for (int i = 0; i < contacts_array.length(); i++)
					{
						JSONObject contact = contacts_array.getJSONObject(i);
						String name = contact.getString(TAG_NAME);
						String number = contact.getString(TAG_NUMBER);
						Contact aContact = new Contact(name, number);
						contactsList.add(aContact);
					}
				}
			}
			catch (NullPointerException e)
			{
				MESSAGE = NO_CONNECTION_MESSAGE;
				Log.e("JSON Error", e.getMessage());
			}
			
			catch (JSONException e)
			{
				Log.e("JSON Error", e.getMessage());
			}
	
		return contactsList;
	}


	public static int saveInvitedContacts(ArrayList<Contact> contacts, User aUser, Event anEvent)
	{
		if (contacts == null || aUser == null)
		{
			throw new IllegalArgumentException("PARAMETERS CANT BE NULL!!");
		}
		Iterator<Contact> anIterator = contacts.iterator();
		Contact usersContact = aUser.getUsers_contact();
		while (anIterator.hasNext())
		{
			Contact aContact = (Contact) anIterator.next();
			List<NameValuePair> url_parameters = new ArrayList<NameValuePair>();
			url_parameters.add(new BasicNameValuePair("inviter_name", usersContact.getName()));
			url_parameters.add(new BasicNameValuePair("inviter_number", usersContact.getPhone_number()));
			url_parameters.add(new BasicNameValuePair("invitee_number", aContact.getPhone_number()));
			url_parameters.add(new BasicNameValuePair("invitee_name", aContact.getName()));
			url_parameters.add(new BasicNameValuePair("event_id", "" + anEvent.getEvent_id()));
			JSONObject jsonObject = NetworkManager.makeHttpPostRequest(SAVE_INVITED_CONTACTS, url_parameters);

			try
			{
				// Checking for SUCCESS TAG
				int success = jsonObject.getInt(TAG_SUCCESS);
				MESSAGE = jsonObject.getString(TAG_MESSAGE);
				if (success == 0)
				{
					return FAILURE;
				}
			}
			catch (NullPointerException e)
			{
				MESSAGE = NO_CONNECTION_MESSAGE;
				Log.e("JSON Error", e.getMessage());
			}
			catch (JSONException e)
			{
				MESSAGE = NO_CONNECTION_MESSAGE;
				Log.e("JSON Error", e.getMessage());
				return FAILURE;
			}

		}

		return SUCCESS;
	}


	public static int deleteInvitedContacts(ArrayList<Contact> contacts, Event anEvent, User aUser)
	{
		if (contacts == null || aUser == null)
		{
			throw new IllegalArgumentException("PARAMETERS CANT BE NULL!!");
		}
		Iterator<Contact> anIterator = contacts.iterator();
		Contact usersContact = aUser.getUsers_contact();
		while (anIterator.hasNext())
		{
			Contact aContact = (Contact) anIterator.next();
			List<NameValuePair> url_parameters = new ArrayList<NameValuePair>();
			url_parameters.add(new BasicNameValuePair("inviter_name", usersContact.getName()));
			url_parameters.add(new BasicNameValuePair("inviter_number", usersContact.getPhone_number()));
			url_parameters.add(new BasicNameValuePair("invitee_number", aContact.getPhone_number()));
			url_parameters.add(new BasicNameValuePair("invitee_name", aContact.getName()));
			url_parameters.add(new BasicNameValuePair("event_id", "" + anEvent.getEvent_id()));
			JSONObject jsonObject = NetworkManager.makeHttpPostRequest(DELETE_INVITED_CONTACTS, url_parameters);

			try
			{
				// Checking for SUCCESS TAG
				int success = jsonObject.getInt(TAG_SUCCESS);
				MESSAGE = jsonObject.getString(TAG_MESSAGE);
				if (success == 0)
				{
					return FAILURE;
				}
			}
			catch (NullPointerException e)
			{
				MESSAGE = NO_CONNECTION_MESSAGE;
				Log.e("JSON Error", e.getMessage());
			}
			catch (JSONException e)
			{
				MESSAGE = NO_CONNECTION_MESSAGE;
				Log.e("JSON Error", e.getMessage());
				return FAILURE;
			}
		}
		return SUCCESS;
	}


	public static void saveUsersContactLocaly(Contact usersContact, Activity anActivity)
	{
		if (usersContact==null||anActivity==null)
		{
			throw new IllegalArgumentException(ILLEGAL_PARAMETER_TEXT);
		}
		// TODO Auto-generated method stub
		SharedPreferences preferences = anActivity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("name", usersContact.getName());
		editor.putString("number", usersContact.getPhone_number());
		editor.commit();
		Manager.MESSAGE="Contacts SuccessFully Saved.\nYou Can Now Try Again";
	}


	public static CharSequence[] getContactNames(ArrayList<Contact> contactsList)
	{
		Iterator<Contact> anIterator = contactsList.iterator();
		CharSequence[] strings = new CharSequence[contactsList.size()];
		int i = 0;
		while (anIterator.hasNext())
		{
			Contact contact = (Contact) anIterator.next();
			String name = contact.getName() + "\n" + contact.getPhone_number();
			strings[i] = name;
			i++;
		}
		return strings;
	}
}
