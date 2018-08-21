package com.hollybits.socialpetnetwork.helper;

import java.util.ArrayList;
import java.util.List;

public interface MessageObservable {
    List<MessageObserver> observerList = new ArrayList<>();

    default void notifyObserver(){
        for (MessageObserver ob:
                observerList){
            ob.update();
        }
    }

    default void addObserver(MessageObserver ob){
        observerList.add(ob);
    }
    default void removeObserver(MessageObserver messageObserver){observerList.remove(messageObserver);}
}
