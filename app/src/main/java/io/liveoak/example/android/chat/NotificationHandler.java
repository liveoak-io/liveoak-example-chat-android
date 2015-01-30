package io.liveoak.example.android.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import org.jboss.aerogear.android.unifiedpush.MessageHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mwringe on 23/01/15.
 */
public class NotificationHandler implements MessageHandler {

    public static final int NOTIFICATION_ID = 1201;

    public Set<String> pending = new HashSet<>();

    @Override
    public void onMessage(final Context context, Bundle message) {

        if (message.getString("io.liveoak.push.event").equals("created")) {

            if (!pending.contains(message.getString("io.liveoak.push.url"))) {
                pending.add(message.getString("io.liveoak.push.url"));
            }

            String title, text;
            int icon;
            if (pending.size() == 1) {
                title = context.getResources().getString(R.string.chat_notification_title_message);
                text = context.getResources().getString(R.string.chat_notification_text_message);
                icon = R.drawable.small_icon;
            } else {
                title = context.getResources().getString(R.string.chat_notification_title_messages);
                text = context.getResources().getString(R.string.chat_notification_text_messages);
                text = text.replace("${MESSAGE_COUNT}", "" + pending.size());
                icon = R.drawable.small_icon_chats;


            }

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(context, ChatActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(icon)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.liveoak_logo))

                            .setContentTitle(title)
                            .setContentText(text)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_VIBRATE);

            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
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