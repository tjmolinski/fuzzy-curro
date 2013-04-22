package com.pg.marspg.twitter;

import java.io.Serializable;

import android.graphics.Bitmap;

public class Tweet implements Serializable {
	
	private static final long serialVersionUID = 7173141779343117535L;
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
