package io.liveoak.android.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.json.JSONObject;

import io.liveoak.helper.LiveOak;

/**
 * Created by mwringe on 28/02/14.
 */
public class NotificationHandler implements MessageHandler {

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessage(final Context context, Bundle message) {
        ChatApplication chatApplication = (ChatApplication) context.getApplicationContext();
        LiveOak liveOak = chatApplication.getLiveOak();

        String title = message.getString("title");
        String resourceURI = message.getString("io.liveoak.push.url");
        String event = message.getString("iol.liveoak.push.event");

        // the resourceURI returns from the server with the application name already prepended to it
        // since getResource will also prepend the application name, remove it now so its not added twice
        String resourceURISansApplication = resourceURI.substring(("/" + liveOak.APPLICATION_NAME).length());

        liveOak.getResource(resourceURISansApplication, new Callback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject resource) {
                String chatText = resource.optString("text");
                String sender = resource.optString("name");

                NotificationManager notificationManager = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);

                Intent intent = new Intent(context, ApplicationActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.small_icon)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.application_icon))
                                .setContentTitle(sender)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(chatText))
                                .setContentText(chatText)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)

                                .setVibrate(new long[]{0l, 250l, 250l, 250l});

                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }

            @Override
            public void onFailure(Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public void onDeleteMessage(Context context, Bundle arg0) {
        //
    }

    @Override
    public void onError() {
        //
    }
}
