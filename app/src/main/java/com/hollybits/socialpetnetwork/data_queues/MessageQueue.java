package com.hollybits.socialpetnetwork.data_queues;

import android.util.Log;

import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.MessageObservable;
import com.hollybits.socialpetnetwork.models.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import io.paperdb.Paper;

public class MessageQueue implements MessageObservable{
    private static volatile MessageQueue instance;

    private static Map<Long,Message> messages;

    private MessageQueue() {
        messages = new ConcurrentHashMap<>();
    }


    public static MessageQueue getInstance(){
        MessageQueue localInstance = instance;
        if (localInstance == null){
            synchronized (MessageQueue.class){
                localInstance = instance;
                if(localInstance == null){
                    instance = localInstance = new MessageQueue();
                }
            }
        }

        return localInstance;
    }

    public void add(Message message){
        messages.put(message.getUserFrom(), message);
        notifyObserver();
    }

    public Message get(Long id){
        Log.d("ACCEPTED ID FOR FINDIG:", id.toString());
        //for (int i = 0; i < messages.size(); i++) {
            //if (id.equals(messages.get(i).getUserFrom())){
                Message m = messages.get(id);
                System.err.println("message in queue --->  " + m.getMessage());
                messages.remove(id);
                //System.err.println("message in queue --->  " + messages.size());
                return m;
            //}
        //}
        //return null;
    }

    public int size(){
        return messages.size();
    }

    public boolean isEmpty(){
        return messages.isEmpty();
    }


//    public Message poll(){
//        Message m = messages.get(0);
//        messages.remove(0);
//        return m;
//    }


}
