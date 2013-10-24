package com.pg.marspg.fragments;

import java.util.ArrayList;

import twitter4j.ResponseList;
import twitter4j.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pg.marspg.Constants;
import com.pg.marspg.Dashboard;
import com.pg.marspg.R;
import com.pg.marspg.asynctasks.RetrieveTwitterData;
import com.pg.marspg.interfaces.OnRetrieveTweetsCompleted;
import com.pg.marspg.twitter.Tweet;
import com.pg.marspg.twitter.TweetItemAdapter;

public class TwitterFeed extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.twitter_feed, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getTwitterFeeds(view);
	}

	public ArrayList<Tweet> convertResultToTweets(ResponseList<Status> result) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		for(Status item : result) {
			tweets.add(new Tweet(item.getUser().getScreenName(), item.getText()));
		}
		return tweets;
	}

	public void getTwitterFeeds(final View v) {
		new RetrieveTwitterData(new OnRetrieveTweetsCompleted() {
			@Override
			public void onTaskCompleted(ResponseList<Status> result) {
				ArrayList<Tweet> curiosityFeed = convertResultToTweets(result);
				((Dashboard)getActivity()).setTweets(curiosityFeed);
				popluateFeedListViews(v, curiosityFeed);
			}
		}).execute(Constants.MARS_ROVER_TWITTER_HANDLE);
	}

	public void popluateFeedListViews(View v, ArrayList<Tweet> tweets) {
		ListView listView = (ListView) v.findViewById(R.id.twitterfeed_ListView);
		listView.setAdapter(new TweetItemAdapter(this.getActivity(), R.layout.listitem, tweets));
	}
}
