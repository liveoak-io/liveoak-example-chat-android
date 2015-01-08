package io.liveoak.helper;

import android.util.Log;

import org.jboss.aerogear.android.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import io.liveoak.helper.rest.DeleteTask;
import io.liveoak.helper.rest.PutTask;

/**
 * Created by mwringe on 01/05/14.
 */
public class PushSubscription {

    private final String logTag = PushSubscription.class.getSimpleName();

    private String resourcePath;
    private JSONObject message;
    private String alias;
    private String pushResourceName;
    private LiveOak liveOak;

    private String resourceName;


    public PushSubscription(LiveOak liveOak, String pushResourceName, String resourcePath, JSONObject message, String alias, String resourceName) {
        this.liveOak = liveOak;
        this.pushResourceName = pushResourceName;
        this.resourcePath = resourcePath;
        this.message = message;
        this.alias = alias;
        this.resourceName = resourceName;
    }

    public void subscribe(Callback<JSONObject> callback) {
        String subscribeResource = "/" + pushResourceName + "/aliases/" + alias + "/" + resourceName;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resource-path", resourcePath);
            jsonObject.put("message", message);

            jsonObject.put("alias", alias);
        } catch (Exception e) {
            Log.e(logTag, "Error trying to create subscribe json object", e);
            throw new RuntimeException(e);
        }

        liveOak.updateResource(subscribeResource, jsonObject, callback);
    }

    public void unsubscribe(String alias, Callback<JSONObject> callback) {
        String subscribeResource = "/" + pushResourceName + "/aliases/" + alias + "/foo";
        liveOak.deleteResource(subscribeResource, callback);
    }
}