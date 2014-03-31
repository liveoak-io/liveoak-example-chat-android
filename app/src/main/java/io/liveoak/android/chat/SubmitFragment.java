package io.liveoak.android.chat;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    // UI references.
    EditText messageEditText;
    TextView nameTextView;
    TextView networkWarningTextView;
    Button sendButton;

    String name = null;

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
            name = getActivity().getSharedPreferences(ChatApplication.LIVEOAK_PREFERENCE_KEY, Context.MODE_PRIVATE).getString(ChatApplication.USERNAME_KEY, getString(R.string.defaultUser));
        }

        View view = inflater.inflate(R.layout.submit_fragment, container, false);

        TextView nameTextView = (TextView) view.findViewById(R.id.user_textview);
        nameTextView.setText(name);

        sendButton = (Button) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        messageEditText = (EditText) view.findViewById(R.id.edit_message);
        messageEditText.setOnEditorActionListener(this);

        this.nameTextView = (TextView) view.findViewById(R.id.user_textview);


        this.networkWarningTextView = (TextView) view.findViewById(R.id.submit_network_warning);
        checkNetworkState();

        return view;
    }

    public NetworkChangeListener createNetworkChangeReceiver() {
        return new NetworkChangeListener();
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

        liveOak.createResource("/storage/chat", chat.toJSONObject(), new Callback<JSONObject>() {

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

    // checks the network state.
    // If we don't have an active network, then display an error message and disable the submit button
    protected void checkNetworkState() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            networkWarningTextView.setVisibility(View.GONE);
            sendButton.setEnabled(true);
        } else {
            networkWarningTextView.setVisibility(View.VISIBLE);
            sendButton.setEnabled(false);
        }
    }

    // used to check if we have a network connection or not
    public class NetworkChangeListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            checkNetworkState();
        }
    }
}
