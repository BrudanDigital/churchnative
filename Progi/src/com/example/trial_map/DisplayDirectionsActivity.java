package com.example.trial_map;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class DisplayDirectionsActivity extends ActionBarActivity
{
	private TextView	directionsTextView;
	private Button		drawRouteBtn;
	LatLng						dest;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_directions);
		directionsTextView = (TextView) findViewById(R.id.directions_textView);
		drawRouteBtn = (Button) findViewById(R.id.directions_drawMapBtn);
		drawRouteBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				setResult(RESULT_OK);
				finish();
			}
		});
		
		ArrayList<String> directions = ListEventsActivity.directionsArrayList;
		if (directions != null)
		{
			Iterator<String> iterator = directions.iterator();
			String directionsString = null;
			while (iterator.hasNext())
			{
				if (directionsString != null)
				{
					directionsString = directionsString.concat(iterator.next() + ".\n");
					continue;
				}

				directionsString = (iterator.next()) + ".\n";
			}
			directionsTextView.setText(Html.fromHtml(directionsString).toString().toUpperCase());
		}
	}
}
