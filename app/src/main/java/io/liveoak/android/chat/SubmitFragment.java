package io.liveoak.android.chat;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jboss.aerogear.android.Callback;
import org.json.JSONObject;

import io.liveoak.helper.LiveOak;


public class SubmitFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {

    EditText messageEditText;
    String name = null;
    TextView nameTextView;

    private final String logTag = SubmitFragment.class.getSimpleName();

    public SubmitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            name = savedInstanceState.getString("name", null);
        }

        if (name == null) {
            name = getActivity().getPreferences(Context.MODE_PRIVATE).getString("name", getString(R.string.defaultUser));
        }

        View view = inflater.inflate(R.layout.submit_fragment, container, false);

        TextView nameTextView = (TextView) view.findViewById(R.id.user_textview);
        nameTextView.setText(name);

        Button sendButton = (Button) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        messageEditText = (EditText) view.findViewById(R.id.edit_message);
        messageEditText.setOnEditorActionListener(this);

        this.nameTextView = (TextView) view.findViewById(R.id.user_textview);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", name);
    }

    @Override
    public void onClick(View view) {
        sendChat();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        sendChat();
        return true;
    }

    public void sendChat() {
        Chat chat = new Chat(nameTextView.getText().toString(), messageEditText.getText().toString());

        LiveOak liveOak = ((ChatApplication) this.getActivity().getApplication()).getLiveOak();

        liveOak.createResource("/chat/storage/chat", chat.toJSONObject(), new Callback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                messageEditText.setText(null);
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getActivity().getApplicationContext(), "Error trying to send chat to liveoak.", Toast.LENGTH_SHORT).show();
                Log.e(logTag, exception.getMessage(), exception);
            }
        });
    }
}
