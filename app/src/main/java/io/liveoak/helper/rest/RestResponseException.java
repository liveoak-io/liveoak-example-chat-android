package io.liveoak.helper.rest;

import org.json.JSONObject;

/**
 * Created by mwringe on 01/05/14.
 */
public class RestResponseException extends Exception {

    String path;
    JSONObject jsonObject;
    int responseCode;

    public RestResponseException(String path, int responseCode, JSONObject jsonObject) {
        this.path = path;
        this.responseCode = responseCode;
        this.jsonObject = jsonObject;
    }

    public RestResponseException(String path, int responseCode) {
        this(path, responseCode, null);
    }

    @Override
    public String toString() {
        if (jsonObject == null) {
            return this.getClass().getName() + ": { path: " + path + ", response-code: " + responseCode + "}";
        } else {
             return this.getClass().getName() + ": { path: " + path + ", response-code: " + responseCode + ", error-message: " + jsonObject.toString() + "}";
        }
    }
}
