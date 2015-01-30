package io.liveoak.example.android.chat;

import android.app.Application;

import java.io.InputStream;

import io.liveoak.helper.LiveOak;

/**
 * Created by mwringe on 19/01/15.
 */
public class ChatApplication extends Application {

    LiveOak liveOak;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // Load the LiveOak configuration from the liveoak.json file
            InputStream liveOakJSONIS = getAssets().open("liveoak.json");
            this.liveOak = LiveOak.create(this.getApplicationContext(), liveOakJSONIS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Accessor method to retrieve the LiveOak object for this Application
     *
     * @return The application's LiveOak object
     */
    public LiveOak getLiveOak() {
        return this.liveOak;
    }

}
