package com.hollybits.socialpetnetwork.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor on 15.08.2018.
 */

public interface FriendshipRequestObservable {

    List<FriendShipRequestObserver> observerList = new ArrayList<>();


    default void notifyObservers(){
        for (FriendShipRequestObserver ob:
             observerList) {
            ob.update();
        }
    }

    default void addObserver(FriendShipRequestObserver ob){
        observerList.add(ob);
    }

}
