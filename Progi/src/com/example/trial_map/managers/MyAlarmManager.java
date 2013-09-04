package com.example.trial_map.managers;

import java.util.Calendar;
import java.util.StringTokenizer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.trial_map.R;
import com.example.trial_map.beans.Event;

public class MyAlarmManager extends BroadcastReceiver
{
	private static final String	TIME_DELIMETER	= ":";
	private static final String	DATE_DELIMETER	= "/";
	private static final int		HELLO_ID				= 1;
	private static int					NOTIFICATION_ID	= 1;


	public static boolean setAlarm(Event anEvent, Context aContext)
	{
		// pseudo code
		// get date string
		// split it up into day month year
		// get time string
		// split it up into minutes,hrs
		// set calendar object to those values
		// set the title and notification to be displayed by alarm
		// set the alarm to go off using the calendar object

		String Date = anEvent.getDate();
		String start_time = anEvent.getTime();

		StringTokenizer date_Tokenizer = new StringTokenizer(Date, DATE_DELIMETER);
		int day = Integer.parseInt(date_Tokenizer.nextToken());
		int month = Integer.parseInt(date_Tokenizer.nextToken());
		int year = Integer.parseInt(date_Tokenizer.nextToken());

		StringTokenizer time_Tokenizer = new StringTokenizer(start_time, TIME_DELIMETER);
		int hour = Integer.parseInt(time_Tokenizer.nextToken());
		int minute = Integer.parseInt(time_Tokenizer.nextToken());

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);

		Intent alarmIntent = new Intent(aContext, MyAlarmManager.class);
		alarmIntent.putExtra("title", anEvent.getName_of_event());
		alarmIntent.putExtra("note", anEvent.getName_of_event() + " is occuring now");

		PendingIntent sender = PendingIntent.getBroadcast(aContext, HELLO_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
		AlarmManager alarmManager = (AlarmManager) aContext.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		return true;
	}


	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context aContext, Intent anIntent)
	{
		// pseudo code
		// get notification title and note
		// set notification
		// start alarm
		Bundle extras = anIntent.getExtras();
		String titleString = extras.getString("title");
		String noteString = extras.getString("note");
		NotificationManager aNotificationManager = (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification aNotification = new Notification(R.drawable.progi_logo, "Progi", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(aContext, NOTIFICATION_ID, new Intent(aContext, MyAlarmManager.class), 0);
		aNotification.setLatestEventInfo(aContext, titleString, noteString, contentIntent);

		aNotification.flags = Notification.FLAG_INSISTENT;
		aNotification.defaults = Notification.DEFAULT_SOUND;

		aNotificationManager.notify(NOTIFICATION_ID++, aNotification);
	}
}
