package com.tjm.rpr.fragments;

import java.util.ArrayList;

import twitter4j.ResponseList;
import twitter4j.Status;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.tjm.rpr.Constants;
import com.tjm.rpr.Dashboard;
import com.tjm.rpr.R;
import com.tjm.rpr.Utilities;
import com.tjm.rpr.asynctasks.RetrieveTwitterData;
import com.tjm.rpr.interfaces.OnRetrieveTweetsCompleted;
import com.tjm.rpr.twitter.Tweet;
import com.tjm.rpr.twitter.TweetItemAdapter;

public class TwitterFeed extends Fragment {
	
	private Button btnRefresh;
	private ListView lvTwitterFeed;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.twitter_feed, container, false);
		View footer = View.inflate(getActivity(), R.layout.footer_refresh_button, null);
    	
		lvTwitterFeed = (ListView) layout.findViewById(R.id.twitterfeed_ListView);
    	lvTwitterFeed.addFooterView(footer);
    	btnRefresh = (Button) footer;
    	
		return layout;
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getTwitterFeeds(view);
        
        btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getTwitterFeeds(view);
			}
		});
	}

	public ArrayList<Tweet> convertResultToTweets(ResponseList<Status> result) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		for(Status item : result) {
			tweets.add(new Tweet(item.getUser().getScreenName(), item.getText()));
		}
		return tweets;
	}

	public void getTwitterFeeds(final View v) {
		if(!Utilities.hasNetworkConnected(getActivity())) {
			AlertDialog aDialog = Utilities.createConnectionIssueDialog(getActivity());
			((Dashboard)getActivity()).setDialog(aDialog);
			return;
		}
		
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
