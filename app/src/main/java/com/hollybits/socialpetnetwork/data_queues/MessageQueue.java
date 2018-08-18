package com.hollybits.socialpetnetwork.data_queues;

import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.MessageObservable;
import com.hollybits.socialpetnetwork.models.Message;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import io.paperdb.Paper;

public class MessageQueue implements MessageObservable{
    private static volatile MessageQueue instance;

    private static Queue<Message> messages;

    private MessageQueue() {
        messages = new ArrayDeque<>();
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
                    Paper.book().write(MainActivity.FROM_USER_ID,list);
                }

        );
        thread.start();
    }

    public int size(){
        return messages.size();
    }

    public boolean isEmpty(){
        return messages.isEmpty();
    }


    public Message poll(){
        return messages.poll();
    }


}
