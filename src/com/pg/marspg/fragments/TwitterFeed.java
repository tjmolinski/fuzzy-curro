package com.pg.marspg.fragments;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.array;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pg.marspg.OnTaskCompleted;
import com.pg.marspg.R;
import com.pg.marspg.RetrieveSiteData;
import com.pg.marspg.twitter.Tweet;
import com.pg.marspg.twitter.TweetItemAdapter;

public class TwitterFeed extends Fragment {
	private ArrayList<Tweet> curiosityFeed;
	private ArrayList<Tweet> jplFeed;
	
	private String curiosityFeedURL = "http://search.twitter.com/search.json?q=from%3Amarscuriosity&rpp=15&page=1";
	private String jplFeedURL = "http://search.twitter.com/search.json?q=@NASAJPL&rpp=15&page=1";
	
	//from%3Amarscuriosity
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	View v = inflater.inflate(R.layout.twitter_feed, container, false);
    	
    	getTwitterFeeds( v);
        return v;
    }
    
    public ArrayList<Tweet> convertJsonToFeedArray(String JsonString) throws JSONException
    {
    	// Get Json
    	JSONObject head = new JSONObject(JsonString);
    	//JSONObject test
    	JSONArray totalResults = head.getJSONArray("results");
    	
    	// Convert into tweet Object
    	ArrayList<Tweet> tweets = new ArrayList<Tweet>();
    	Tweet tempTweet;
    	JSONObject curResult;
    	String username, imageurl, text;
    	Bitmap bmp;
    	for(int i=0; i< totalResults.length(); i++)
    	{
    		curResult = totalResults.getJSONObject(i);
    		// username
    		username = curResult.getString("from_user");
    		// image
    		imageurl = curResult.getString("profile_image_url"); 
    		// text
    		text = curResult.getString("text");
    		tempTweet = new Tweet( username, text, imageurl);
    		tweets.add( tempTweet );
    	}
    	
    	return tweets;
    }
    
    public void getTwitterFeeds( final View v)
    {
    	Log.e("getTwitterFeeds", "In getTwitterFeeds");
    	(new RetrieveSiteData(getActivity(), new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(String result) {
				try
				{
					curiosityFeed = convertJsonToFeedArray(result);
					popluateFeedListViews( v, curiosityFeed );		
				}
				catch(JSONException e)
				{
					// Dun Goofed
					Log.e("Dun Goofed", e.toString());
				}
			}
		})).execute(curiosityFeedURL);
    	/*
    	(new RetrieveSiteData(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(String result) {
				try
				{
					jplFeed = convertJsonToFeedArray(result);
				}
				catch(JSONException e)
				{
					// Dun Goofed
				}
			}
		})).execute(jplFeedURL);
		*/
    }
    public void popluateFeedListViews( View v, ArrayList<Tweet> tweets)
    {
    	ListView listView = (ListView) v.findViewById(R.id.twitterfeed_ListView);
		listView.setAdapter(new TweetItemAdapter(this.getActivity(), R.layout.listitem, tweets));
    }
}
