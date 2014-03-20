package io.liveoak.android.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mwringe on 03/03/14.
 */
public class ChatAdapter extends BaseAdapter {

    List<Chat> chats;
    String user;
    Context context;
    int resource;
    int selfResource;
    LayoutInflater inflater;

    public ChatAdapter(Context context, int resource, int selfResource, String user) {
        this(context, resource, selfResource, user, new ArrayList<Chat>());
    }

    public ChatAdapter(Context context, int resource, int selfResource, String user, List<Chat> chats) {
        this.user = user;
        this.context = context;
        this.resource = resource;
        this.selfResource = selfResource;
        this.chats = chats;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Chat chat = chats.get(position);
        String sender = chat.getSender();

        if (sender.equals(user)) {
            view = inflater.inflate(selfResource, null);
        } else {
            view = inflater.inflate(resource, null);
        }

        TextView senderTextView = (TextView) view.findViewById(R.id.chat_name);
        TextView messageTextView = (TextView) view.findViewById(R.id.chat_message);


        senderTextView.setText(chat.getSender());
        messageTextView.setText(chat.getText());

        return view;
    }

    public void addChat(Chat chat) {
        chats.add(chat);
        this.notifyDataSetChanged();
    }
}
