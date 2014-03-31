package io.liveoak.android.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.json.JSONObject;

import io.liveoak.helper.LiveOak;

public class ApplicationActivity extends Activity {

    static final int LOGIN_REQUEST_CODE=1201;
    static final int CHAT_REQUEST_CODE=1202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String username = getSharedPreferences(ChatApplication.LIVEOAK_PREFERENCE_KEY, Context.MODE_PRIVATE).getString(ChatApplication.USERNAME_KEY, null);

        if (username == null) {
            startLoginActivity();
        } else {
            startChatActivity();
        }

    }

    protected void startChatActivity() {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(chatIntent, CHAT_REQUEST_CODE);
    }

    protected void startLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED) {
            // if cancelled, eg the back button is pressed, then exist the program
            finish();
        }

        // get the username back from the LoginActivity
        if (requestCode == this.LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String username = data.getStringExtra(LoginActivity.EXTRAS_USERNAME);
                getSharedPreferences(ChatApplication.LIVEOAK_PREFERENCE_KEY, Context.MODE_PRIVATE).edit().putString(ChatApplication.USERNAME_KEY, username).commit();
                startChatActivity();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // if cancelled, then exist the program
                finish();
            }
        } else if (requestCode == this.CHAT_REQUEST_CODE) {
            // if the chat application exists, then reopen the login screen
            if (resultCode == Activity.RESULT_OK) {
                startLoginActivity();
            }
        }
    }
}
