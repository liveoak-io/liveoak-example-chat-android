package io.liveoak.android.chat;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by mwringe on 03/03/14.
 */
public class Chat implements Parcelable {
    String sender;
    String text;
    String id;

    public Chat(String id, String sender, String text) {
        this.id = id;
        this.sender = sender;
        this.text = text;
    }

    public String getSender() {
        return this.sender;
    }

    public String getText() {
        return this.text;
    }

    public String getId() {
        return this.id;
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

    public static final Parcelable.Creator<Chat> CREATOR
            = new Parcelable.Creator<Chat>() {
        public Chat createFromParcel(Parcel in) {
            return new Chat(in.readString(), in.readString(), in.readString());
        }

        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(sender);
        dest.writeString(text);
    }
}
