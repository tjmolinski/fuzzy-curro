package com.pg.marspg;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class RetrieveSiteData extends AsyncTask<String, Void, String> {
	private OnTaskCompleted completedListener;
	
	public RetrieveSiteData(OnTaskCompleted listener) {
		completedListener = listener;
	}
	
	@Override
	protected String doInBackground(String... urls) {
	    StringBuilder builder = new StringBuilder(100000);

	    for (String url : urls) {
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet(url);
	        try {
	            HttpResponse execute = client.execute(httpGet);
	            InputStream content = execute.getEntity().getContent();

	            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
	            String s = "";
	            while ((s = buffer.readLine()) != null) {
	                builder.append(s);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    return builder.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		completedListener.onTaskCompleted(result);
	}
}