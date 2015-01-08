package io.liveoak.android.chat;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jboss.aerogear.android.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends ListFragment {

    ChatAdapter adapter;
    String username;
    ArrayList<Chat> chats = null;
    Handler retrieveChatsHandler;
    View noChatsView;

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

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("chats")) {
                chats = savedInstanceState.getParcelableArrayList("chats");
            }

            if (savedInstanceState.containsKey("username")) {
                username = savedInstanceState.getString("username");
                setUsername(username);
            }
        }

        if (username == null) {
            username = getActivity().getSharedPreferences(ChatApplication.APP_PREFERENCE_FILENAME, Context.MODE_PRIVATE).getString(ChatApplication.USERNAME_KEY, null);
            if (username != null) {
                setUsername(username);
            }
        }

        this.noChatsView = chatsView.findViewById(R.id.no_messages);

        return chatsView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ChatApplication application = ((ChatApplication) getActivity().getApplication());
        List<Chat> pendingChats = application.getPendingChats();
        if (pendingChats != null && !pendingChats.isEmpty()) {
            for (Chat pChat : pendingChats) {
                addChat(pChat);
            }
        }
        pendingChats.clear();
    }


    public void setUsername(String username) {
        this.username = username;

        if (adapter == null) {
            adapter = new ChatAdapter(this.getActivity(), R.layout.chat_fragment, R.layout.chat_fragment_self, username, new ArrayList<Chat>());
        }

        this.setListAdapter(adapter);

        if (chats != null) {
            adapter.setChats(chats);
        } else {
            getChats();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            outState.putParcelableArrayList("chats", adapter.getParcelableList());
        }
        outState.putString("username", username);
    }

    public void addChat(Chat chat) {
        this.adapter.addChat(chat);
    }

    protected void getChats() {
        retrieveChatsRunner.run();
    }

    private Runnable retrieveChatsRunner = new Runnable() {

        @Override
        public void run() {
            ChatApplication application = ((ChatApplication) getActivity().getApplication());
            application.getLiveOak().getResource("/storage/chat?fields=*(*)&limit=100", new Callback<JSONObject>() {

                        @Override
                        public void onSuccess(JSONObject resource) {
                            JSONArray chatArray = (JSONArray) resource.opt("members");

                            if (chatArray != null) {
                                List<Chat> chats = new ArrayList<Chat>();
                                for (int i = 0; i < chatArray.length(); i++) {
                                    JSONObject chat = (JSONObject) chatArray.opt(i);
                                    chats.add(new Chat(chat.optString("id"), chat.optString("name"), chat.optString("text")));
                                }
                                adapter.setChats(chats);
                            }

                            if (adapter.chats != null && adapter.chats.size() == 0) {
                                // hide the current default spinner
                                getListView().getEmptyView().setVisibility(View.GONE);
                                // show the no message dialog and set as empty view
                                //noChatsView.setVisibility(View.VISIBLE);
                                getListView().setEmptyView(noChatsView);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {

                            if (retrieveChatsHandler == null) {
                                retrieveChatsHandler = new Handler();
                            }
                            // retry every 2 seconds after an error
                            retrieveChatsHandler.postDelayed(retrieveChatsRunner, 2000);
                        }
                    }
            );
        }
    };

}
