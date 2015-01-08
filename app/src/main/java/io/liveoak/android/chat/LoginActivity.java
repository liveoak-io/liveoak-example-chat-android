package io.liveoak.android.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Activity to handle getting a username for a user
 *
 * Note: since this example does not actually authenticate users, the 'login' is just the
 *       user selecting a name.
 *
 * When a LiveOak SDK is available, a developer would be expected to use an Activity from the SDK instead.
 */
public class LoginActivity extends Activity implements OnClickListener, TextWatcher, TextView.OnEditorActionListener {

    // UI references.
    EditText usernameEditText;
    Button enterButton;
    TextView requiredTextView;

    // return intent key for the username
    static final String EXTRAS_USERNAME = "username";

    // result code to be returned by the activity if the back button was pressed.
    static final int BACKPRESSED_RESULT_CODE = 1201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_login);

        // get a reference to the UI elements and set up the listeners
        usernameEditText = (EditText) findViewById(R.id.login_username_edittext);
        usernameEditText.addTextChangedListener(this);
        usernameEditText.setOnEditorActionListener(this);

        enterButton = (Button) findViewById(R.id.login_enter_button);
        enterButton.setOnClickListener(this);

        requiredTextView = (TextView) findViewById(R.id.login_username_required_text);
    }

    // handle when the submit button is clicked. This should return the username to the
    // activity which called this one
    @Override
    public void onClick(View v) {
        handleSubmit();
    }

    /**
     * If the back button is pressed, then return back to the activity
     */
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(this.BACKPRESSED_RESULT_CODE);
        finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //
        if (usernameEditText.getText() != null && usernameEditText.getText().length() > 0 ) {
            handleSubmit();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Handle the submit action.
     * This will return the username back to the activity which called it
     */
    protected void handleSubmit() {
        String username = usernameEditText.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRAS_USERNAME, username);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // this type of event is currently not used
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
        // this type of event is currently not used
    }
}



