package com.hollybits.socialpetnetwork.data_queues;

import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.MessageObservable;
import com.hollybits.socialpetnetwork.models.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import io.paperdb.Paper;

public class MessageQueue implements MessageObservable{
    private static volatile MessageQueue instance;

    private static List<Message> messages;

    private MessageQueue() {
        messages = new ArrayList<>();
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
        messages.add(message);
        notifyObserver();
        Thread thread = new Thread(

                () -> {
                    Long id = Paper.book().read(MainActivity.FROM_USER_ID);
                    List<Message> list = Paper.book(MainActivity.MESSAGE_BOOK).read(id.toString());
                    list.add(message);
                    Paper.book().write(MainActivity.FROM_USER_ID, list);
                }

        );
        thread.start();
    }

    public Message get(Long id){
        Long userFromId = Paper.book().read(MainActivity.FROM_USER_ID);
        for (int i = 0; i < messages.size(); i++) {
            if (id.equals(userFromId)){
                Message m = messages.get(i);
                messages.remove(i);
                return m;
            }
        }
        return null;
    }

    public int size(){
        return messages.size();
    }

    public boolean isEmpty(){
        return messages.isEmpty();
    }


    public Message poll(){
        Message m = messages.get(0);
        messages.remove(0);
        return m;
    }


}
