package io.liveoak.example.android.chat;


import org.jboss.aerogear.android.core.RecordId;

/**
 * Represents a chat object stored on the server.
 *
 * Created by mwringe on 20/01/15.
 */
public class Chat {

    @RecordId
    private String id;

    private String name;

    private String text;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
