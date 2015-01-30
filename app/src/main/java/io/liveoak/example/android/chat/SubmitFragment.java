package io.liveoak.example.android.chat;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.Pipe;
import org.jboss.aerogear.android.pipe.PipeManager;


public class SubmitFragment extends Fragment implements View.OnClickListener {

    private EditText messageEditText;
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.handler = new Handler();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.submit_fragment, container, false);

        messageEditText = (EditText) view.findViewById(R.id.submit_fragment_message);

        Button submitButton = (Button) view.findViewById(R.id.submit_fragment_submit_button);
        submitButton.setOnClickListener(this);


        return view;
    }

    public void onClick(View v) {
        String username = ((ChatActivity) this.getActivity()).getUsername();

        Pipe<Chat> pipe = PipeManager.getPipe("chats");
        Chat chat = new Chat();
        chat.setName(username);
        chat.setText(messageEditText.getText().toString());

        pipe.save(chat, new Callback<Chat>() {
            @Override
            public void onSuccess(Chat chat) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        messageEditText.setText(null);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });

    }
}
