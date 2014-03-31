package io.liveoak.android.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginActivity extends Activity implements OnClickListener, TextWatcher {

    // UI references.
    EditText usernameEditText;
    Button enterButton;
    TextView requiredTextView;

    //
    static final String EXTRAS_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_login);

        // get a reference to the UI elements and set up the listeners
        usernameEditText = (EditText) findViewById(R.id.login_username_edittext);
        usernameEditText.addTextChangedListener(this);

        enterButton = (Button) findViewById(R.id.login_enter_button);
        enterButton.setOnClickListener(this);

        requiredTextView = (TextView) findViewById(R.id.login_username_required_text);
    }

    // handle when the submit button is clicked. This should return the username to the
    // activity which called this one
    @Override
    public void onClick(View v) {
        String username = usernameEditText.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRAS_USERNAME, username);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //do nothing for now
    }

    // enable or disable the submit button based on if required fields contian content or not
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //check if we have any content in the edit field or not, if so enable the login button
        if (s.length() > 0) {
            enterButton.setEnabled(true);
            requiredTextView.setVisibility(View.INVISIBLE);
        } else {
            enterButton.setEnabled(false);
            requiredTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // do nothing
    }
}



