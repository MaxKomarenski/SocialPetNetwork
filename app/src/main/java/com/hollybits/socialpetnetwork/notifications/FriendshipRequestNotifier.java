package com.hollybits.socialpetnetwork.notifications;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import com.google.firebase.messaging.RemoteMessage;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;

/**
 * Created by Victor on 19.09.2018.
 */

public class FriendshipRequestNotifier implements NotificationInfoShower {
    @Override
    public void show(Context context, RemoteMessage remoteMessage) {
        RemoteViews notificationLayout = new RemoteViews(MainActivity.PACKAGE_NAME, R.layout.notification_small);
        notificationLayout.setTextViewText(R.id.notification_description, "Check your requests");
        notificationLayout.setTextViewText(R.id.notification_type_text_view, "New friendship request");
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
