package com.hollybits.socialpetnetwork.models;

import java.sql.Timestamp;

public class Message {

    private Long id;
    private String message;
    private Timestamp timestamp;
    private Long friendsId;
    private Long userTo;
    private Long userFrom;
    private boolean read;

    public Message() {
    }

    public Message(String message, Long userTo, Long userFrom, boolean read) {
        this.message = message;
        this.userTo = userTo;
        this.userFrom = userFrom;
        this.read = read;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(Long userFrom) {
        this.userFrom = userFrom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Long getUserTo() {
        return userTo;
    }

    public void setUserTo(Long user_to) {
        this.userTo = user_to;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Long getFriendsId() {
        return friendsId;
    }

    public void setFriendsId(Long friendsId) {
        this.friendsId = friendsId;
    }
}
