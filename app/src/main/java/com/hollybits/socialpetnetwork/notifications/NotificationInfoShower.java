package com.hollybits.socialpetnetwork.notifications;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Victor on 19.09.2018.
 */

public interface NotificationInfoShower {


    void show(Context context, RemoteMessage remoteMessage);


}
