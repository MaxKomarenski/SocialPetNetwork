package com.hollybits.socialpetnetwork.models;

import java.sql.Timestamp;

public class Comment{
    private Long id;
    private Timestamp time;
    private Long userWhoLeaveAComment;
    private String text;
    private Long photoID;
    private String name;
    private String surname;

    public Comment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPhotoID() {
        return photoID;
    }

    public void setPhotoID(Long photoID) {
        this.photoID = photoID;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Long getUserWhoLeaveAComment() {
        return userWhoLeaveAComment;
    }

    public void setUserWhoLeaveAComment(Long userWhoLeaveAComment) {
        this.userWhoLeaveAComment = userWhoLeaveAComment;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
