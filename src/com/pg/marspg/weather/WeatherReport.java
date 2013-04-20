package com.pg.marspg.weather;


public class WeatherReport {
	
	public int iSol;
	public String sTerrestialDate;
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
	
	public WeatherReport() {
	}

	public void populateData(String startTag, String data) {
		String nakedTag = startTag.substring(1, startTag.length()-1);
		
		if(data.equalsIgnoreCase("--")) {
			return;
		}
		
		if(nakedTag.equalsIgnoreCase("weather_report")) {
		} else if(nakedTag.equalsIgnoreCase("title")) {	
		} else if(nakedTag.equalsIgnoreCase("link")) {
		} else if(nakedTag.equalsIgnoreCase("sol")) {
			iSol = Integer.valueOf(data);
		} else if(nakedTag.equalsIgnoreCase("terrestrial_date")) {
			sTerrestialDate = data;
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
}
