package com.pg.marspg.asynctasks;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.User;
import android.os.AsyncTask;

import com.pg.marspg.Constants;
import com.pg.marspg.interfaces.OnRetrieveTweetsCompleted;
import com.pg.marspg.twitter.TwitterConfigurator;

public class RetrieveTwitterData extends AsyncTask<String, Void, ResponseList<twitter4j.Status>> {
	private OnRetrieveTweetsCompleted completedListener;

	public RetrieveTwitterData(OnRetrieveTweetsCompleted listener) {
		completedListener = listener;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected ResponseList<twitter4j.Status> doInBackground(String... args) {
		if(args[0] == null || args[0].length() <= 0) { return null; } 
		
		try {
			Twitter twitter = new TwitterConfigurator().getTwitter();
			String[] srch = new String[] { Constants.MARS_ROVER_TWITTER_HANDLE };
			ResponseList<User> users = twitter.lookupUsers(srch);
			for(int i = 0; i < users.size(); i++) {
				User user = users.get(i);
				if (user.getScreenName().equals(Constants.MARS_ROVER_TWITTER_HANDLE)) {
					ResponseList<twitter4j.Status> statusess = twitter.getUserTimeline(user.getScreenName());
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
		completedListener.onTaskCompleted(result);
	}
}
