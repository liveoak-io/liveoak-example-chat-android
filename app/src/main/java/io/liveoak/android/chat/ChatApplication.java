package io.liveoak.android.chat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.jboss.aerogear.android.unifiedpush.PushConfig;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import io.liveoak.helper.LiveOak;

/**
 * Created by mwringe on 28/02/14.
 */
public class ChatApplication extends Application {

    /**********************************************************/
    /*           Application Configuration Setting            */
    /**
     * ******************************************************
     */

    // UPS Settings
    private static final String UPS_URL = <INSERT UPS URL HERE>; //eg "http://myHost//ag-push;
    private final String VARIANT_ID = <INSERT VARIANT ID HERE>;
    private final String SECRET = <INSERT VARIANT SECRET HERE>;
    private final String GCM_SENDER_ID = <INSERT GCM NUMBER HERE>;


    // LiveOak Settings
    private static final String LIVEOAK_HOST = <INSERT LIVEOAK HOST HERE>; //eg hostname or ip address;
    private static final int LIVEOAK_PORT = 8080; //eg 8080;
    private static final String APPLICATION_NAME = "chat-html";
    // LiveOak PushSubscription Settings
    private static final String UPS_RESOURCE_NAME = "push";
    private static final String RESOURCE_SUBSCRIPTION = "/storage/chat/*";
    private static final String MESSAGE = "{'alert': 'New message', 'title':'LiveOak Chat'}";


    // A unique alias for this particular application instance.
    protected String alias;

    /**
     * ******************************************************
     */

    public static final String LIVEOAK_PREFERENCE_KEY = "liveoak";
    public static final String USERNAME_KEY = "username";

    private PushRegistrar registration;
    private LiveOak liveOak;
    private LiveOak.PushSubscription subscription;

    @Override
    public void onCreate() {
        super.onCreate();

        // if the alias doesn't already exist, then create it
        SharedPreferences preferences = getSharedPreferences(this.getClass().getSimpleName(), Context.MODE_PRIVATE);
        alias = preferences.getString("alias", null);
        if (alias == null) {
            alias = UUID.randomUUID().toString();
            preferences.edit().putString("alias", alias).commit();
        }

        // Register with application with UPS
        Registrations registrations = new Registrations();
        try {
            PushConfig config = new PushConfig(new URI(UPS_URL), GCM_SENDER_ID);
            config.setVariantID(VARIANT_ID);
            config.setSecret(SECRET);
            config.setAlias(alias);

            registration = registrations.push("unifiedpush", config);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Setup the LiveOak instance and create a subscription for it
        this.liveOak = new LiveOak(LIVEOAK_HOST, LIVEOAK_PORT, APPLICATION_NAME);

        try {
            JSONObject message = new JSONObject(MESSAGE);
            this.subscription = liveOak.createPushSubscription(UPS_RESOURCE_NAME, RESOURCE_SUBSCRIPTION, message, alias);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Accessor method for Activities to access the 'PushRegistrar' object
    public PushRegistrar getRegistration() {
        return registration;
    }

    // Accessor method for Activities to access the Server URL for the LiveOak instance
    public LiveOak getLiveOak() {
        return this.liveOak;
    }

    // Accessor method for Activities to access the subscription for receiving chats
    public LiveOak.PushSubscription getSubscription() {
        return this.subscription;
    }
}
