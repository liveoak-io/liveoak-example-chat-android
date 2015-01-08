package io.liveoak.android.chat;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.jboss.aerogear.android.unifiedpush.PushConfig;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.liveoak.helper.LiveOak;
import io.liveoak.helper.PushSubscription;

/**
 * Created by mwringe on 28/02/14.
 */
public class ChatApplication extends Application {

    // a list of chats not yet handled by the main application. These include chats received
    // by the NotificationHandler when the main application is not in focus.
    List<Chat> pendingChats = new ArrayList<Chat>();

    // file name of the application preferences
    public static final String APP_PREFERENCE_FILENAME = "liveoak-chat";
    // key to store the username in the preferences.
    public static final String USERNAME_KEY = "username";

    private LiveOak liveOak;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // Load the LiveOak configuration from the liveoak.json file
            InputStream liveOakJSONIS = getAssets().open("liveoak.json");
            this.liveOak = LiveOak.create(this.getApplicationContext(),liveOakJSONIS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Accessor method to retrieve the LiveOak object for this Application
     * @return The application's LiveOak object
     */
    public LiveOak getLiveOak() {
        return this.liveOak;
    }

    /**
     * Get a list of chats not yet handled by the main chat application
     * @return The list of pending chats
     */
    public List<Chat> getPendingChats() {
        return this.pendingChats;
    }
}
