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

public class ApplicationActivity extends Activity implements MessageHandler {

    ChatsFragment chatsFragment;
    public String name;

    private final String logTag = ApplicationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String name = getPreferences(Context.MODE_PRIVATE).getString("name", null);
        if (name == null) {
            final Dialog nameDialog = new Dialog(this);
            nameDialog.setContentView(R.layout.name_dialog);
            nameDialog.setTitle("Set Name");
            nameDialog.setCancelable(false);
            nameDialog.show();

            final Button button = (Button) nameDialog.findViewById(R.id.submit_name_button);
            final EditText nameField = (EditText) nameDialog.findViewById(R.id.edit_name);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (nameField.getText().toString() != null && !nameField.getText().toString().isEmpty()) {
                        String name = nameField.getText().toString();
                        getPreferences(Context.MODE_PRIVATE).edit().putString("name", name).commit();
                        nameDialog.dismiss();

                        Intent intent = new Intent(getApplicationContext(), ApplicationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }
            });

            nameDialog.show();
            setContentView(new View(this));
            return;

        }

        setContentView(R.layout.application_activity);


        this.chatsFragment = (ChatsFragment) this.getFragmentManager().findFragmentById(R.id.chats_fragment);
        Log.e(logTag, "CHATSFRAGMENT ONCREATE " + this.chatsFragment);

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
                Toast.makeText(ApplicationActivity.this, "Registered With UPS",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                AlertDialog alertDialog = new AlertDialog.Builder(ApplicationActivity.this)
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
                Toast.makeText(ApplicationActivity.this, "Subscribed with LiveOak",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                AlertDialog alertDialog = new AlertDialog.Builder(ApplicationActivity.this)
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
//        outState.putString("name", name);
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
            getPreferences(Context.MODE_PRIVATE).edit().remove("name").commit();

            // unregister with liveoak
            ChatApplication chatApplication = (ChatApplication) this.getApplication();
            LiveOak.PushSubscription subscription = (chatApplication).getSubscription();


            subscription.unsubscribe(new Callback<JSONObject>() {

                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Toast.makeText(ApplicationActivity.this, "Unsubscribed from LiveOak",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(ApplicationActivity.this, "Error trying to unsubscribe from LiveOak", Toast.LENGTH_SHORT).show();
                    Log.e(logTag, exception.getMessage(), exception);
                }
            });


            PushRegistrar push = ((ChatApplication) getApplication()).getRegistration();
            push.unregister(this, new Callback<Void>() {
                private static final long serialVersionUID = 1L;

                @Override
                public void onSuccess(Void ignore) {
                    Toast.makeText(ApplicationActivity.this, "Unregistered With UPS",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(ApplicationActivity.this, "Unregistration with UPS Failed", Toast.LENGTH_SHORT).show();
                    Log.e(logTag, exception.getMessage(), exception);
                }
            });

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
            liveOak.getResource(resourceURI, new Callback<JSONObject>() {

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
