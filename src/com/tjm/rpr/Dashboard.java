package com.tjm.rpr;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.tjm.rpr.fragments.Information;
import com.tjm.rpr.fragments.TwitterFeed;
import com.tjm.rpr.fragments.Weather;
import com.tjm.rpr.twitter.Tweet;
import com.tjm.rpr.weather.WeatherReport;

public class Dashboard extends FragmentActivity {
	private TabHost mTabHost;
    private ViewPager  mViewPager;
    private GenericTabAdapter mTabsAdapter;

    //Going to cache these values so we don't have to repull
	private ArrayList<WeatherReport> weatherReports = new ArrayList<WeatherReport>();
	private ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	private Dialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dashboard);
		
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mViewPager = (ViewPager)findViewById(R.id.pager);

        LayoutInflater inflater = this.getLayoutInflater();
        RelativeLayout weatherTab = (RelativeLayout) inflater.inflate(R.layout.custom_tab, null);
        RelativeLayout feedTab = (RelativeLayout) inflater.inflate(R.layout.custom_tab, null);
        RelativeLayout informationTab = (RelativeLayout) inflater.inflate(R.layout.custom_tab, null);
        
        ((TextView) weatherTab.findViewById(R.id.title)).setText(getResources().getString(R.string.weather));
        ((TextView) feedTab.findViewById(R.id.title)).setText(getResources().getString(R.string.twitter_feed));
        ((TextView) informationTab.findViewById(R.id.title)).setText(getResources().getString(R.string.information));
        
        mTabsAdapter = new GenericTabAdapter(this, mTabHost, mViewPager);
        mTabsAdapter.addTab(mTabHost.newTabSpec(Constants.WEATHER_FRAGMENT_TAG)
        		.setIndicator(weatherTab), Weather.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec(Constants.TWITTER_FEED_FRAGMENT_TAG)
        		.setIndicator(feedTab), TwitterFeed.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec(Constants.INFORMATION_FRAGMENT_TAG)
        		.setIndicator(informationTab), Information.class, null);
	}
	
	public ArrayList<WeatherReport> getReports() {
		return weatherReports;
	}
	
	public void setReports(ArrayList<WeatherReport> reports) {
		weatherReports = new ArrayList<WeatherReport>();
		weatherReports = reports;
	}
	
	public ArrayList<Tweet> getTweets() {
		return tweets;
	}
	
	public void setTweets(ArrayList<Tweet> tweetList) {
		tweets = new ArrayList<Tweet>();
		tweets = tweetList;
	}

	public void setDialog(Dialog aDialog) {
		if(mDialog != null) {
			mDialog.dismiss();
		}
		
		mDialog = aDialog;
		mDialog.show();
	}
}
