package com.pg.marspg.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterConfigurator {
    protected Twitter twitter;
    private ConfigurationBuilder configBuilder;

    public TwitterConfigurator(){
        configBuilder = new ConfigurationBuilder();
        configBuilder.setDebugEnabled(true);        
        configBuilder.setOAuthConsumerKey("[consumer key here]");
        configBuilder.setOAuthConsumerSecret("[consumer secret key here]");
        configBuilder.setOAuthAccessToken("[OAuthAccessToken here]");
        configBuilder.setOAuthAccessTokenSecret("[secret OAuthAccessToken here]");

        //use the ConfigBuilder.build() method and pass the result to the TwitterFactory
        TwitterFactory tf = new TwitterFactory(configBuilder.build());
        //you can now get authenticated instance of Twitter object.
        twitter = tf.getInstance();
    }
    
    public Twitter getTwitter() {
    	return twitter;
    }
}
