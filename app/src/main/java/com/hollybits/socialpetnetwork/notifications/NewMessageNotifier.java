package com.hollybits.socialpetnetwork.notifications;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import com.google.firebase.messaging.RemoteMessage;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.enums.NotificationType;

/**
 * Created by Victor on 19.09.2018.
 */

public class NewMessageNotifier implements NotificationInfoShower {
    @Override
    public void show(Context context, RemoteMessage remoteMessage) {
        RemoteViews notificationLayout = new RemoteViews(MainActivity.PACKAGE_NAME, R.layout.notification_small);
        String text = remoteMessage.getData().get("message_text");
        String fromName = remoteMessage.getData().get("name_from");
        if(text.length() < 27){
            notificationLayout.setTextViewText(R.id.notification_description, text);
        }else {
            //contact.getLastMessage();
            String message = String.format("%.26s", text + "...");
            notificationLayout.setTextViewText(R.id.notification_description, message);
        }
        notificationLayout.setTextViewText(R.id.notification_type_text_view, "New message for you from "+fromName);
        // RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);
        Notification customNotification = new NotificationCompat.Builder(context, "SOCIAL PET NET ID")
                .setSmallIcon(R.drawable.ic_dog)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, customNotification);
    }
}
