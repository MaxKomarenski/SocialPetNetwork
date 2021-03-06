package com.hollybits.socialpetnetwork.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hollybits.socialpetnetwork.Fragments.Account;
import com.hollybits.socialpetnetwork.Fragments.LostPets;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.data_queues.FriendShipRequestQueue;
import com.hollybits.socialpetnetwork.data_queues.MessageQueue;
import com.hollybits.socialpetnetwork.enums.NotificationType;
import com.hollybits.socialpetnetwork.helper.FriendDownloader;
import com.hollybits.socialpetnetwork.helper.MessageObservable;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;
import com.hollybits.socialpetnetwork.models.LostPet;
import com.hollybits.socialpetnetwork.models.Message;

import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

/**
 * Created by Victor on 05.08.2018.
 */

public class NotificationsAcceptor extends FirebaseMessagingService  {


    private static final String TAG = "MyFirebaseMsgService";






    /**
     * Called when message is received.
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     *
     *

     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.

        Log.d("DATA", ":");
        for(String s: remoteMessage.getData().keySet()){
            Log.d(s, remoteMessage.getData().get(s));
        }
        handleNotification(remoteMessage);
        // Check if message contains a notification payload.

        Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    // [END receive_message]


    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */

    /**
     * Handle time allotted to BroadcastReceivers.
     */


    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Log.d("TOKEN", token);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
 //    * @param messageBody FCM message body received.
     */
//    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        String channelId = getString(R.string.default_notification_channel_id);
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
//                        .setContentTitle("FCM Message")
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }





    private void handleNotification(RemoteMessage remoteMessage){


        if(Paper.book().read(MainActivity.CURRENTUSER) == null){
            return;
        }
        Map<String, String> data = remoteMessage.getData();
        NotificationType type = NotificationType.valueOf(data.get("type"));
        Log.d("NOTIFICATION ACCEPTOR", type.name());
        //showNotification(type, remoteMessage);
        switch (type){

            case PERSONALMESSAGE:{
                break;
            }
            case FRIEDSHIPREQUEST:{
                System.err.println("----------- i got friendship request --------------------");
                InfoAboutUserFriendShipRequest info = new InfoAboutUserFriendShipRequest();
                info.setId(Long.decode(data.get("id")));
                info.setName(data.get("name"));
                info.setSurname(data.get("surname"));
                info.setCity(data.get("city"));
                info.setCountry(data.get("country"));
                info.setPetName(data.get("petName"));
                info.setPetBreed(data.get("petBreed"));
                info.setRequestId(Long.decode(data.get("requestId")));
                FriendShipRequestQueue.getInstance().add(info);
                break;
            }
            case FRIENDSHIPACCEPTED:{
                FriendDownloader.downloadNewFriendAndAddHimToPaperBook(Long.decode(data.get("new_friend_id")));
                break;
            }

            case MESSAGESENT:{
                Message message = new Message();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                FragmentDispatcher.incCounter();
                message.setTimestamp(timestamp);
                message.setMessage(data.get("message_text"));
                message.setRead(false);
                message.setUserFrom(Long.decode(data.get("user_from")));
                message.setUserTo(Long.decode(data.get("user_to")));
                message.setFriendsId(Long.decode(data.get("friends_id")));
                System.err.println("user from --> " + Long.decode(data.get("user_from")));
                MessageQueue.getInstance().add(message);
                break;
            }
            case SOS:{
                if(LostPets.getInstance()!=null)
                    LostPets.getInstance().update();
                break;
            }

            case DELETEFRIEND:{
                deleteFriendFromPaperBook(Long.decode(data.get("user_from")));
            }

            default:{
                break;
            }
        }
    }

    private void showNotification(NotificationType type, RemoteMessage remoteMessage){
        Boolean state = Paper.book().read("CHANEL");

        if(state == null){
            createNotificationChannel();
        }
        Log.d(TAG, "SHOWING NOTIFICATION");
        NotificationViewer viewer = NotificationViewer.getInstance();
        viewer.showNotification(type, remoteMessage, this);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.chanel_name);
            String description = getString(R.string.chanel_discription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("SOCIAL PET NET ID", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Paper.book().write("CHANEL", true);
    }

    private void deleteFriendFromPaperBook(Long friendID){
        System.err.println("id friend who deleted you ---->    " + friendID);
        List<FriendInfo> friends = Paper.book().read(MainActivity.FRIEND_LIST);
        for(int i = 0; i < friends.size(); i++){
            if(friends.get(i).getId().equals(friendID)){
                friends.remove(i);
            }
        }
        Paper.book().delete(MainActivity.CONTACT_LIST);
        Paper.book(MainActivity.MESSAGE_BOOK).delete(friendID.toString());
        Paper.book().write(MainActivity.FRIEND_LIST, friends);
    }
}
