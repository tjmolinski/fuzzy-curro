package com.pg.marspg.twitter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import com.pg.marspg.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class TweetItemAdapter extends ArrayAdapter<Tweet> {
  private ArrayList<Tweet> tweets;
private int resourceID;

  public TweetItemAdapter(Context context, int textViewResourceId, ArrayList<Tweet> tweets) {
    super(context, textViewResourceId, tweets);
    this.tweets = tweets;
  }
  
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View v = convertView;
    if (v == null) {
    	
    	LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(this.getContext().LAYOUT_INFLATER_SERVICE);
    	v = vi.inflate(R.layout.listitem, null);
    }

    Tweet tweet = tweets.get(position);
    if (tweet != null) {
    	TextView username = (TextView) v.findViewById(R.id.ListItem_username);
      	TextView message = (TextView) v.findViewById(R.id.ListItem_message);
      	ImageView image = (ImageView) v.findViewById(R.id.ListItem_avatar);

      	if (username != null) {
    	  username.setText(tweet.username);
      	}

  	if(message != null) {
    	  message.setText(tweet.message);
  	}

  	if(image != null) {
  		image.setImageBitmap( getBitmap(tweet.image_url));
      	}
    }

    return v;
  }
  

public Bitmap getBitmap(String bitmapUrl) {
  try {
    URL url = new URL(bitmapUrl);
    return BitmapFactory.decodeStream(url.openConnection().getInputStream());
  }
  catch(Exception ex) {return null;}
}

}
