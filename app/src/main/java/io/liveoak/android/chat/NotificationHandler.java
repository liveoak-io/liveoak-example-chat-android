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
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.liveoak.helper.LiveOak;

/**
 * Handler for incoming push notifications when the main application is not in focus.
 *
 */
public class NotificationHandler implements MessageHandler {

    public static final int NOTIFICATION_ID = 1201;

    @Override
    public void onMessage(final Context context, Bundle message) {

        final ChatApplication chatApplication = (ChatApplication) context.getApplicationContext();
        LiveOak liveOak = chatApplication.getLiveOak();

        String resourceURI = message.getString("io.liveoak.push.url");

        // the resourceURI returns from the server with the application name already prepended to it
        // since getResource will also prepend the application name, remove it now so its not added twice
        String resourceURISansApplication = resourceURI.substring(("/" + liveOak.APPLICATION_NAME).length());

        liveOak.getResource(resourceURISansApplication, new Callback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject resource) {
                String chatText = resource.optString("text");
                String sender = resource.optString("name");
                String id = resource.optString("id");

                Chat chat = new Chat(id, sender, chatText);

                List<Chat> pendingChats = chatApplication.getPendingChats();

                Set<String> chatIds = new HashSet<String>();
                for (Chat pendingChat: pendingChats) {
                    chatIds.add(pendingChat.getId());
                }

                if (chatIds.contains(chat.getId())) {
                    return;
                }
                pendingChats.add(chat);

                String title = new String();
                String text = new String();
                //single message, show the contents
                if (pendingChats.size() == 1) {
                    title = chat.getSender();
                    text = chat.getText();
                } else {
                    title = pendingChats.size() + " new messages";
                    text = "From : ";
                    Set<String> users = new HashSet<String>();
                    for (Chat pchat : pendingChats) {
                        users.add(pchat.getSender());
                    }

                    for (String user: users) {
                        text += user + ", ";
                    }

                    text = text.substring(0, text.length() - 2);
                }


                NotificationManager notificationManager = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);

                Intent intent = new Intent(context, ChatActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.small_icon)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon))
                                .setContentTitle(title)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                                .setContentText(text)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setTicker("New Message")

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
        // This event is currently ignored
    }

    @Override
    public void onError() {
        // This event is currently ignored
    }
}
