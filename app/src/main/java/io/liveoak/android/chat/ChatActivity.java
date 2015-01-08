package io.liveoak.android.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.json.JSONObject;

import io.liveoak.helper.LiveOak;

public class ChatActivity extends Activity implements MessageHandler {

    ChatsFragment chatsFragment;
    SubmitFragment submitFragment;
    public String username = null;

    static final int LOGIN_REQUEST_CODE=1201;

    static final String BUNDLE_KEY_USERNAME = "username";

    SubmitFragment.NetworkChangeListener networkChangeListener;

    private final String logTag = ChatActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkGooglePlayServices();

        //try and name from the savedInstanceState is available
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY_USERNAME)) {
            username = savedInstanceState.getString(BUNDLE_KEY_USERNAME, null);
        }
        // otherwise check if the shared preferences has it already
        if (username == null) {
            username = getSharedPreferences(ChatApplication.APP_PREFERENCE_FILENAME, Context.MODE_PRIVATE).getString(ChatApplication.USERNAME_KEY, null);
        }
        // if not in shared preferences or savedInstanceState, then the user hasn't logged before, do that now
        if (username == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
        } else {
            connectToLiveOak();
        }

        setContentView(R.layout.chat_activity);
        this.chatsFragment = (ChatsFragment) this.getFragmentManager().findFragmentById(R.id.chats_fragment);

        this.submitFragment = (SubmitFragment) this.getFragmentManager().findFragmentById(R.id.submit_fragment);
        networkChangeListener = submitFragment.createNetworkChangeReceiver();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String username = data.getStringExtra(LoginActivity.EXTRAS_USERNAME);
                getSharedPreferences(ChatApplication.APP_PREFERENCE_FILENAME, Context.MODE_PRIVATE).edit().putString(ChatApplication.USERNAME_KEY, username).commit();
                chatsFragment.setUsername(username);
                submitFragment.setUsername(username);
                connectToLiveOak();
            } else if (resultCode == LoginActivity.BACKPRESSED_RESULT_CODE) {
                finish();
            } else {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
            }
        }
    }

    public void connectToLiveOak() {
        final LiveOak liveOak = ((ChatApplication) getApplication()).getLiveOak();
        liveOak.connect(new Callback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                liveOak.subscribe("/storage/chat/*", new JSONObject(), new Callback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        //TODO
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                } );
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplication(), "Error connecting to LiveOak " + e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY_USERNAME, username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.application, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_settings) {

            //logout and disconnect from the LiveOak system.
            //This will logout the current user and unsubscribe them to resources
            LiveOak liveOak = ((ChatApplication) this.getApplication()).getLiveOak();
            liveOak.disconnect();

            // clear the name parameter
            getSharedPreferences(ChatApplication.APP_PREFERENCE_FILENAME, Context.MODE_PRIVATE).edit().remove(ChatApplication.USERNAME_KEY).commit();

            finish();
            startActivity(getIntent());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Registrations.registerMainThreadHandler(this);
        registerReceiver(networkChangeListener, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        //remove the notification if still visible
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationHandler.NOTIFICATION_ID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Registrations.unregisterMainThreadHandler(this);
        unregisterReceiver(networkChangeListener);
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {

    }

    @Override
    public void onMessage(Context context, Bundle message) {

        LiveOak liveOak = ((ChatApplication) this.getApplication()).getLiveOak();
        String resourceURI = message.getString("io.liveoak.push.url");
        String event = message.getString("io.liveoak.push.event");


        if (event.equalsIgnoreCase("created")) {
            // the resourceURI returns from the server with the application name already prepended to it
            // since getResource will also prepend the application name, remove it now so its not added twice
            String resourceURISansApplication = resourceURI.substring(("/" + liveOak.APPLICATION_NAME).length());
            liveOak.getResource(resourceURISansApplication, new Callback<JSONObject>() {

                @Override
                public void onSuccess(JSONObject resource) {
                    Chat chat = new Chat(resource.optString("id"), resource.optString("name"), resource.optString("text"));
                    chatsFragment.addChat(chat);
                }

                @Override
                public void onFailure(Exception exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
    }

    @Override
    public void onError() {

    }

    public void checkGooglePlayServices() {
        // check if google play services is installed
        int googlePlayServicesStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (googlePlayServicesStatus != ConnectionResult.SUCCESS) {
            String title = "Error With Google Play Services";
            String message = "Some aspects of the application may not work properly";

            if (googlePlayServicesStatus == ConnectionResult.SERVICE_MISSING) {
                title = "Missing Google Play Services";
                message = "You will not be able to receive push notifications.";
            } else if (googlePlayServicesStatus == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                title = "Google Play Services Update Required";
                message = "You need to update your version of google play services. Some aspects of the application may not work properly.";
            }


            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            alertDialog.show();
        }
    }
}
