package com.pg.marspg;

public class Utilities {
	public static String insertCharacterToString(String string, int position, char character) {
		if(position >= string.length()) return null;
		return string.substring(0,position) + character + string.substring(position);
	}
}
