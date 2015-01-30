package io.liveoak.example.android.chat;

/**
 * Represents the chat collection stored on the server.
 *
 * For our purposes here we only care about the count so that
 * we can calculate how to return the last 100 entries back
 * to the client.
 *
 * Created by mwringe on 30/01/15.
 */
public class Chats {
    private int count;

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
