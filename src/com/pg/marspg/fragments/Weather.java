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
import com.pg.marspg.Utilities;
import com.pg.marspg.weather.WeatherReport;

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
		WeatherReport report = new WeatherReport();
		
		for(int i = 0; i < result.length();) {
			String startTag = "";
			String data;
			char startPtr = result.charAt(i);
			if(startPtr == '<') { //Start of tag
				int k = i+1;
				char endPtr = result.charAt(k);
				
				if(endPtr == '?' || endPtr == '/') {
					i++;
					continue;
				}
				
				while(endPtr != '>') {
					endPtr = result.charAt(k);
					k++;
				}
				startTag = result.substring(i, k);
				data = getDataFromTag(startTag, result.substring(i));
				report.populateData(startTag, data);
				
				i=k;
			} else {
				i++;
			}
		}

		((TextView)getActivity().findViewById(R.id.sol)).setText("Sol: " + report.iSol);
		((TextView)getActivity().findViewById(R.id.terrestrial_date)).setText(report.sTerrestialDate);
		((TextView)getActivity().findViewById(R.id.min_temp)).setText("Min temp: " + report.fMinTemp);
		((TextView)getActivity().findViewById(R.id.max_temp)).setText("Max temp: " + report.fMaxTemp);
		((TextView)getActivity().findViewById(R.id.pressure)).setText("Pressure: " + report.fPressure);
		((TextView)getActivity().findViewById(R.id.pressure_string)).setText("Pressure String: " + report.sPressureString);
		((TextView)getActivity().findViewById(R.id.absolute_humidity)).setText("Absolute Humidity: " + (report.fAbsoluteHumidity>0.0f?report.fAbsoluteHumidity:"--"));
		((TextView)getActivity().findViewById(R.id.wind_speed)).setText("Wind Speed: " + report.fWindSpeed);
		((TextView)getActivity().findViewById(R.id.wind_direction)).setText("Wind Direction: " + report.sWindDirection);
		((TextView)getActivity().findViewById(R.id.atmo_opacity)).setText("Atmospheric Opacity: " + report.sAtmosphericOpacity);
		((TextView)getActivity().findViewById(R.id.season)).setText("Season: " + report.sSeason);
		((TextView)getActivity().findViewById(R.id.ls)).setText("ls: " + report.fls);
		((TextView)getActivity().findViewById(R.id.sunrise)).setText("Sunrise: " + report.sSunrise);
		((TextView)getActivity().findViewById(R.id.sunset)).setText("Sunset: " + report.sSunset);
	}

	private String getDataFromTag(String startTag, String result) {
		String data = "";
		String closeTag = Utilities.insertCharacterToString(startTag, 1, '/');
		int tagSize = closeTag.length();
		int subPtr = 0;
		
		while(!result.substring(subPtr,subPtr+tagSize).equalsIgnoreCase(closeTag)) {
			subPtr++;
			if(subPtr+tagSize >= result.length()) break;
		}

		if(subPtr < result.length()) {
			data = result.substring(startTag.length(), subPtr);
		}
		
		return data;
	}
}