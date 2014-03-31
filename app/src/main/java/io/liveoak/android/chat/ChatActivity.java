package io.liveoak.android.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.json.JSONObject;

import io.liveoak.helper.LiveOak;

public class ChatActivity extends Activity implements MessageHandler {

    ChatsFragment chatsFragment;
    SubmitFragment submitFragment;
    public String username;

    static final int LOGIN_REQUEST_CODE=1201;

    static final String USERNAME = "username";

    private final String logTag = ChatActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //username = savedInstanceState.getString(USERNAME, null);
        if (username != null) {
            String username = getSharedPreferences(ChatApplication.LIVEOAK_PREFERENCE_KEY, Context.MODE_PRIVATE).getString(ChatApplication.USERNAME_KEY, null);
        }

        setContentView(R.layout.chat_activity);
        this.chatsFragment = (ChatsFragment) this.getFragmentManager().findFragmentById(R.id.chats_fragment);

        this.submitFragment = (SubmitFragment) this.getFragmentManager().findFragmentById(R.id.submit_fragment);
        registerReceiver(submitFragment.createNetworkChangeReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        // access the registration object
        Log.e(logTag, getApplication().toString());
        PushRegistrar push = ((ChatApplication) getApplication())
                .getRegistration();

        // fire up registration..

        // The method will attempt to register the device with GCM and the UnifiedPush server
        push.register(getApplicationContext(), new Callback<Void>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSuccess(Void ignore) {
                Toast.makeText(ChatActivity.this, "Registered With UPS",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("Error: Could not register with UPS")
                        .setMessage("An error occured while trying to register with UPS. Please make " +
                                "sure the UPS server is up and running and that the device can access it." +
                                "\n\nError Received:\n\n" +
                                exception.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
                alertDialog.show();
                Log.e(logTag, exception.getMessage(), exception);
            }
        });

        LiveOak.PushSubscription subscription = ((ChatApplication) getApplication()).getSubscription();
        subscription.subscribe(new Callback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                Toast.makeText(ChatActivity.this, "Subscribed with LiveOak",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("Error: Could not subscribe to LiveOak")
                        .setMessage("An error occured while trying to subscribe to LiveOak. Please make " +
                                "sure the LiveOak server is up and running and that the device can access it." +
                                "\n\nError Received:\n\n" +
                                exception.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
                alertDialog.show();
                Log.e(logTag, exception.getMessage(), exception);
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
            Toast.makeText(this, "LOGOUT", Toast.LENGTH_SHORT).show();

            // clear the name parameter
            getSharedPreferences(ChatApplication.LIVEOAK_PREFERENCE_KEY, Context.MODE_PRIVATE).edit().remove(ChatApplication.USERNAME_KEY).commit();

            // unregister with liveoak
            ChatApplication chatApplication = (ChatApplication) this.getApplication();
            LiveOak.PushSubscription subscription = (chatApplication).getSubscription();


            subscription.unsubscribe(new Callback<JSONObject>() {

                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Toast.makeText(ChatActivity.this, "Unsubscribed from LiveOak",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(ChatActivity.this, "Error trying to unsubscribe from LiveOak", Toast.LENGTH_SHORT).show();
                    Log.e(logTag, exception.getMessage(), exception);
                }
            });


            PushRegistrar push = ((ChatApplication) getApplication()).getRegistration();
            push.unregister(this, new Callback<Void>() {
                private static final long serialVersionUID = 1L;

                @Override
                public void onSuccess(Void ignore) {
                    Toast.makeText(ChatActivity.this, "Unregistered With UPS",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(ChatActivity.this, "Unregistration with UPS Failed", Toast.LENGTH_SHORT).show();
                    Log.e(logTag, exception.getMessage(), exception);
                }
            });

            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Registrations.registerMainThreadHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Registrations.unregisterMainThreadHandler(this);
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {

    }

    @Override
    public void onMessage(Context context, Bundle message) {

        LiveOak liveOak = ((ChatApplication) this.getApplication()).getLiveOak();
        String resourceURI = message.getString("io.liveoak.push.url");
        String event = message.getString("io.liveoak.push.event");


        if (event.equals("created")) {
            // the resourceURI returns from the server with the application name already prepended to it
            // since getResource will also prepend the application name, remove it now so its not added twice
            String resourceURISansApplication = resourceURI.substring(("/" + liveOak.APPLICATION_NAME).length());
            liveOak.getResource(resourceURISansApplication, new Callback<JSONObject>() {

                @Override
                public void onSuccess(JSONObject resource) {
                    Chat chat = new Chat(resource.optString("name"), resource.optString("text"));
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
}
