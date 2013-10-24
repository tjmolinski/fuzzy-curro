package com.pg.marspg.twitter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pg.marspg.R;

public class TweetItemAdapter extends ArrayAdapter<Tweet> {
  private ArrayList<Tweet> tweets;

  public TweetItemAdapter(Context context, int textViewResourceId, ArrayList<Tweet> tweets) {
    super(context, textViewResourceId, tweets);
    this.tweets = tweets;
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View v = convertView;
    
    /**
     * TODO: Change to view holders
     */
    if (v == null) {
    	LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	v = vi.inflate(R.layout.listitem, null);
    }

    Tweet tweet = tweets.get(position);
    if (tweet != null) {
    	TextView username = (TextView) v.findViewById(R.id.ListItem_username);
      	TextView message = (TextView) v.findViewById(R.id.ListItem_message);

      	if (username != null) {
      		username.setText(tweet.username);
      	}

	  	if(message != null) {
	  		message.setText(tweet.message);
	  	}
    }

    return v;
    }
}
