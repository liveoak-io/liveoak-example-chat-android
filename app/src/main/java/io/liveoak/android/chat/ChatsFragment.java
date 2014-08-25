package io.liveoak.android.chat;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jboss.aerogear.android.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatsFragment extends ListFragment {

    ChatAdapter adapter;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chatsView = inflater.inflate(R.layout.chats_fragment, container, false);

        String name = getActivity().getSharedPreferences(ChatApplication.LIVEOAK_PREFERENCE_KEY, Context.MODE_PRIVATE).getString(ChatApplication.USERNAME_KEY, "Android");
        ChatAdapter adapter = new ChatAdapter(this.getActivity(), R.layout.chat_fragment, R.layout.chat_fragment_self, name, new ArrayList<Chat>());
        this.setListAdapter(adapter);
        this.adapter = adapter;
        getChats();
        return chatsView;
    }

    public void addChat(Chat chat) {
        this.adapter.addChat(chat);
    }

    protected void getChats() {
        ChatApplication application = ((ChatApplication) this.getActivity().getApplication());
        application.getLiveOak().getResource("/storage/chat?fields=*(*)&limit=100", new Callback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject resource) {
                JSONArray chatArray = (JSONArray) resource.opt("members");

                if (chatArray != null) {

                    for (int i = 0; i < chatArray.length(); i++) {
                        JSONObject chat = (JSONObject) chatArray.opt(i);
                        adapter.addChat(new Chat(chat.optString("name"), chat.optString("text")));
                    }

                }
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getActivity(), "Error trying to read chat.", Toast.LENGTH_SHORT).show();
                Log.e(ChatsFragment.class.getSimpleName(), "Error trying to read chat", exception);
            }
        });
    }
}
