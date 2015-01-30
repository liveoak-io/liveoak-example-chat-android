package io.liveoak.helper;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.unifiedpush.PushConfig;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

/**
 * Created by mwringe on 01/05/14.
 */
public class LiveOakUPS {

    private PushConfig pushConfig;
    private PushRegistrar registration = null;
    private LiveOak liveOak;

    private LiveOakUPS(LiveOak liveOak, PushConfig pushConfig) {
        this.liveOak = liveOak;
        this.pushConfig = pushConfig;
    }

    public static LiveOakUPS create(LiveOak liveOak, JSONObject jsonObject) throws Exception {
        PushConfig pushConfig = generatePushConfig(jsonObject);

        return new LiveOakUPS(liveOak, pushConfig);
    }

    private static PushConfig generatePushConfig(JSONObject jsonObject) throws Exception {
        String upsURL = jsonObject.getString("ups-url");
        JSONArray jsonArray = jsonObject.getJSONArray("gcm-sender-id");
        String[] gcmIds = new String[jsonArray.length()];
        for (int i = 0; i < gcmIds.length; i++) {
            gcmIds[i] = (String) jsonArray.get(i);
        }

        PushConfig pushConfig = new PushConfig(new URI(upsURL), gcmIds);

        pushConfig.setVariantID(jsonObject.optString("variant-id"));
        pushConfig.setSecret(jsonObject.optString("variant-secret"));
        /* TODO: add in all the other stuff here:

        pushConfig.setDeviceType();
        pushConfig.setOperatingSystem(Build.DEVICE.);
        pushConfig.setOsVersion(Build.VERSION.RELEASE);
        */

        return pushConfig;
    }

    void setAlias(String alias) {
        this.pushConfig.setAlias(alias);
    }

    void connect(final Callback<JSONObject> callback) {
        if (registration == null) {
            registration = new Registrations().push("liveoak", pushConfig);
        }
        registration.register(liveOak.getContext(), new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                JSONObject response = new JSONObject();
                try {
                    response.put("ups-registered", true);
                } catch (JSONException e) {
                    // TODO: log a message about this
                }
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    void disconnect(final Callback<JSONObject> callback) {
        if (registration != null) {
            registration.unregister(liveOak.getContext(), new Callback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    JSONObject response = new JSONObject();
                    try {
                        response.put("ups-unregistered", true);
                    } catch (JSONException e) {
                        // TODO: log a message about this
                    }
                    callback.onSuccess(response);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.onFailure(e);
                }
            });
            registration = null;
        }
    }
}
