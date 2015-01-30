package io.liveoak.example.android.chat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwringe on 20/01/15.
 */
public class ChatAdapter extends BaseAdapter {

    private LinkedHashMap<String, Chat> chats = new LinkedHashMap<>();
    private List<String> chatIdList = new ArrayList<>();

    private static final String NULL_USER = "Unknown";
    private static final String NULL_USER_CHAR = "?";

    private Map<String, Integer> userIconColors = new HashMap<>();


    private ChatActivity context;
    private LayoutInflater inflater;

    public ChatAdapter(ChatActivity activity) {
        this.context = activity;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int position) {
        return chats.get(chatIdList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chat = chats.get(chatIdList.get(position));

        String name = chat.getName();
        if (name == null || name.isEmpty()) {
            name = NULL_USER;
        }

        View view = inflater.inflate(R.layout.chat_fragment, null);

        TextView nameTextView = (TextView) view.findViewById(R.id.chat_fragment_name);
        TextView message = (TextView) view.findViewById(R.id.chat_fragment_message);
        TextView iconText = (TextView) view.findViewById(R.id.chat_fragment_icon);

        nameTextView.setText(name);
        message.setText(chat.getText());
        if (!name.equals(NULL_USER)) {
            iconText.setText(name.substring(0, 1));
        } else {
            iconText.setText(NULL_USER_CHAR);
        }

        TypedArray colours = context.getResources().obtainTypedArray(R.array.icon_colors);
        Integer color = userIconColors.get(name);
        if (color == null) {
            color = Math.abs(name.hashCode()) % colours.length();
            userIconColors.put(name, color);
        }

        ((GradientDrawable) iconText.getBackground()).setColor(colours.getColor(color, 0));

        return view;
    }

    public void overwriteChats(List<Chat> newChats) {
        chats.clear();
        chatIdList.clear();
        for (Chat newChat : newChats) {
            chats.put(newChat.getId(), newChat);
            chatIdList.add(newChat.getId());
        }
    }

    public void addChat(Chat newChat) {
        if (!chats.containsKey(newChat.getId())) {
            chats.put(newChat.getId(), newChat);
            chatIdList.add(newChat.getId());
        }
    }

    public void removeChat(String chatId) {
        if (chats.containsKey(chatId)) {
            chats.remove(chatId);
            chatIdList.remove(chatId);
        }
    }
}

