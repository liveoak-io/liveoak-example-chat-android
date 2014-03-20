package io.liveoak.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.net.HttpHeaders;

import org.jboss.aerogear.android.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mwringe on 28/02/14.
 */
public class LiveOak {

    private final String LIVEOAK_URL;
    private final String APPLICATION_NAME;

    private final String logTag = LiveOak.class.getSimpleName();

    /**
     * Creates an object used to interact with a liveoak instance.
     *
     * @param host            The host where the liveoak
     * @param port            The port of the liveoak instance
     * @param applicationName The application we are interested in
     */
    public LiveOak(String host, int port, String applicationName) {
        this.LIVEOAK_URL = "http://" + host + ":" + port;
        this.APPLICATION_NAME = applicationName;
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
            url = LIVEOAK_URL + resourceURL;
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
        String url = LIVEOAK_URL + uri;
        new PostTask(callback).execute(url, jsonObject);
    }

    private final class PostTask extends AsyncTask<Object, Integer, Object> {

        Callback callback;

        private PostTask(Callback<JSONObject> callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(Object... params) {
            String uri = (String) params[0];
            JSONObject body = (JSONObject) params[1];

            try {
                URL resourceURL = new URL(uri);

                HttpURLConnection connection = (HttpURLConnection) resourceURL.openConnection();
                connection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
                connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
                connection.setRequestMethod("POST");

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body.toString());
                writer.flush();
                writer.close();

                if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                    BufferedReader bReader = new BufferedReader(reader);

                    String line = bReader.readLine();
                    String content = new String();
                    while (line != null) {
                        content += line;
                        line = bReader.readLine();
                    }

                    return new JSONObject(content);
                }

            } catch (Exception e) {
                return e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof JSONObject) {
                this.callback.onSuccess((JSONObject) result);
            } else {
                this.callback.onFailure((Exception) result);
            }
        }
    }

    private final class GetTask extends AsyncTask<String, Integer, Object> {

        Callback callback;

        private GetTask(Callback<JSONObject> callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(String... params) {

            String resourceURLParam = params[0];

            try {
                URL resourceURL = new URL(resourceURLParam);

                HttpURLConnection connection = (HttpURLConnection) resourceURL.openConnection();

                connection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() <= 200 && connection.getResponseCode() < 300) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                    BufferedReader bReader = new BufferedReader(reader);

                    String line = bReader.readLine();
                    String content = new String();
                    while (line != null) {
                        content += line;
                        line = bReader.readLine();
                    }

                    return new JSONObject(content);
                } else {
                    throw new Exception("Error trying to get resource: " + connection.getResponseCode());
                }
            } catch (Exception e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof JSONObject) {
                this.callback.onSuccess((JSONObject) result);
            } else {
                this.callback.onFailure((Exception) result);
            }
        }
    }

    private final class PutTask extends AsyncTask<Object, Integer, Object> {

        Callback callback;

        private PutTask(Callback<JSONObject> callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(Object... params) {
            String uri = (String) params[0];
            JSONObject body = (JSONObject) params[1];

            try {
                URL resourceURL = new URL(uri);

                HttpURLConnection connection = (HttpURLConnection) resourceURL.openConnection();
                connection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
                connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
                connection.setRequestMethod("PUT");

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body.toString());
                writer.flush();
                writer.close();

                if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                    BufferedReader bReader = new BufferedReader(reader);

                    String line = bReader.readLine();
                    String content = new String();
                    while (line != null) {
                        content += line;
                        line = bReader.readLine();
                    }

                    return new JSONObject(content);
                } else {
                    return new Exception("Error while trying to create resource: " + connection.getResponseCode());
                }


            } catch (Exception e) {
               return e;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof JSONObject) {
                this.callback.onSuccess((JSONObject) result);
            } else {
                this.callback.onFailure((Exception) result);
            }
        }
    }


    private final class DeleteTask extends AsyncTask<String, Integer, Object> {

        Callback callback;

        private DeleteTask(Callback<JSONObject> callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(String... params) {
            String resourceURLParam = params[0];

            try {
                URL resourceURL = new URL(resourceURLParam);

                HttpURLConnection connection = (HttpURLConnection) resourceURL.openConnection();

                connection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
                connection.setRequestMethod("DELETE");

                if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                    BufferedReader bReader = new BufferedReader(reader);

                    String line = bReader.readLine();
                    String content = new String();
                    while (line != null) {
                        content += line;
                        line = bReader.readLine();
                    }

                    return new JSONObject(content);
                } else {
                    return new Exception("Error while trying to delete resource: " + connection.getResponseCode());
                }
            } catch (Exception e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof JSONObject) {
                this.callback.onSuccess((JSONObject) result);
            } else {
                this.callback.onFailure((Exception) result);
            }
        }
    }

    public PushSubscription createPushSubscription(String pushResourceName, String resourcePath, JSONObject message, String alias) {
        return new PushSubscription(pushResourceName, resourcePath, message, alias);
    }

    public class PushSubscription {

        private String resourcePath;
        private JSONObject message;
        private String alias;
        private String pushResourceName;


        private PushSubscription(String pushResourceName, String resourcePath, JSONObject message, String alias) {
            this.pushResourceName = pushResourceName;
            this.resourcePath = resourcePath;
            this.message = message;
            this.alias = alias;
        }

        public void subscribe(Callback<JSONObject> callback) {
            String subscribeURL = LIVEOAK_URL + "/" + APPLICATION_NAME + "/" + pushResourceName + "/subscriptions/" + alias;

            //String jsonString = "{ 'resourcePath': '" + resourcePath + "', 'message':" + message + ", 'alias':['" + alias + "']}";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("resourcePath", resourcePath);
                jsonObject.put("message", message);

                JSONArray aliasArray = new JSONArray();
                aliasArray.put(alias);

                jsonObject.put("alias", aliasArray);
            } catch (Exception e) {
                Log.e(logTag, "Error trying to create subscribe json object", e);
                throw new RuntimeException(e);
            }

            new PutTask(callback).execute(subscribeURL, jsonObject);
        }

        public void unsubscribe(Callback<JSONObject> callback) {
            String unsubscribeURL = LIVEOAK_URL + "/" + APPLICATION_NAME + "/" + pushResourceName + "/subscriptions";
            new DeleteTask(callback).execute(unsubscribeURL + "/" + alias);
        }
    }
}
