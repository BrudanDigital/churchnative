package com.example.trial_map;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String>
{
	private final Context		context;
	private final ArrayList<String>	values;

	public CustomArrayAdapter(Context context, ArrayList<String> values)
	{
		super(context, R.layout.list_events, values);
		this.context = context;
		this.values = values;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_events, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
		textView.setText(values.get(position));

		// Change icon based on name
		String value = values.get(position);

		System.out.println(value);
		imageView.setImageResource(R.drawable.party);
//		if (value.equals("")) {
//			imageView.setImageResource(R.drawable.windowsmobile_logo);
//		} else if (s.equals("iOS")) {
//			imageView.setImageResource(R.drawable.ios_logo);
//		} else if (s.equals("Blackberry")) {
//			imageView.setImageResource(R.drawable.blackberry_logo);
//		} else {
//			imageView.setImageResource(R.drawable.android_logo);
//		}
		return rowView;
	}
}
