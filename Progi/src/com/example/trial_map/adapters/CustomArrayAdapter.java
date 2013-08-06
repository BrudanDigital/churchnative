package com.example.trial_map.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trial_map.R;
import com.example.trial_map.beans.Event;

public class CustomArrayAdapter extends ArrayAdapter<String>
{
	private final Context						context;
	private final ArrayList<String>	values;
	private ArrayList<Event>				events_ArrayList;


	public CustomArrayAdapter(Context context, ArrayList<String> values)
	{
		super(context, R.layout.list_events, values);
		this.context = context;
		this.values = values;
	}


	public CustomArrayAdapter(Context context, ArrayList<String> values, ArrayList<Event> events)
	{
		super(context, R.layout.list_events, values);
		if (context==null||values==null||events==null)
		{
			throw new IllegalArgumentException("Parameters Cant be Null");
		}
		this.context = context;
		this.values = values;
		this.events_ArrayList = events;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_events, parent, false);
		TextView label = (TextView) rowView.findViewById(R.id.label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
		label.setText(values.get(position));

		// Change icon based on name
		String type = events_ArrayList.get(position).getType_of_event();

		System.out.println(type);
		
		if (type.equalsIgnoreCase("meeting"))
		{
			imageView.setImageResource(R.drawable.meeting);
		}
		else if (type.equalsIgnoreCase("party"))
		{
			imageView.setImageResource(R.drawable.party);
		}
		else if (type.equalsIgnoreCase("Social"))
		{
			imageView.setImageResource(R.drawable.social);
		}
		else if (type.equalsIgnoreCase("religious"))
		{
			imageView.setImageResource(R.drawable.religious);
		}
		else if (type.equalsIgnoreCase("programming"))
		{
			imageView.setImageResource(R.drawable.programming);
		}
		else if (type.equalsIgnoreCase("cinema"))
		{
			imageView.setImageResource(R.drawable.cinema);
		}
		else if (type.equalsIgnoreCase("drink up"))
		{
			imageView.setImageResource(R.drawable.drink_up);
		}
		else if (type.equalsIgnoreCase("music festival"))
		{
			imageView.setImageResource(R.drawable.music_festival);
		}
		else if (type.equalsIgnoreCase("strike"))
		{
			imageView.setImageResource(R.drawable.strike);
		}
		return rowView;
	}

}
