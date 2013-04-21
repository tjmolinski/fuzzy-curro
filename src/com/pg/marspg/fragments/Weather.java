package com.pg.marspg.fragments;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.pg.marspg.Constants;
import com.pg.marspg.OnTaskCompleted;
import com.pg.marspg.R;
import com.pg.marspg.RetrieveSiteData;
import com.pg.marspg.Utilities;
import com.pg.marspg.weather.WeatherReport;

public class Weather extends Fragment {
	private ArrayList<WeatherReport> weatherReports = new ArrayList<WeatherReport>();
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.weather, container, false);
        
        (new RetrieveSiteData(getActivity(), new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(String result) {
				parseXml(result);
				fillInSelectedForecastWithReport(weatherReports.get(weatherReports.size()-1));
				fillInSpecificDayForecastWithReport(3, (weatherReports.size()-2>=0?weatherReports.get(weatherReports.size()-2):null));
				fillInSpecificDayForecastWithReport(2, (weatherReports.size()-3>=0?weatherReports.get(weatherReports.size()-3):null));
				fillInSpecificDayForecastWithReport(1, (weatherReports.size()-4>=0?weatherReports.get(weatherReports.size()-4):null));
			}
		})).execute(Constants.HISTORICAL_MARS_WEATHER_SITE);
        
        v.findViewById(R.id.selected_forecast).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePicker();
			}
		});
        
        return v;
    }

	private void parseXml(String result) {
		WeatherReport report = null;
		
		for(int i = 0; i < result.length();) {
			String startTag = "";
			String nakedTag = "";
			String data;
			char startPtr = result.charAt(i);
			if(startPtr == '<') { //Start of tag
				int k = i+1;
				char endPtr = result.charAt(k);
				
				//End tag or xml tag was found so lets skip this one
				if(endPtr == '?' || endPtr == '/') {
					i++;
					continue;
				}
				
				while(endPtr != '>') {
					endPtr = result.charAt(k);
					k++;
				}
				
				startTag = result.substring(i, k);
				nakedTag = startTag.substring(1, startTag.length()-1);
				
				if(nakedTag.equalsIgnoreCase("record") || nakedTag.equalsIgnoreCase("weather_report")) {
					if(report != null) {
						weatherReports.add(report);
					}
					
					report = new WeatherReport();
				} else if(report != null) {
					data = Utilities.getDataFromXmlTag(startTag, result.substring(i));
					report.populateData(startTag, data);
				}
				i=k;
			} else {
				i++;
			}
		}
		//Need to add the last one that we populated
		weatherReports.add(report);
		weatherReports = WeatherReport.sort(weatherReports);
	}
	
	public void fillInSpecificDayForecastWithReport(int dayNum, WeatherReport report) {
		View selectedDay;
		switch(dayNum) {
		case 1:
			selectedDay = getActivity().findViewById(R.id.day_one);
			break;
		case 2:
			selectedDay = getActivity().findViewById(R.id.day_two);
			break;
		case 3:
			selectedDay = getActivity().findViewById(R.id.day_three);
			break;
		default:
			return;
		}
		
		if(report != null) {
			((TextView)selectedDay.findViewById(R.id.date)).setText((report.gcTerrestialDate.get(Calendar.MONTH)+1) + "/" + report.gcTerrestialDate.get(Calendar.DAY_OF_MONTH));
			((TextView)selectedDay.findViewById(R.id.min_temp)).setText(String.valueOf(report.fMinTemp));
			((TextView)selectedDay.findViewById(R.id.max_temp)).setText(String.valueOf(report.fMaxTemp));
		} else {
			((TextView)selectedDay.findViewById(R.id.date)).setText("--/--");
			((TextView)selectedDay.findViewById(R.id.min_temp)).setText("--");
			((TextView)selectedDay.findViewById(R.id.max_temp)).setText("--");
		}
	}
	
	public void fillInSelectedForecastWithReport(WeatherReport report) {
		((TextView)getActivity().findViewById(R.id.sol)).setText("Sol: " + report.iSol);
		((TextView)getActivity().findViewById(R.id.terrestrial_date)).setText(DateFormat.getDateInstance().format(report.gcTerrestialDate.getTime()));
		((TextView)getActivity().findViewById(R.id.min_temp)).setText(String.valueOf(report.fMinTemp));
		((TextView)getActivity().findViewById(R.id.max_temp)).setText(String.valueOf(report.fMaxTemp));
		((TextView)getActivity().findViewById(R.id.pressure)).setText("Pressure:\n" + report.fPressure);
		((TextView)getActivity().findViewById(R.id.pressure_string)).setText("Pressure String:\n" + report.sPressureString);
		((TextView)getActivity().findViewById(R.id.absolute_humidity)).setText("Absolute Humidity:\n" + (report.fAbsoluteHumidity>0.0f?report.fAbsoluteHumidity:"--"));
		((TextView)getActivity().findViewById(R.id.wind_speed)).setText("Wind Speed:\n" + report.fWindSpeed);
		((TextView)getActivity().findViewById(R.id.wind_direction)).setText("Wind Direction:\n" + report.sWindDirection);
		((TextView)getActivity().findViewById(R.id.atmo_opacity)).setText(report.sAtmosphericOpacity);
		((TextView)getActivity().findViewById(R.id.season)).setText("Season:\n" + report.sSeason);
		((TextView)getActivity().findViewById(R.id.ls)).setText("ls:\n" + report.fls);
		((TextView)getActivity().findViewById(R.id.sunrise)).setText("Sunrise:\n" + report.sSunrise);
		((TextView)getActivity().findViewById(R.id.sunset)).setText("Sunset:\n" + report.sSunset);
	}
	
	public void showDatePicker() {
		GregorianCalendar now = new GregorianCalendar();
		new DatePickerDialog(getActivity(), new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				for(int i = 0; i < weatherReports.size(); i++) {
					WeatherReport report = weatherReports.get(i);
					if(report.gcTerrestialDate.get(Calendar.MONTH) == monthOfYear && report.gcTerrestialDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth && report.gcTerrestialDate.get(Calendar.YEAR) == year) {
						fillInSelectedForecastWithReport(report);
						fillInSpecificDayForecastWithReport(3, (i-1>=0?weatherReports.get(i-1):null));
						fillInSpecificDayForecastWithReport(2, (i-2>=0?weatherReports.get(i-2):null));
						fillInSpecificDayForecastWithReport(1, (i-3>=0?weatherReports.get(i-3):null));
						break;
					}
				}
			}
		}, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
	}
}