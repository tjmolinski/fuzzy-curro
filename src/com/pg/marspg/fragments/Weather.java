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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pg.marspg.Constants;
import com.pg.marspg.Dashboard;
import com.pg.marspg.R;
import com.pg.marspg.Utilities;
import com.pg.marspg.asynctasks.RetrieveSiteData;
import com.pg.marspg.interfaces.OnRetrieveSiteDataCompleted;
import com.pg.marspg.weather.WeatherReport;

public class Weather extends Fragment {
	
	private ArrayList<WeatherReport> weatherReports = new ArrayList<WeatherReport>();
	private WeatherReport currentReport;
	
	private TextView tvSol;
	private TextView tvTerrestialDate;
	private TextView tvMinTemp;
	private TextView tvMaxTemp;
	private TextView tvPressure;
	private TextView tvPressureString;
	private TextView tvAbsoluteHumidity;
	private TextView tvWindSpeed;
	private TextView tvWindDirection;
	private TextView tvAtmoOpacity;
	private TextView tvSeason;
	private TextView tvLS;
	private TextView tvSunrise;
	private TextView tvSunset;
	
	private Button btnRefresh;
	private LinearLayout llSelectedForecast;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout =  inflater.inflate(R.layout.weather, container, false);
        
    	tvSol = ((TextView)layout.findViewById(R.id.sol));
    	tvTerrestialDate = ((TextView)layout.findViewById(R.id.terrestrial_date));
    	tvMinTemp = ((TextView)layout.findViewById(R.id.min_temp));
    	tvMaxTemp = ((TextView)layout.findViewById(R.id.max_temp));
    	tvPressure = ((TextView)layout.findViewById(R.id.pressure));
    	tvPressureString = ((TextView)layout.findViewById(R.id.pressure_string));
    	tvAbsoluteHumidity = ((TextView)layout.findViewById(R.id.absolute_humidity));
    	tvWindSpeed = ((TextView)layout.findViewById(R.id.wind_speed));
    	tvWindDirection = ((TextView)layout.findViewById(R.id.wind_direction));
    	tvAtmoOpacity = ((TextView)layout.findViewById(R.id.atmo_opacity));
    	tvSeason = ((TextView)layout.findViewById(R.id.season));
    	tvLS = ((TextView)layout.findViewById(R.id.ls));
    	tvSunrise = ((TextView)layout.findViewById(R.id.sunrise));
    	tvSunset = ((TextView)layout.findViewById(R.id.sunset));
    	
    	llSelectedForecast = (LinearLayout) layout.findViewById(R.id.selected_forecast);
    	btnRefresh = (Button) layout.findViewById(R.id.refresh_button);
    	
        return layout;
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
        
        llSelectedForecast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePicker();
			}
		});
        
        btnRefresh.setOnClickListener(new OnClickListener() {
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
					
					if(report.gcTerrestialDate.get(Calendar.MONTH) == monthOfYear && 
							report.gcTerrestialDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth && 
							report.gcTerrestialDate.get(Calendar.YEAR) == year) {
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
		tvSol.setText("Sol: " + report.iSol);
		tvTerrestialDate.setText(DateFormat.getDateInstance().format(report.gcTerrestialDate.getTime()));
		tvMinTemp.setText(String.valueOf(report.fMinTemp) + getString(R.string.celcius));
		tvMaxTemp.setText(String.valueOf(report.fMaxTemp) + getString(R.string.celcius));
		tvPressure.setText(report.fPressure + " " + getString(R.string.pascals));
		tvPressureString.setText(report.sPressureString);
		tvAbsoluteHumidity.setText((report.fAbsoluteHumidity>0.0f?String.valueOf(report.fAbsoluteHumidity):"--"));
		tvWindSpeed.setText(report.fWindSpeed + " " + getString(R.string.kmh));
		tvWindDirection.setText(report.sWindDirection);
		tvAtmoOpacity.setText(report.sAtmosphericOpacity);
		tvSeason.setText(report.sSeason);
		tvLS.setText(report.fls + getString(R.string.degree));
		tvSunrise.setText(report.sSunrise);
		tvSunset.setText(report.sSunset);
	}
	
	private void getWeatherData() {
        new RetrieveSiteData(getActivity(), new OnRetrieveSiteDataCompleted() {
			@Override
			public void onTaskCompleted(String result) {
				parseXml(result);
				populateAllFieldsWithIndex(weatherReports.size()-1);
			}
		}).execute(Constants.HISTORICAL_MARS_WEATHER_SITE);
	}
}