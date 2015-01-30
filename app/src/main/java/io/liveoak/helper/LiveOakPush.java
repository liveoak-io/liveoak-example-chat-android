package io.liveoak.helper;

import android.content.SharedPreferences;
import android.util.Base64;

import org.jboss.aerogear.android.Callback;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwringe on 29/05/14.
 */
public class LiveOakPush {

    protected String resourceName;
    protected Map<String, PushSubscription> subscriptions = new HashMap<>();
    protected LiveOakUPS liveOakUPS;

    private LiveOak liveOak;
    private String alias;

    private LiveOakPush(LiveOak liveOak, String resourceName, LiveOakUPS liveOakUPS) {
        this.liveOak = liveOak;
        this.resourceName = resourceName;
        this.liveOakUPS = liveOakUPS;
    }

    public static LiveOakPush create(LiveOak liveOak, JSONObject jsonObject) throws Exception {
        String resourceName = jsonObject.optString("resource-name", "push");

        LiveOakUPS liveOakUPS = null;
        if (jsonObject.has("ups-configuration")) {
            liveOakUPS = LiveOakUPS.create(liveOak, jsonObject.getJSONObject("ups-configuration"));
        }

        return new LiveOakPush(liveOak, resourceName, liveOakUPS);
    }

    void connect(final Callback<JSONObject> callback) {
        final SharedPreferences preferences = liveOak.getPreferences();
        alias = preferences.getString("alias", null);

        //Toast.makeText(liveOak.getContext(), "ALIAS : " + alias, Toast.LENGTH_LONG).show();
        //TODO: handle the situation where the alias exists in the preferences, but for some reason is deleted on LiveOak
        // eg: if alias exists locally but not on liveoak, then have liveoak generate a new local alias

        if (alias == null) {
            liveOak.createResource("/" + resourceName + "/aliases", new JSONObject(), new AliasCallBack(callback));
        } else {
            initializeUPS(alias, callback);
        }
    }

    public void subscribe(String resourcePath, JSONObject message, Callback<JSONObject> callback) {
        PushSubscription subscription = new PushSubscription(liveOak, resourceName, resourcePath, message, alias, Base64.encodeToString("resourcePath".getBytes(), Base64.DEFAULT));
        subscriptions.put(resourcePath, subscription);
        subscription.subscribe(callback);
    }

    public void unsubscribe(final String resourcePath, final Callback<JSONObject> callback) {
        PushSubscription subscription = subscriptions.get(resourcePath);
        subscription.unsubscribe(alias, new Callback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                subscriptions.remove(resourcePath);
                callback.onSuccess(jsonObject);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    class AliasCallBack implements Callback<JSONObject> {

        Callback<JSONObject> callback;

        AliasCallBack(Callback<JSONObject> callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess(JSONObject o) {
            alias = o.optString("id");

            //Save Alias to the System Shared Preferences.
            liveOak.getPreferences().edit().putString("alias", alias).commit();
            initializeUPS(alias, callback);
        }

        @Override
        public void onFailure(Exception e) {
            callback.onFailure(e);
        }
    }

    void initializeUPS(String alias, final Callback<JSONObject> callback) {
        liveOakUPS.setAlias(alias);

        liveOakUPS.connect(new Callback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject o) {
                callback.onSuccess(o);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    void disconnect(final Callback<JSONObject> callback) {
        liveOakUPS.disconnect(callback);
        //TODO: properly handle all the callbacks
        for (PushSubscription subscription : subscriptions.values()) {
            subscription.unsubscribe(alias, new Callback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    //TODO
                }

                @Override
                public void onFailure(Exception e) {
                    //TODO
                    e.printStackTrace();
                }
            });
        }
    }
}
