package io.liveoak.example.android.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UsernameActivity extends ActionBarActivity implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {

    //key used to store the username in extras
    static final String EXTRAS_USERNAME_KEY = "username";

    // result code if the user selected the backbutton
    static final int BACKPRESSED_RESULT_CODE = 1202;

    // UI references.
    EditText usernameEditText;
    Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.username_activity);

        // get a reference to the UI elements and set up the listeners
        usernameEditText = (EditText) findViewById(R.id.username_activity_username_edittext);
        usernameEditText.addTextChangedListener(this);
        usernameEditText.setOnEditorActionListener(this);

        enterButton = (Button) findViewById(R.id.username_activity_submit_button);
        enterButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        handleSubmit();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(this.BACKPRESSED_RESULT_CODE);
        finish();
    }

    private void handleSubmit() {
        String username = usernameEditText.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRAS_USERNAME_KEY, username);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (usernameEditText.getText() != null && usernameEditText.getText().length() > 0) {
            handleSubmit();
            return false;
        } else {
            Toast.makeText(this, getResources().getText(R.string.username_activity_username_required), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //check if we have any content in the edit field or not, if so enable the login button
        if (s.length() > 0) {
            enterButton.setEnabled(true);
        } else {
            enterButton.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
