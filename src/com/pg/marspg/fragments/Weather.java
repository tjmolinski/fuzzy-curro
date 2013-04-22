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
import com.pg.marspg.Dashboard;
import com.pg.marspg.OnTaskCompleted;
import com.pg.marspg.R;
import com.pg.marspg.RetrieveSiteData;
import com.pg.marspg.Utilities;
import com.pg.marspg.weather.WeatherReport;

public class Weather extends Fragment {
	private ArrayList<WeatherReport> weatherReports = new ArrayList<WeatherReport>();
	private WeatherReport currentReport;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.weather, container, false);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        
        //Caching the data
        if(((Dashboard)getActivity()).getReports().size() <= 0) {
        	getWeatherData();
        } else {
        	weatherReports = ((Dashboard)getActivity()).getReports();
			populateAllFieldsWithIndex(weatherReports.size()-1);
        }
        
        view.findViewById(R.id.selected_forecast).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePicker();
			}
		});
        
        view.findViewById(R.id.refresh_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getWeatherData();
			}
		});
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
		
		//Store the information into the fragment activity
		//so we aren't required to regrab the list when we
		//lose the fragment
		((Dashboard)getActivity()).setReports(WeatherReport.sort(weatherReports));
		weatherReports = ((Dashboard)getActivity()).getReports();
	}
	
	public void showDatePicker() {
		new DatePickerDialog(getActivity(), new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				for(int i = 0; i < weatherReports.size(); i++) {
					WeatherReport report = weatherReports.get(i);
					GregorianCalendar cal = new GregorianCalendar();
					cal.set(year, monthOfYear, dayOfMonth);
					
					if(i < 1 && cal.before(report.gcTerrestialDate)) {
						populateAllFieldsWithIndex(0);
						return;
					}
					
					if(report.gcTerrestialDate.get(Calendar.MONTH) == monthOfYear && report.gcTerrestialDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth && report.gcTerrestialDate.get(Calendar.YEAR) == year) {
						populateAllFieldsWithIndex(i);
						return;
					}
					
					if(report.gcTerrestialDate.after(cal)) {
						i--;
						populateAllFieldsWithIndex(i);
						return;
					}
				}
				
				//We are trying to access a reading thats ahead of the latest
				//so take the latest entry
				populateAllFieldsWithIndex(weatherReports.size()-1);
			}
		}, currentReport.gcTerrestialDate.get(Calendar.YEAR), currentReport.gcTerrestialDate.get(Calendar.MONTH), currentReport.gcTerrestialDate.get(Calendar.DAY_OF_MONTH)).show();
	}
	
	public void populateAllFieldsWithIndex(int i) {
		fillInSelectedForecastWithReport(weatherReports.get(i));
		fillInSpecificDayForecastWithReport(3, (i-1>=0?weatherReports.get(i-1):null));
		fillInSpecificDayForecastWithReport(2, (i-2>=0?weatherReports.get(i-2):null));
		fillInSpecificDayForecastWithReport(1, (i-3>=0?weatherReports.get(i-3):null));
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
		currentReport = report;
		((TextView)getActivity().findViewById(R.id.sol)).setText("Sol: " + report.iSol);
		((TextView)getActivity().findViewById(R.id.terrestrial_date)).setText(DateFormat.getDateInstance().format(report.gcTerrestialDate.getTime()));
		((TextView)getActivity().findViewById(R.id.min_temp)).setText(String.valueOf(report.fMinTemp) + getString(R.string.celcius));
		((TextView)getActivity().findViewById(R.id.max_temp)).setText(String.valueOf(report.fMaxTemp) + getString(R.string.celcius));
		((TextView)getActivity().findViewById(R.id.pressure)).setText(report.fPressure + " " + getString(R.string.pascals));
		((TextView)getActivity().findViewById(R.id.pressure_string)).setText(report.sPressureString);
		((TextView)getActivity().findViewById(R.id.absolute_humidity)).setText((report.fAbsoluteHumidity>0.0f?String.valueOf(report.fAbsoluteHumidity):"--"));
		((TextView)getActivity().findViewById(R.id.wind_speed)).setText(report.fWindSpeed + " " + getString(R.string.kmh));
		((TextView)getActivity().findViewById(R.id.wind_direction)).setText(report.sWindDirection);
		((TextView)getActivity().findViewById(R.id.atmo_opacity)).setText(report.sAtmosphericOpacity);
		((TextView)getActivity().findViewById(R.id.season)).setText(report.sSeason);
		((TextView)getActivity().findViewById(R.id.ls)).setText(report.fls + getString(R.string.degree));
		((TextView)getActivity().findViewById(R.id.sunrise)).setText(report.sSunrise);
		((TextView)getActivity().findViewById(R.id.sunset)).setText(report.sSunset);
	}
	
	private void getWeatherData() {
        (new RetrieveSiteData(getActivity(), new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(String result) {
				parseXml(result);
				populateAllFieldsWithIndex(weatherReports.size()-1);
			}
		})).execute(Constants.HISTORICAL_MARS_WEATHER_SITE);
	}
}