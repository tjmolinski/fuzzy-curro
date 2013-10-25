package com.tjm.rpr.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterConfigurator {
    protected Twitter twitter;
    private ConfigurationBuilder configBuilder;

    public TwitterConfigurator(){
        configBuilder = new ConfigurationBuilder();
        configBuilder.setDebugEnabled(true);
        configBuilder.setOAuthConsumerKey("Insert your consumer key here");
        configBuilder.setOAuthConsumerSecret("Insert your consumer secret here");
        configBuilder.setOAuthAccessToken("Insert your access token here");
        configBuilder.setOAuthAccessTokenSecret("Insert your access token secret here");

        //use the ConfigBuilder.build() method and pass the result to the TwitterFactory
        TwitterFactory tf = new TwitterFactory(configBuilder.build());
        //you can now get authenticated instance of Twitter object.
        twitter = tf.getInstance();
    }
    
    public Twitter getTwitter() {
    	return twitter;
    }
}
