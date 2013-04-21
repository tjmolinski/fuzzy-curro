package com.pg.marspg.fragments;

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
        
        (new RetrieveSiteData(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(String result) {
				parseXml(result);
				fillInFieldsWithReport(weatherReports.get(weatherReports.size()-1));
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
					if(report != null)
						weatherReports.add(report);
					report = new WeatherReport();
				} else if(report != null) {
					data = getDataFromTag(startTag, result.substring(i));
					report.populateData(startTag, data);
				}
				i=k;
			} else {
				i++;
			}
		}
		weatherReports.add(report);
	}
	
	public void fillInFieldsWithReport(WeatherReport report) {
		((TextView)getActivity().findViewById(R.id.sol)).setText("Sol: " + report.iSol);
		((TextView)getActivity().findViewById(R.id.terrestrial_date)).setText(report.sTerrestialDate);
		((TextView)getActivity().findViewById(R.id.min_temp)).setText("Min temp: " + report.fMinTemp);
		((TextView)getActivity().findViewById(R.id.max_temp)).setText("Max temp: " + report.fMaxTemp);
		((TextView)getActivity().findViewById(R.id.pressure)).setText("Pressure: " + report.fPressure);
		((TextView)getActivity().findViewById(R.id.pressure_string)).setText("Pressure String: " + report.sPressureString);
		((TextView)getActivity().findViewById(R.id.absolute_humidity)).setText("Absolute Humidity: " + (report.fAbsoluteHumidity>0.0f?report.fAbsoluteHumidity:"--"));
		((TextView)getActivity().findViewById(R.id.wind_speed)).setText("Wind Speed: " + report.fWindSpeed);
		((TextView)getActivity().findViewById(R.id.wind_direction)).setText("Wind Direction: " + report.sWindDirection);
		((TextView)getActivity().findViewById(R.id.atmo_opacity)).setText(report.sAtmosphericOpacity);
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
	
	public void showDatePicker() {
		GregorianCalendar now = new GregorianCalendar();
		new DatePickerDialog(getActivity(), new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				for(int i = 0; i < weatherReports.size(); i++) {
					WeatherReport report = weatherReports.get(i);
					int[] dateObject = Utilities.valueOfStringDate(report.sTerrestialDate);
					if(dateObject[0] == monthOfYear && dateObject[1] == dayOfMonth && dateObject[2] == year) {
						fillInFieldsWithReport(report);
						break;
					}
				}
			}
		}, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
	}

//	private void saveHistory(String result) throws IOException {
//		File sdCard = Environment.getExternalStorageDirectory();
//		File directory = new File (sdCard.getAbsolutePath() + Constants.SAVED_HISTORICAL_MARS_WEATHER_FILE_DIRECTORY);
//		directory.mkdirs();
//		File file = new File(directory, Constants.SAVED_HISTORICAL_MARS_WEATHER_FILE_NAME);
//		FileOutputStream fOut = new FileOutputStream(file);
//		OutputStreamWriter osw = new OutputStreamWriter(fOut);
//		osw.write(result);
//		osw.flush();
//		osw.close();
//	}
	
//	private String getContentsOfFile() {
//		File sdcard = Environment.getExternalStorageDirectory();
//
//		//Get the text file
//		File file = new File(sdcard.getAbsolutePath(), Constants.SAVED_HISTORICAL_MARS_WEATHER_FILE_PATH);
//
//		//Read text from file
//		StringBuilder text = new StringBuilder();
//
//		try {
//		    BufferedReader br = new BufferedReader(new FileReader(file));
//		    String line;
//
//		    while ((line = br.readLine()) != null) {
//		        text.append(line);
//		        text.append('\n');
//		    }
//		}
//		catch (IOException e) {
//		}
//		
//		return text.toString();
//	}
}