package com.hollybits.socialpetnetwork.notifications;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;
import com.hollybits.socialpetnetwork.data_queues.MessageQueue;
import com.hollybits.socialpetnetwork.enums.NotificationType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 19.09.2018.
 */

public class NotificationViewer {

    private static volatile NotificationViewer instance;
    private static Map<NotificationType, NotificationInfoShower> infoShowerMap;
    private NotificationViewer(){
        infoShowerMap = new HashMap<>();
        infoShowerMap.put(NotificationType.MESSAGESENT, new NewMessageNotifier());
        infoShowerMap.put(NotificationType.TEST, new TestNotifier());
        infoShowerMap.put(NotificationType.SOS, new SosRequestNotifier());
        infoShowerMap.put(NotificationType.FRIEDSHIPREQUEST, new FriendshipRequestNotifier());
    }

    public static NotificationViewer getInstance(){
        NotificationViewer localInstance = instance;
        if (localInstance == null){
            synchronized (MessageQueue.class){
                localInstance = instance;
                if(localInstance == null){
                    instance = localInstance = new NotificationViewer();
                }
            }
        }
        return localInstance;
    }

    public  void showNotification(NotificationType type, RemoteMessage remoteMessage, Context  context){
        if(infoShowerMap.containsKey(type))
            infoShowerMap.get(type).show(context, remoteMessage);
    }




}
