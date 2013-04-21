package com.pg.marspg.twitter;

import android.graphics.Bitmap;

public class Tweet {
	
	public String username;
	public String message;
	public String image_url;
	public Bitmap image;
	
	public Tweet(String username, String message, String url) 
	{
		this.username = username;
	    this.message = message;
	    this.image_url = url;
	}

}
