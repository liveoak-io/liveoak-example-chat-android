package io.liveoak.android.chat;

import org.json.JSONObject;

/**
 * Created by mwringe on 03/03/14.
 */
public class Chat {
    String sender;
    String text;

    public Chat(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getSender() {
        return this.sender;
    }

    public String getText() {
        return this.text;
    }

    public JSONObject toJSONObject() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", this.sender);
            jsonObject.put("text", this.text);

            return jsonObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
