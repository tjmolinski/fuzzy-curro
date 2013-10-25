package com.tjm.rpr.weather;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.tjm.rpr.Utilities;

public class WeatherReport implements Serializable {
	private static final long serialVersionUID = 702864758944589348L;
	public int iSol;
	public GregorianCalendar gcTerrestialDate;
	public float fMinTemp;
	public float fMaxTemp;
	public float fPressure;
	public String sPressureString;
	public float fAbsoluteHumidity;
	public float fWindSpeed;
	public String sWindDirection;
	public String sAtmosphericOpacity;
	public String sSeason;
	public float fls;
	public String sSunrise;
	public String sSunset;
	
	public WeatherReport() { }

	public void populateData(String startTag, String data) {
		String nakedTag = startTag.substring(1, startTag.length()-1);
		
		if(data.contains("--") || data.contains("xx")) { return; }
		
		if(data.contains(",")) {
			data = data.replaceAll(",", ".");
		}
		
		/*
		 * Tags for 'nakedTag' can be found here
		 * http://cab.inta-csic.es/rems/rems_weather.xml
		 */
		if(nakedTag.equalsIgnoreCase("weather_report")) {
		} else if(nakedTag.equalsIgnoreCase("title")) {	
		} else if(nakedTag.equalsIgnoreCase("link")) {
		} else if(nakedTag.equalsIgnoreCase("sol")) {
			iSol = Integer.valueOf(data);
		} else if(nakedTag.equalsIgnoreCase("terrestrial_date")) {
			int[] dateObject = Utilities.valueOfStringDate(data);
			gcTerrestialDate = new GregorianCalendar();
			gcTerrestialDate.set(Calendar.MONTH, dateObject[0]);
			gcTerrestialDate.set(Calendar.DAY_OF_MONTH, dateObject[1]);
			gcTerrestialDate.set(Calendar.YEAR, dateObject[2]);
		} else if(nakedTag.equalsIgnoreCase("magnitudes")) {
		} else if(nakedTag.equalsIgnoreCase("min_temp")) {
			fMinTemp = Float.valueOf(data);
		} else if(nakedTag.equalsIgnoreCase("max_temp")) {
			fMaxTemp = Float.valueOf(data);
		} else if(nakedTag.equalsIgnoreCase("pressure")) {
			fPressure = Float.valueOf(data);
		} else if(nakedTag.equalsIgnoreCase("pressure_string")) {
			sPressureString = data;
		} else if(nakedTag.equalsIgnoreCase("abs_humidity")) {
			fAbsoluteHumidity = Float.valueOf(data);
		} else if(nakedTag.equalsIgnoreCase("wind_speed")) {
			fWindSpeed = Float.valueOf(data);
		} else if(nakedTag.equalsIgnoreCase("wind_direction")) {
			sWindDirection = data;
		} else if(nakedTag.equalsIgnoreCase("atmo_opacity")) {
			sAtmosphericOpacity = data;
		} else if(nakedTag.equalsIgnoreCase("season")) {
			sSeason = data;
		} else if(nakedTag.equalsIgnoreCase("ls")) {
			fls = Float.valueOf(data);
		} else if(nakedTag.equalsIgnoreCase("sunrise")) {
			sSunrise = data;
		} else if(nakedTag.equalsIgnoreCase("sunset")) {
			sSunset = data;
		}
	}

	public static ArrayList<WeatherReport> sort(ArrayList<WeatherReport> weatherReports) {
		WeatherReport[] clonedList = weatherReports.toArray(new WeatherReport[weatherReports.size()]);
		int size = weatherReports.size();

		//Insert sort
		for(int i = 2; i < size; i++) {
			for(int j = i; j > 1; j--) {
				long comp1 = clonedList[j-1].gcTerrestialDate.getTimeInMillis();
				long comp2 = clonedList[j].gcTerrestialDate.getTimeInMillis();
				if(comp2 < comp1) {
					WeatherReport temp = clonedList[j-1];
					clonedList[j-1] = clonedList[j];
					clonedList[j] = temp;
				}
			}
		}
		
		return new ArrayList<WeatherReport>(Arrays.asList(clonedList));
	}
}
