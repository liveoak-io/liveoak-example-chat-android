package io.liveoak.example.android.chat;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.pipe.MarshallingConfig;
import org.jboss.aerogear.android.pipe.Pipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.rest.gson.GsonResponseParser;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import io.liveoak.helper.LiveOak;


public class ChatActivity extends ActionBarActivity implements UserDialog.Listener, MessageHandler {

    private final String logTag = ChatActivity.class.getSimpleName();

    // value to store the username
    private String username = null;
    // key used to store the username
    private static final String USERNAME_KEY = "username";

    // request code from the Username Selector Intent
    private static final int USERNAME_REQUEST_CODE = 1201;

    ChatsFragment chatsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        setupAeroGearPipes();

        // Check if the username is already set, otherwise open the username activity
        checkUsername(savedInstanceState);

        setContentView(R.layout.chat_activity);
        this.chatsFragment = (ChatsFragment) this.getFragmentManager().findFragmentById(R.id.chats_fragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);

        MenuItem userMenuItem = (MenuItem) menu.findItem(R.id.user_settings);
        userMenuItem.setTitle(username);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.user_settings) {
            DialogFragment userDialog = new UserDialog();
            userDialog.show(this.getFragmentManager(), "dialog");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check the result of the username activity
        if (requestCode == this.USERNAME_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                username = data.getStringExtra(UsernameActivity.EXTRAS_USERNAME_KEY);
                getPreferences(Context.MODE_PRIVATE).edit().putString(USERNAME_KEY, username).commit();
                connectToLiveOak();
            } else if (resultCode == UsernameActivity.BACKPRESSED_RESULT_CODE) {
                finish();
            } else {
                Intent loginIntent = new Intent(this, UsernameActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(loginIntent, USERNAME_REQUEST_CODE);
            }
        }
    }

    /**
     * Will check if the user is loged in or not. Otherwise will open
     * a new intent to gather the username.
     *
     * @param savedInstanceState
     */
    private void checkUsername(Bundle savedInstanceState) {
        if (username == null) {
            // check the saved instance state first
            // then check the shared preferences from the Application
            if (savedInstanceState != null && savedInstanceState.containsKey(USERNAME_KEY)) {
                username = savedInstanceState.getString(USERNAME_KEY);
            } else {
                // if its not in the saved instance state, then check the shared preferences
                username = getPreferences(Context.MODE_PRIVATE).getString(USERNAME_KEY, null);
            }

            // if the username is still null, it wasn't in the instancestate or in the preferences
            // open up a new intent to request it
            if (username == null) {
                Intent selectUsernameIntent = new Intent(this, UsernameActivity.class);
                selectUsernameIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(selectUsernameIntent, USERNAME_REQUEST_CODE);
            }
        }
    }

    public String getUsername() {
        return this.username;
    }

    public void connectToLiveOak() {
        final LiveOak liveOak = ((ChatApplication) getApplication()).getLiveOak();
        liveOak.connect(new Callback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                liveOak.subscribe("/storage/chat/*", new JSONObject(), new Callback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        //
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplication(), "An Error occurred while trying to subscribe to the chat collection. Please see the logs", Toast.LENGTH_SHORT).show();
                        Log.e(logTag, "An error occurred trying to subscribe to the chat collection", e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplication(), "An Error occurred while connecting to LiveOak. Please see the logs", Toast.LENGTH_SHORT).show();
                Log.e(logTag, "An error occurred while trying to connect to the LiveOak server", e);
            }
        });
    }

    @Override
    public void onLogout() {
        LiveOak liveOak = ((ChatApplication) this.getApplication()).getLiveOak();
        liveOak.disconnect();

        // clear the name parameter from preferences
        getPreferences(Context.MODE_PRIVATE).edit().remove(USERNAME_KEY).commit();
        username = null;

        // restart the application
        recreate();
    }

    @Override
    public void onMessage(Context context, Bundle message) {

        String resourceURI = message.getString("io.liveoak.push.url");
        String event = message.getString("io.liveoak.push.event");
        if (event.equalsIgnoreCase("created")) {

            Pipe pipe = PipeManager.getPipe("liveoak.chat", this);

            ReadFilter readFilter = new ReadFilter();
            try {
                readFilter.setLinkUri(new URI(URLEncoder.encode(resourceURI, "UTF-8")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            pipe.read(readFilter, new org.jboss.aerogear.android.core.Callback<List<Chat>>() {
                @Override
                public void onSuccess(final List<Chat> chats) {
                    for (Chat chat : chats) {
                        chatsFragment.addChat(chat);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {
        // Chat example app ignores delete messages for now
    }

    @Override
    public void onError() {
        Log.e(logTag, "An Error Occured while handling messages. Please see the error logs for more details");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // When not running, set up the notification handler for push messages
        Registrations.unregisterMainThreadHandler(this);
    }

    @Override
    protected void onResume() {
        super.onPause();
        // When in focus, make this the main handler for push messages
        Registrations.registerMainThreadHandler(this);
        //remove the notification if still visible
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationHandler.NOTIFICATION_ID);
    }


    private void setupAeroGearPipes() {
        try {
            MarshallingConfig marshallingConfig = new MarshallingConfig();
            marshallingConfig.setDataRoot("members");

            GsonResponseParser responseParser = new GsonResponseParser();
            responseParser.setMarshallingConfig(marshallingConfig);

            LiveOak liveOak = ((ChatApplication) this.getApplication()).getLiveOak();

            PipeManager.config("chats", RestfulPipeConfiguration.class)
                    .withUrl(new URL(liveOak.LIVEOAK_URL + "/" + liveOak.APPLICATION_NAME + "/storage/chat?fields=*(*)"))
                    .responseParser(responseParser)
                    .forClass(Chat.class);

            PipeManager.config("liveoak.chat", RestfulPipeConfiguration.class)
                    .withUrl(new URL(liveOak.LIVEOAK_URL))
                    .forClass(Chat.class);

            PipeManager.config("liveoak.chats", RestfulPipeConfiguration.class)
                    .withUrl(new URL(liveOak.LIVEOAK_URL + "/" + liveOak.APPLICATION_NAME + "/storage/chat"))
                    .forClass(Chats.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
