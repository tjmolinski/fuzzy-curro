package com.pg.marspg;

import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class RetrieveTwitterData extends
		AsyncTask<String, Void, ResponseList<twitter4j.Status>> {
	private ProgressDialog bar;
	private Context mContext;

	public RetrieveTwitterData(Context context) {
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		bar = new ProgressDialog(mContext);
		bar.setMessage("Get THOSE TWEETS");
		bar.setIndeterminate(true);
		bar.show();

	}

	@Override
	protected ResponseList<twitter4j.Status> doInBackground(String... arg0) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		//Need to obscure this
				.setOAuthConsumerKey("")
				.setOAuthConsumerSecret("")
				.setOAuthAccessToken("")
				.setOAuthAccessTokenSecret("");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		try {
			String[] srch = new String[] { "MarsCuriosity" };
			ResponseList<User> users = twitter.lookupUsers(srch);
			for (User user : users) {
				System.out.println("Friend's Name " + user.getName()); 
				if (user.getStatus() != null) {
					System.out.println("Friend timeline");
					
					ResponseList<twitter4j.Status> statusess = twitter.getUserTimeline(srch[0]);
					return statusess;
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	protected void onPostExecute(ResponseList<twitter4j.Status> result) {
		if (result != null) {
			bar.setMessage(result.get(0).getText());
		} else {
			bar.setMessage("NOTHING");
		}
	}
}
