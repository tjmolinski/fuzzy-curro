package com.tjm.rpr;

import java.util.Locale;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utilities {
	public static String insertCharacterToString(String string, int position, char character) {
		if(position >= string.length()) return null;
		return string.substring(0,position) + character + string.substring(position);
	}
	
	public static int[] valueOfStringDate(String date) {
		int[] dateObject = new int[3];
		String[] brokenUpDate = date.split(" ");

		brokenUpDate[0] = brokenUpDate[0].trim().replaceAll(",", "").replace(".", "").toLowerCase(Locale.US);
		brokenUpDate[1] = brokenUpDate[1].trim().replaceAll(",", "").replace(".", "").toLowerCase(Locale.US);
		brokenUpDate[2] = brokenUpDate[2].trim().replaceAll(",", "").replace(".", "").toLowerCase(Locale.US);
		
		//Handling metric date format
		if(Pattern.matches("[a-zA-Z]+", brokenUpDate[1])) {
			String tmp = brokenUpDate[0];
			brokenUpDate[0] = brokenUpDate[1];
			brokenUpDate[1] = tmp;
		}
		
		if(brokenUpDate[0].contains("jan") || brokenUpDate[0].contains("ene")) {
			dateObject[0] = 0;
		}else if(brokenUpDate[0].contains("feb")) {
			dateObject[0] = 1;
		}else if(brokenUpDate[0].contains("mar")) {
			dateObject[0] = 2;
		}else if(brokenUpDate[0].contains("apr")) {
			dateObject[0] = 3;
		}else if(brokenUpDate[0].contains("may")) {
			dateObject[0] = 4;
		}else if(brokenUpDate[0].contains("jun")) {
			dateObject[0] = 5;
		}else if(brokenUpDate[0].contains("jul")) {
			dateObject[0] = 6;
		}else if(brokenUpDate[0].contains("aug")) {
			dateObject[0] = 7;
		}else if(brokenUpDate[0].contains("sept")) {
			dateObject[0] = 8;
		}else if(brokenUpDate[0].contains("oct")) {
			dateObject[0] = 9;
		}else if(brokenUpDate[0].contains("nov")) {
			dateObject[0] = 10;
		}else if(brokenUpDate[0].contains("dec")) {
			dateObject[0] = 11;
		}

		dateObject[1] = Integer.valueOf(brokenUpDate[1]);
		dateObject[2] = Integer.valueOf(brokenUpDate[2]);
		
		return dateObject;
	}

	public static String getDataFromXmlTag(String startTag, String result) {
		String data = "";
		String closeTag = Utilities.insertCharacterToString(startTag, 1, '/');
		int tagSize = closeTag.length();
		int subPtr = 0;
		
		if(result.trim().length() > 0) {
			while(!result.substring(subPtr,subPtr+tagSize).equalsIgnoreCase(closeTag)) {
				subPtr++;
				if(subPtr+tagSize >= result.length()) break;
			}
	
			if(subPtr < result.length()) {
				data = result.substring(startTag.length(), subPtr);
			}
		}
		
		return data;
	}
	
	public static boolean hasNetworkConnected(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		return mWifi.isConnected() || mMobile.isConnected();
	}
	
	public static AlertDialog createConnectionIssueDialog(Context context) {
		Builder dBuilder = new Builder(context);
		dBuilder.setMessage(context.getString(R.string.no_network_connection));
		dBuilder.setTitle(context.getString(R.string.no_network_connection_title));
		return dBuilder.create();
	}
}
