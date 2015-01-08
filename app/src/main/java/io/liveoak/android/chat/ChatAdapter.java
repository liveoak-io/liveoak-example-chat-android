package io.liveoak.android.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by mwringe on 03/03/14.
 */
public class ChatAdapter extends BaseAdapter {

    private final String logTag = ChatAdapter.class.getSimpleName();

    List<Chat> chats;
    String user;
    Context context;
    int resource;
    int selfResource;
    LayoutInflater inflater;

    Set<String> pending = new HashSet<String>();

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
        return position;
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

        //position starts at 0, size starts counting at 1
        if (pending.contains(chat.getId())) {
            pending.remove(chat.getId());
            if (sender.equals(user)) {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.load_from_right));
            } else {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.load_from_left));
            }
        }

        return view;
    }

    public void addChat(Chat newChat) {
            //hack to prevent showing two chats simulatanously
            for (Chat chat : chats) {
                if (chat.getId().equals(newChat.getId())) {
                    Log.e(logTag, "Error: received a duplicate chat. Not displaying.");
                    return;
                }
            }

            chats.add(newChat);
            this.pending.add(newChat.getId());
            this.notifyDataSetChanged();
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
        this.notifyDataSetChanged();
    }

    public ArrayList<Chat> getParcelableList() {
        ArrayList<Chat> parcelableChats = new ArrayList<Chat>();
        for (Chat chat: chats) {
            parcelableChats.add(chat);
        }
        return parcelableChats;
    }
}
