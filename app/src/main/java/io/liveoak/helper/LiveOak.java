package io.liveoak.helper;

import android.content.Context;
import android.content.SharedPreferences;

import org.jboss.aerogear.android.Callback;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import io.liveoak.helper.rest.DeleteTask;
import io.liveoak.helper.rest.GetTask;
import io.liveoak.helper.rest.PostTask;
import io.liveoak.helper.rest.PutTask;

/**
 * Created by mwringe on 28/02/14.
 */
public class LiveOak {

    public final String LIVEOAK_URL;
    public final String APPLICATION_NAME;

    // The key for the liveoak specific shared preferences.
    final static String LIVEOAK_SHARED_PREFERENCES = "liveoak.io";

    private Context context;
    private final String logTag = LiveOak.class.getSimpleName();

    private LiveOakPush liveOakPush;

    private LiveOak(Context context, URL url, String applicationName) {
        this.context = context;
        this.LIVEOAK_URL = url.toString();
        this.APPLICATION_NAME = applicationName;
    }

    public static LiveOak create(Context context, InputStream jsonInputStream) throws Exception {

        BufferedReader bReader = new BufferedReader(new InputStreamReader(jsonInputStream));
        StringBuilder json = new StringBuilder();
        String line = bReader.readLine();
        while (line != null) {
            json.append(line);
            line = bReader.readLine();
        }

        JSONTokener jsonTokener = new JSONTokener(json.toString());
        JSONObject jsonObject = new JSONObject(jsonTokener);
        return create(context, jsonObject);
    }

    public static LiveOak create(Context context, JSONObject jsonObject) throws Exception {
        String url = jsonObject.optString("liveoak-url");
        String applicationName = jsonObject.optString("application-name");

        LiveOak liveOak = new LiveOak(context, new URL(url), applicationName);

        if (jsonObject.has("push")) {
            LiveOakPush liveOakPush = LiveOakPush.create(liveOak, jsonObject.getJSONObject("push"));
            liveOak.setPush(liveOakPush);
        }

        return liveOak;
    }

    private void setPush(LiveOakPush liveOakPush) {
        //get the alias fro LiveOak

        //
        this.liveOakPush = liveOakPush;
    }

    //TODO: find a better name. SignIn? SignOut?
    // Should eventually handle things like authorization...

    // Setup everything with the LiveOak instance
    public void connect(Callback<JSONObject> callback) {
        //get the alias value from the LiveOak instance

        this.liveOakPush.connect(callback);
    }

    // disconnect everything from the liveOak instance
    public void disconnect() {

    }

    Context getContext() {
        return this.context;
    }

    SharedPreferences getPreferences() {
        return context.getSharedPreferences(LiveOak.LIVEOAK_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * Retrieves a JSON representation of a particular resource
     *
     * @param resourceURL The path of the resource to retreive
     * @return The JSON representation of the resource
     * @throws Exception
     */
    public void getResource(String resourceURL, Callback<JSONObject> callback) {
        String url = resourceURL;
        // handle relativeURLs if applicable
        if (resourceURL.startsWith("/")) {
            url = LIVEOAK_URL + "/" + APPLICATION_NAME + resourceURL;
        }

        new GetTask(callback).execute(url);
    }

    /**
     * Creates a resource from a JSON object
     *
     * @param uri        The URI of the resources parent
     * @param jsonObject A JSON representation of the resource
     * @return The newly created resource
     */
    public void createResource(String uri, JSONObject jsonObject, Callback<JSONObject> callback) {
        String url = LIVEOAK_URL + "/" + APPLICATION_NAME + uri;
        new PostTask(callback).execute(url, jsonObject);
    }

    public void deleteResource(String resourceURL, Callback<JSONObject> callback) {
        String url = resourceURL;
        // handle relativeURLs if applicable
        if (resourceURL.startsWith("/")) {
            url = LIVEOAK_URL + "/" + APPLICATION_NAME + resourceURL;
        }
        new DeleteTask(callback).execute(url);
    }

    public void updateResource(String resourceURL, JSONObject jsonObject, Callback<JSONObject> callback) {
        String url = resourceURL;
        // handle relativeURLs if applicable
        if (resourceURL.startsWith("/")) {
            url = LIVEOAK_URL + "/" + APPLICATION_NAME + resourceURL;
        }
        new PutTask(callback).execute(url, jsonObject);
    }

    public void subscribe(String resourcePath, JSONObject message, Callback<JSONObject> callback) {
        if (resourcePath.startsWith("/")) {
            resourcePath = "/" + APPLICATION_NAME + resourcePath;
        }
        liveOakPush.subscribe(resourcePath, message, callback);
    }
}
