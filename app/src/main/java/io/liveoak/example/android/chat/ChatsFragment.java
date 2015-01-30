package io.liveoak.example.android.chat;


import android.app.ListFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.pipe.Pipe;
import org.jboss.aerogear.android.pipe.PipeManager;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class ChatsFragment extends ListFragment {

    View emptyChats;
    ChatAdapter chatAdapter;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chats_fragment, container, false);

        this.emptyChats = view.findViewById(R.id.chats_fragment_no_messages);

        TextView emptyIcon = (TextView) view.findViewById(R.id.chats_fragment_no_messages_icon);
        Typeface typeface = Typeface.createFromAsset(this.getActivity().getAssets(), "fontawesome-webfont.ttf");
        emptyIcon.setTypeface(typeface);


        chatAdapter = new ChatAdapter((ChatActivity) this.getActivity());
        this.setListAdapter(chatAdapter);

        getChats(chatAdapter);

        return view;
    }


    private void getChats(final ChatAdapter chatAdapter) {

        Pipe<Chats> chatspipe = PipeManager.getPipe("liveoak.chats", this.getActivity());
        chatspipe.read(new org.jboss.aerogear.android.core.Callback<List<Chats>>() {
            @Override
            public void onSuccess(List<Chats> chatses) {
                int count = (chatses.get(0).getCount());
                int offset = count - 100;
                if (offset < 0) {
                    offset = 0;
                }

                Pipe<Chat> pipe = PipeManager.getPipe("chats", getActivity());

                ReadFilter readFilter = new ReadFilter();
                readFilter.setOffset(offset);
                readFilter.setLimit(100);

                pipe.read(readFilter, new org.jboss.aerogear.android.core.Callback<List<Chat>>() {
                            @Override
                            public void onSuccess(List<Chat> receivedChats) {
                                List<Chat> chats = new ArrayList(receivedChats);
                                //due to a strange issue with AeroGear pipes,
                                //if the chat collection is empty, it tries to convert
                                //the parent resource into a 'chat' object
                                if (chats.size() == 1) {
                                    Chat chat = chats.get(0);
                                    if (chat.getId().equals("chat") && chat.getName() == null && chat.getText() == null) {
                                        // we somehow got back the parent, clear the chat list
                                        chats.clear();
                                    }
                                }


                                if (chats.size() > 0) {
                                    chatAdapter.overwriteChats(chats);
                                    chatAdapter.notifyDataSetChanged();
                                } else {
                                    getListView().getEmptyView().setVisibility(View.GONE);
                                    getListView().setEmptyView(emptyChats);
                                    chatAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getActivity(), "Error trying to read the chats. Please see the logs", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                );

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public void addChat(Chat chat) {
        chatAdapter.addChat(chat);
        chatAdapter.notifyDataSetChanged();
    }


}
