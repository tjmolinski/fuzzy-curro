package com.pg.marspg.interfaces;

import twitter4j.ResponseList;

public interface OnRetrieveTweetsCompleted {
	void onTaskCompleted(ResponseList<twitter4j.Status> result);
}
