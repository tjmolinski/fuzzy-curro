package com.pg.marspg.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pg.marspg.Dashboard;
import com.pg.marspg.OnTaskCompleted;
import com.pg.marspg.R;
import com.pg.marspg.RetrieveSiteData;
import com.pg.marspg.twitter.Tweet;
import com.pg.marspg.twitter.TweetItemAdapter;

public class TwitterFeed extends Fragment {
	private ArrayList<Tweet> curiosityFeed = new ArrayList<Tweet>();

	private String curiosityFeedURL = "http://search.twitter.com/search.json?q=from%3Amarscuriosity&rpp=15&page=1";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.twitter_feed, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		getTwitterFeeds(view);
	}

	public ArrayList<Tweet> convertJsonToFeedArray(String JsonString)
			throws JSONException {
		// Get Json
		JSONObject head = new JSONObject(JsonString);
		// JSONObject test
		JSONArray totalResults = head.getJSONArray("results");

		// Convert into tweet Object
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		Tweet tempTweet;
		JSONObject curResult;
		String username, imageurl, text;
		Bitmap bmp;
		for (int i = 0; i < totalResults.length(); i++) {
			curResult = totalResults.getJSONObject(i);
			// username
			username = curResult.getString("from_user");
			// image
			imageurl = curResult.getString("profile_image_url");
			// text
			text = curResult.getString("text");
			tempTweet = new Tweet(username, text, imageurl);
			tweets.add(tempTweet);
		}

		return tweets;
	}

	public void getTwitterFeeds(final View v) {
		Log.e("getTwitterFeeds", "In getTwitterFeeds");
		if (((Dashboard) getActivity()).getTweets().size() <= 0) {
			(new RetrieveSiteData(getActivity(), new OnTaskCompleted() {
				@Override
				public void onTaskCompleted(String result) {
					try {
						curiosityFeed = convertJsonToFeedArray(result);
						((Dashboard)getActivity()).setTweets(curiosityFeed);
						popluateFeedListViews(v, curiosityFeed);
					} catch (JSONException e) {
						// Dun Goofed
						Log.e("Dun Goofed", e.toString());
					}
				}
			})).execute(curiosityFeedURL);
		} else {
			curiosityFeed = ((Dashboard) getActivity()).getTweets();
			popluateFeedListViews(v, curiosityFeed);
		}
	}

	public void popluateFeedListViews(View v, ArrayList<Tweet> tweets) {
		ListView listView = (ListView) v
				.findViewById(R.id.twitterfeed_ListView);
		listView.setAdapter(new TweetItemAdapter(this.getActivity(),
				R.layout.listitem, tweets));
	}
}
