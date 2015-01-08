package io.liveoak.helper.rest;

import org.jboss.aerogear.android.Callback;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by mwringe on 01/05/14.
 */
public class PutTask extends RESTTask {

    public PutTask(Callback<JSONObject> callback) {
       super(callback);
    }

    @Override
    protected Object doInBackground(Object... params) {
        String uri = (String) params[0];
        JSONObject body = (JSONObject) params[1];

        try {
            return request(new URL(uri), Method.PUT, body);
        } catch (Exception e) {
            return e;
        }
    }

}