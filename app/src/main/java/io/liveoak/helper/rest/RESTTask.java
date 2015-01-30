package io.liveoak.helper.rest;

import android.os.AsyncTask;

import com.google.common.net.HttpHeaders;

import org.jboss.aerogear.android.Callback;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mwringe on 01/05/14.
 */
public abstract class RESTTask extends AsyncTask<Object, Integer, Object> {

    Callback callback;

    enum Method {GET, POST, DELETE, PUT}

    public RESTTask(Callback<JSONObject> callback) {
        super();
        this.callback = callback;
    }

    protected Object request(URL url, Method method) {
        return request(url, method, null);
    }

    protected Object request(URL url, Method method, JSONObject body) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
            connection.setRequestMethod(method.toString());

            if (method == Method.POST || method == Method.PUT) {
                connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
                if (body != null && body instanceof JSONObject) {
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(body.toString());
                    writer.flush();
                    writer.close();
                }
            }

            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
                return getJSON(connection.getInputStream());
            } else {
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    JSONObject errorResponse = getJSON(connection.getErrorStream());

                    return new RestResponseException(connection.getURL().toString(), connection.getResponseCode(), errorResponse);
                } else {
                    return new RestResponseException(connection.getURL().toString(), connection.getResponseCode());
                }
            }

        } catch (Exception e) {
            return e;
        }
    }

    protected JSONObject getJSON(InputStream inputStream) throws Exception {
        InputStreamReader reader = new InputStreamReader(inputStream);

        BufferedReader bReader = new BufferedReader(reader);

        String line = bReader.readLine();
        String content = new String();
        while (line != null) {
            content += line;
            line = bReader.readLine();
        }

        return new JSONObject(content);
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
