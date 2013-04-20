package com.pg.marspg.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pg.marspg.OnTaskCompleted;
import com.pg.marspg.R;
import com.pg.marspg.RetrieveSiteData;

public class Weather extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.weather, container, false);
        
        (new RetrieveSiteData(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(String result) {
				parseXml(result);
			}
		})).execute("http://cab.inta-csic.es/rems/rems_weather.xml");
        
        return v;
    }

	private void parseXml(String result) {
		for(int i = 0; i < result.length(); i++) {
			char startPtr = result.charAt(i);
			if(startPtr == '<') { //Start of tag
				char endPtr = startPtr;
				int k = 0;
				while(endPtr != '>') {
					endPtr = result.charAt(i+k);
					k++;
				}
			}
		}
		String weather[] = result.split("<atmo_opacity>");
		String type[] = weather[1].split("</atmo_opacity>");
		((TextView)getActivity().findViewById(R.id.atmo_opacity)).setText("Atmospheric Opacity: " + type[0]);
	}
}