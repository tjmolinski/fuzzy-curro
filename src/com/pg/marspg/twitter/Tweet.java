package com.pg.marspg.twitter;

import java.io.Serializable;

public class Tweet implements Serializable {
	
	private static final long serialVersionUID = 7173141779343117535L;
	public String username;
	public String message;
	
	public Tweet(String username, String message) {
		this.username = username;
	    this.message = message;
	}
}
